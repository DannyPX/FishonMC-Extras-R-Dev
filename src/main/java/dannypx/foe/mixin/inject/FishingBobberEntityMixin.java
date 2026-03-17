package dannypx.foe.mixin.inject;

import dannypx.foe.common.item.FishingRodNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.interfaces.IFishingBobberEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin implements IFishingBobberEntity {
    @Shadow @Nullable public abstract PlayerEntity getPlayerOwner();

    @Unique
    private ItemStack baitStack = ItemStack.EMPTY;

    @Unique
    private boolean disabledBait = false;

    private FishingRodNbtObject currentFishingRod = FishingRodNbtObject.empty();

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectTick(CallbackInfo ci) {
        if(this.getPlayerOwner() != null
                && currentFishingRod.getItemStack() == ItemStack.EMPTY
        ) {
            Pair<Boolean, @Nullable FishingRodNbtObject> validatedFishingRod = ValidateItem.isFishingRod(this.getPlayerOwner().getMainHandStack());

            if(validatedFishingRod.value1()
                    && !ItemStack.areItemsAndComponentsEqual(currentFishingRod.getItemStack(), validatedFishingRod.value2().getItemStack())
            ) {
                currentFishingRod = validatedFishingRod.value2();
                if(!currentFishingRod.getTackleBox().isEmpty()) {
                    baitStack = currentFishingRod.getTackleBox().getFirst().getItemStack();
                    disabledBait = currentFishingRod.getDisableBait();
                }
            }
        }
    }

    @Override
    public ItemStack foer$getBaitStack() {
        return baitStack;
    }

    @Override
    public boolean foer$isDisabledBait() {
        return disabledBait;
    }
}
