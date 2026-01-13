package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public class ValidateItem {
    /**
     * Check whether it is a general item
     */
    public static Pair<Boolean, NbtCompound> isServerItem(ItemStack itemStack) {
        return isValidItem(itemStack);
    }

    /**
     * Check whether it is a specific item
     */
    public static boolean isServerItem(ItemStack itemStack, Item itemType) {
        Pair<Boolean, NbtCompound> item = isValidItem(itemStack);

        //isValidItem
        if(item.v1()) {
            return itemStack.getItem() == itemType;
        } else {
            return false;
        }
    }

    private static Pair<Boolean, NbtCompound> isValidItem(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            NbtCompound nbtCompound = ItemStackHelper.getNbt(itemStack);
            return Pair.of(hasLore(itemStack)
                            && hasCustomData(itemStack)
                            && !isShopItem(nbtCompound)
                            && (isType(nbtCompound) || isFish(nbtCompound) || isOther(itemStack)),
                    nbtCompound);
        }
        return Pair.nullableFalse();
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
