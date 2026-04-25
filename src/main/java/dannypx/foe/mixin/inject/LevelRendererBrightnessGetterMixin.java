package dannypx.foe.mixin.inject;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.logic.LightHandler;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.BrightnessGetter.class)
public interface LevelRendererBrightnessGetterMixin {
    @Inject(method = "lambda$static$0", at = @At("TAIL"), cancellable = true)
    private static void injectGetBrightness(BlockAndLightGetter blockAndTintGetter, BlockPos blockPos, CallbackInfoReturnable<Integer> cir) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.levelRendererBrightnessGetterMixinMethod_68890.get()
                && !blockAndTintGetter.getBlockState(blockPos).isSolidRender()) {
            cir.setReturnValue(LightHandler.instance().calculateFishingHookLight(blockPos, cir.getReturnValue()));
        }
    }
}
