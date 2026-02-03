package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class PetNbtObject extends NbtObject {

    public static final String LEVEL = "level";
    public static final String XP_NEED = "xp_need";
    public static final String XP_CURRENT = "xp_cur";

    public PetNbtObject(NbtCompound nbtCompound, ItemStack itemStack) {
        super(nbtCompound, itemStack);
    }

    public int getLevel() {
        if(this.nbtCompound.contains(LEVEL)) {
            return this.nbtCompound.getInt(LEVEL);
        }
        return 0;
    }

    public float getProgress() {
        if(this.nbtCompound.contains(XP_NEED) && this.nbtCompound.contains(XP_CURRENT)) {
            float neededXP = this.nbtCompound.getFloat(XP_NEED);
            float currentXP = this.nbtCompound.getFloat(XP_CURRENT);
            return currentXP / neededXP;
        }
        return 0f;
    }

    public static PetNbtObject of(NbtCompound nbtCompound, ItemStack itemStack) {
        return new PetNbtObject(nbtCompound, itemStack);
    }

    public static PetNbtObject empty() {
        return new PetNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
