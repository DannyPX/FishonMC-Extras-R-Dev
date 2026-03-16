package dannypx.foe.common.interfaces;

import net.minecraft.item.ItemStack;

public interface IFishingBobberEntityState {
    ItemStack foer$getBaitStack();
    boolean foer$isDisabledBait();

    void foer$setBaitStack(ItemStack stack);
    void foer$setDisabledBait(boolean disabledBait);
}
