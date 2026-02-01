package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class FishNbtObject extends NbtObject {

    private static final String FISH = "fish";

    public FishNbtObject(NbtCompound nbtCompound, ItemStack itemStack) {
        super(nbtCompound, itemStack);
    }

    public String getFish() {
        return this.nbtCompound.getString(FISH);
    }

    public static FishNbtObject of(NbtCompound nbtCompound, ItemStack itemStack) {
        return new FishNbtObject(nbtCompound, itemStack);
    }

    public static FishNbtObject empty() {
        return new FishNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
