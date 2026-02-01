package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;


public class FishingPartNbtObject extends NbtObject {
    public FishingPartNbtObject(NbtCompound nbtCompound, ItemStack itemStack) {
        super(nbtCompound, itemStack);
    }

    public static FishingPartNbtObject of(NbtCompound nbtCompound, ItemStack itemStack) {
        return new FishingPartNbtObject(nbtCompound, itemStack);
    }

    public static FishingPartNbtObject empty() {
        return new FishingPartNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
