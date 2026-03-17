package dannypx.foe.mixin.inject;

import dannypx.foe.common.interfaces.IFishingBobberEntityState;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FishingBobberEntityState.class)
public class FishingBobberEntityStateMixin implements IFishingBobberEntityState {

    @Unique
    private ItemStack baitStack = ItemStack.EMPTY;

    @Unique
    private boolean disabledBait = false;

    @Override
    public ItemStack foer$getBaitStack() {
        return baitStack;
    }

    @Override
    public void foer$setBaitStack(ItemStack stack) {
        this.baitStack = stack;
    }

    @Override
    public boolean foer$isDisabledBait() {
        return disabledBait;
    }

    @Override
    public void foer$setDisabledBait(boolean disabledBait) {
        this.disabledBait = disabledBait;
    }
}
