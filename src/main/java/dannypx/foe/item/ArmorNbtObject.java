package dannypx.foe.item;

import dannypx.foe.helper.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;

public class ArmorNbtObject extends NbtObject {
    public static final String ARMOR_ROLLS = "fish_bonus";
    public static final String ARMOR_ROLLS_UNLOCKED = "unlocked";
    public static final String ARMOR_ROLLS_ROLLED = "rolled";
    public static final String ARMOR_ROLLS_ROLLS = "rolls";
    public static final String IDENTIFIED = "identified";

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

    public static int calculateMoneyRolls(int rolls, int tier) {
        int adjustedRolls = rolls - 1;

        if (adjustedRolls <= 0) {
            return 0;
        }

        int cap = getTierCap(tier);
        int overflowValue = getOverflowValue(tier);

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

    private static int getTierCap(int tier) {
        if (tier < 4) return 15;
        if (tier == 4) return 18;
        return 23;
    }

    private static int getOverflowValue(int tier) {
        if (tier < 4) return 25000;
        if (tier == 4) return 32500;
        return 50000;
    }

    public static ArmorNbtObject of(@NotNull NbtCompound nbtCompound, @NotNull ItemStack itemStack) {
        return new ArmorNbtObject(nbtCompound, itemStack);
    }

    public static ArmorNbtObject empty() {
        return new ArmorNbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
