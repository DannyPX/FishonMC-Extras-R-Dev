package dannypx.foe.common.item;

import dannypx.foe.common.helper.ItemStackHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PetNbtObject extends NbtObject {

    public static final String LEVEL = "level";
    public static final String XP_NEED = "xp_need";
    public static final String XP_CURRENT = "xp_cur";
    public static final String RATING = "rating";

    public static final int RATING_LINE = 15;
    public static final int RATING_SIBLING = 2;

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
            return Math.min(currentXP / neededXP, 1f);
        }
        return 0f;
    }

    public Text getRatingText() {
        if(this.itemStack.get(DataComponentTypes.LORE) != null) {
            List<Text> textList = this.getLore();
            return textList.get(RATING_LINE).getSiblings().get(RATING_SIBLING);
        }
        return Text.empty();
    }

    public static PetNbtObject of(@NotNull NbtCompound nbtCompound, @NotNull ItemStack itemStack) {
        return new PetNbtObject(nbtCompound, itemStack);
    }

    public static PetNbtObject empty() {
        return new PetNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
