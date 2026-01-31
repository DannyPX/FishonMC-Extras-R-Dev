package dannypx.foe.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class FishNbtObject extends NbtObject {

    private static final String FISH = "fish";
    private static final String WEIGHT = "weight";

    public FishNbtObject(NbtCompound nbtCompound, ItemStack itemStack) {
        super(nbtCompound, itemStack);
    }

    public String getFish() {
        return this.nbtCompound.getString(FISH);
    }

    public float getWeight() {
        return this.nbtCompound.getFloat(WEIGHT);
    }

    public static FishNbtObject of(NbtCompound nbtCompound, ItemStack itemStack) {
        return new FishNbtObject(nbtCompound, itemStack);
    }
}
