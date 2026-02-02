package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class PetNbtObject extends NbtObject {

    public PetNbtObject(NbtCompound nbtCompound, ItemStack itemStack) {
        super(nbtCompound, itemStack);
    }

    public static PetNbtObject of(NbtCompound nbtCompound, ItemStack itemStack) {
        return new PetNbtObject(nbtCompound, itemStack);
    }

    public static PetNbtObject empty() {
        return new PetNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
