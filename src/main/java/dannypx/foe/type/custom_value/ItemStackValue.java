package dannypx.foe.type.custom_value;

import net.minecraft.world.item.ItemStack;

public record ItemStackValue(ItemStack value) implements TrackerValue<ItemStack> {
    @Override
    public TrackerValue<ItemStack> setValue(ItemStack value) {
        return new ItemStackValue(value);
    }

    public static TrackerValue<ItemStack> of(ItemStack value) {
        return new ItemStackValue(value);
    }
}
