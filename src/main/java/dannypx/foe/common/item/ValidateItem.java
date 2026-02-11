package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ValidateItem {
    private static final String PET = "pet";

    public static Pair<Boolean, NbtObject> isServerItem(ItemStack itemStack) {
        return isValidItem(itemStack);
    }

    public static Pair<Boolean, NbtObject> isServerItem(ItemStack itemStack, Item itemType) {
        //isValidItem
        return Pair.of(itemStack.getItem() == itemType, isValidItem(itemStack).v2());
    }

    private static Pair<Boolean, NbtObject> isValidItem(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            NbtCompound nbtCompound = ItemStackHelper.getNbt(itemStack);
            return Pair.of(hasLore(itemStack)
                            && hasCustomData(itemStack)
                            && (isType(nbtCompound) || isFish(nbtCompound) || isOther(itemStack)),
                    NbtObject.of(nbtCompound, itemStack));
        }
        return Pair.ofFalse(NbtObject.of(new NbtCompound(), itemStack));
    }

    private static boolean isType(NbtCompound nbtCompound) {
        return nbtCompound != null && nbtCompound.contains("type");
    }

    private static boolean isFish(NbtCompound nbtCompound) {
        return nbtCompound != null && nbtCompound.contains("fish");
    }

    private static boolean isOther(ItemStack itemStack) {
        return itemStack.getItem() == Items.FISHING_ROD;
    }

    public static Pair<Boolean, NbtObject> isType(ItemStack itemStack) {
        if(!itemStack.isEmpty()
                && hasLore(itemStack)
                && hasCustomData(itemStack)) {
            NbtCompound nbtCompound = ItemStackHelper.getNbt(itemStack);
            return Pair.of(isType(nbtCompound), NbtObject.of(nbtCompound, itemStack));
        }
        return Pair.ofFalse(NbtObject.of(new NbtCompound(), itemStack));
    }

    public static Pair<Boolean, PetNbtObject> isPet(ItemStack itemStack) {
        Pair<Boolean, NbtObject> validatedItem = isType(itemStack);
        if(validatedItem.v1()) {
            return isPet(validatedItem.v2());
        }
        return Pair.ofFalse(PetNbtObject.of(validatedItem.v2().nbtCompound, validatedItem.v2().itemStack));
    }

    public static Pair<Boolean, PetNbtObject> isPet(NbtObject item) {
        return Pair.of(Objects.equals(item.getType(), PET), PetNbtObject.of(item.nbtCompound, item.itemStack));
    }

    public static Pair<Boolean, FishNbtObject> isFish(ItemStack itemStack) {
        if(!itemStack.isEmpty()
                && hasLore(itemStack)
                && hasCustomData(itemStack)) {
            NbtCompound nbtCompound = ItemStackHelper.getNbt(itemStack);
            return Pair.of(isFish(nbtCompound), FishNbtObject.of(nbtCompound, itemStack));
        }
        return Pair.ofFalse(FishNbtObject.of(new NbtCompound(), itemStack));
    }

    public static Pair<Boolean, FishingRodNbtObject> isFishingRod(ItemStack itemStack) {
        Pair<Boolean, NbtObject> serverItem = isServerItem(itemStack, Items.FISHING_ROD);
        return Pair.of(serverItem.v1(), FishingRodNbtObject.of(serverItem.v2().nbtCompound, serverItem.v2().getItemStack()));
    }

    private static boolean hasLore(ItemStack itemStack) {
        return itemStack.get(DataComponentTypes.LORE) != null;
    }

    private static boolean hasCustomData(ItemStack itemStack) {
        return itemStack.get(DataComponentTypes.CUSTOM_DATA) != null;
    }

    private static boolean isShopItem(NbtCompound nbtCompound) {
        return Objects.requireNonNull(nbtCompound).getBoolean("shopitem");
    }
}
