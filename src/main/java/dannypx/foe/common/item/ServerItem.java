package dannypx.foe.common.item;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.constants.Rarity;
import dannypx.foe.common.constants.ServerItemId;
import dannypx.foe.common.minecraft.ItemStackHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public class ServerItem {
    public final String type;
    public final Rarity rarity;

    public ServerItem(String type, Rarity rarity) {
        this.type = type;
        this.rarity = rarity;
    }

    public static Pair<ServerItem, ServerItemId> getServerItem(ItemStack itemStack) {
        return null;
    }

    /**
     * Check whether it is a general item
     */
    public static boolean isServerItem(ItemStack itemStack) {
        Pair<Boolean, NbtCompound> item = isValidItem(itemStack);
        //isValidItem
        if(item.v1) {
            //is type
            if(isType(item.v2)) {
                return false;
            } else {
                return itemStack.getItem() == Items.FISHING_ROD;
            }
        } else {
            return false;
        }
    }

    /**
     * Check whether it is a specific item
     */
    public static boolean isServerItem(ItemStack itemStack, ServerItemId itemId) {
        Pair<Boolean, NbtCompound> item = isValidItem(itemStack);

        //isValidItem
        if(item.v1) {
            return switch(itemId) {
                case ServerItemId.FISHINGROD -> itemStack.getItem() == Items.FISHING_ROD;
                default -> false;
            };
        } else {
            return false;
        }
    }

    private static boolean isType(NbtCompound nbtCompound) {
        return nbtCompound != null && nbtCompound.contains("type");
    }

    private static Pair<Boolean, NbtCompound> isValidItem(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            NbtCompound nbtCompound = ItemStackHelper.getNbt(itemStack);
            return Pair.of(hasLore(itemStack)
                            && hasCustomData(itemStack)
                            && !isShopItem(nbtCompound),
                    nbtCompound);
        }
        return Pair.of(false, null);
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
