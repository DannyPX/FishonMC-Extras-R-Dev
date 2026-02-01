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
    public static Pair<Boolean, NbtCompound> isServerItem(ItemStack itemStack) {
        return isValidItem(itemStack);
    }

    public static Pair<Boolean, NbtCompound> isServerItem(ItemStack itemStack, Item itemType) {
        Pair<Boolean, NbtCompound> item = isValidItem(itemStack);

        //isValidItem
        if(item.v1() && itemStack.getItem() == itemType) {
            return item;
        } else {
            return Pair.ofFalse();
        }
    }

    private static Pair<Boolean, @Nullable NbtCompound> isValidItem(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            NbtCompound nbtCompound = ItemStackHelper.getNbt(itemStack);
            return Pair.of(hasLore(itemStack)
                            && hasCustomData(itemStack)
                            && !isShopItem(nbtCompound)
                            && (isType(nbtCompound) || isFish(nbtCompound) || isOther(itemStack)),
                    nbtCompound);
        }
        return Pair.ofFalse();
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

    public static Pair<Boolean, @Nullable NbtObject> isType(ItemStack itemStack) {
        if(!itemStack.isEmpty()
                && hasLore(itemStack)
                && hasCustomData(itemStack)) {
            NbtCompound nbtCompound = ItemStackHelper.getNbt(itemStack);
            return Pair.of(!isShopItem(nbtCompound) && isType(nbtCompound), NbtObject.of(nbtCompound, itemStack));
        }
        return Pair.ofFalse();
    }

    public static Pair<Boolean, @Nullable FishNbtObject> isFish(ItemStack itemStack) {
        if(!itemStack.isEmpty()
                && hasLore(itemStack)
                && hasCustomData(itemStack)) {
            NbtCompound nbtCompound = ItemStackHelper.getNbt(itemStack);
            return Pair.of(!isShopItem(nbtCompound) && isFish(nbtCompound), FishNbtObject.of(nbtCompound, itemStack));
        }
        return Pair.ofFalse();
    }

    public static Pair<Boolean, @Nullable FishingRodNbtObject> isFishingRod(ItemStack itemStack) {
        Pair<Boolean, NbtCompound> serverItem = isServerItem(itemStack, Items.FISHING_ROD);
        return serverItem.v1() ? Pair.of(true, FishingRodNbtObject.of(serverItem.v2(), itemStack)) : Pair.ofFalse();
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
