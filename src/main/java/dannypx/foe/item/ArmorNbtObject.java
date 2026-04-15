package dannypx.foe.item;

import dannypx.foe.helper.ItemStackHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArmorNbtObject extends NbtObject {
    public static final String ARMOR_ROLLS = "fish_bonus";
    public static final String ARMOR_ROLLS_UNLOCKED = "unlocked";
    public static final String ARMOR_ROLLS_ROLLED = "rolled";
    public static final String ARMOR_ROLLS_ROLLS = "rolls";
    public static final String IDENTIFIED = "identified";


    public static final int ARMOR_QUALITY_LINE = 2;
    public static final int ARMOR_QUALITY_SIBLING = 3;

    public ArmorNbtObject(NbtCompound nbtCompound, ItemStack itemStack) {
        super(nbtCompound, itemStack);
    }

    public NbtList getArmorRolls() {
        if(this.contains(ARMOR_ROLLS)) {
            return (NbtList)  this.nbtCompound.get(ARMOR_ROLLS);
        }
        return new NbtList();
    }

    public boolean isArmorRollUnlocked(int index) {
        if(!getArmorRolls().isEmpty()
                && index < this.getArmorRolls().size()
                && ((NbtCompound) this.getArmorRolls().get(index)).contains(ARMOR_ROLLS_UNLOCKED)
        ) {
            return ((NbtCompound) this.getArmorRolls().get(index)).getBoolean(ARMOR_ROLLS_UNLOCKED);
        }
        return false;
    }

    public boolean isArmorRollRolled(int index) {
        if(!getArmorRolls().isEmpty()
                && index < this.getArmorRolls().size()
                && ((NbtCompound) this.getArmorRolls().get(index)).contains(ARMOR_ROLLS_ROLLED)
        ) {
            return ((NbtCompound) this.getArmorRolls().get(index)).getBoolean(ARMOR_ROLLS_ROLLED);
        }
        return false;
    }

    public boolean isIdentified() {
        if(this.contains(IDENTIFIED)) {
            return this.getBoolean(IDENTIFIED);
        }
        return false;
    }

    public int getArmorRollRolls(int index) {
        if(!getArmorRolls().isEmpty()
                && index < this.getArmorRolls().size()
                && ((NbtCompound) this.getArmorRolls().get(index)).contains(ARMOR_ROLLS_ROLLS)
        ) {
            return ((NbtCompound) this.getArmorRolls().get(index)).getInt(ARMOR_ROLLS_ROLLS);
        }
        return 0;
    }

    public Text getQualityText() {
        if(this.itemStack.get(DataComponentTypes.LORE) != null
                && !this.getLore().isEmpty()
        ) {
            List<Text> textList = this.getLore();
            Text qualityText = textList.get(ARMOR_QUALITY_LINE).getSiblings().get(ARMOR_QUALITY_SIBLING);
            return qualityText.getString().contains("%") ? qualityText : textList.get(ARMOR_QUALITY_LINE).getSiblings().get(ARMOR_QUALITY_SIBLING + 1);
        }
        return Text.empty();
    }

    public static int calculateMoneyRolls(int rolls, int tier) {
        int adjustedRolls = rolls - 1;

        if (adjustedRolls <= 0) {
            return 0;
        }

        int cap = 15;
        int overflowValue = 25000;

        int amount = calculateSquareSum(Math.min(adjustedRolls, cap));

        if (adjustedRolls > cap) {
            amount += (adjustedRolls - cap) * overflowValue;
        }

        return amount;
    }

    private static int calculateSquareSum(int rolls) {
        int sum = 0;
        for (int i = 1; i <= rolls; i++) {
            sum += i * i * 100;
        }
        return sum;
    }

    public static ArmorNbtObject of(@NotNull NbtCompound nbtCompound, @NotNull ItemStack itemStack) {
        return new ArmorNbtObject(nbtCompound, itemStack);
    }

    public static ArmorNbtObject empty() {
        return new ArmorNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
