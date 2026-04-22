package dannypx.foe.interfaces;

import net.minecraft.world.item.ItemStack;

public interface IFishingHookRenderState {
    ItemStack foer$getBaitStack();
    boolean foer$isDisabledBait();

    void foer$setBaitStack(ItemStack stack);
    void foer$setDisabledBait(boolean disabledBait);
}
