package dannypx.foe.mixin.inject;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.logic.LightHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.BlockAndLightGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightCoordsUtil.BrightnessGetter.class)
public interface LightCoordsUtilBrightnessGetterMixin {
    @Inject(method = "lambda$static$0", at = @At("TAIL"), cancellable = true)
    private static void injectGetBrightness(BlockAndLightGetter level, BlockPos blockPos, CallbackInfoReturnable<Integer> cir) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.levelRendererBrightnessGetterMixinStaticLambda0.get()
                && !level.getBlockState(blockPos).isSolidRender()) {
            cir.setReturnValue(LightHandler.instance().calculateFishingHookLight(blockPos, cir.getReturnValue()));
        }
    }
}
