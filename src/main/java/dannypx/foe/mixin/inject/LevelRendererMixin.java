package dannypx.foe.mixin.inject;

import dannypx.foe.handler.logic.LightHandler;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I", at = @At("TAIL"), cancellable = true)
    private static void injectGetLightColor(BlockAndTintGetter blockAndTintGetter, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if(!blockAndTintGetter.getBlockState(pos).isRedstoneConductor(blockAndTintGetter, pos)) {
            cir.setReturnValue(LightHandler.instance().calculateFishingHookLight(pos, cir.getReturnValue()));
        }
    }
}
