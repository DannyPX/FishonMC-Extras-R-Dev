package dannypx.foe.mixin.inject;

import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.logic.LightHandler;
import dannypx.foe.config.Configs;
import net.minecraft.world.chunk.light.BlockLightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLightStorage.class)
public abstract class BlockLightStorageMixin {
    @Inject(method = "getLight", at = @At("RETURN"), cancellable = true)
    private void injectGetLight(long blockPos, CallbackInfoReturnable<Integer> cir) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.blockLightStorageMixinGetLight.get()
        ) {
            cir.setReturnValue(LightHandler.instance().calculateBobberLight(blockPos, cir.getReturnValue()));
        }
    }
}
