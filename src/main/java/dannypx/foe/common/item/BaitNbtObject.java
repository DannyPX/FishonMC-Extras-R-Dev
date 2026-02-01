package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class BaitNbtObject extends NbtObject {
    public BaitNbtObject(NbtCompound nbtCompound, ItemStack itemStack) {
        super(nbtCompound, itemStack);
    }

    public static BaitNbtObject of(NbtCompound nbtCompound, ItemStack itemStack) {
        return new BaitNbtObject(nbtCompound, itemStack);
    }

    public static BaitNbtObject empty() {
        return new BaitNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
