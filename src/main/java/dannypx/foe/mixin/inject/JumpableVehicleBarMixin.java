package dannypx.foe.mixin.inject;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.logic.ConnectionHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.JumpableVehicleBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JumpableVehicleBarRenderer.class)
public abstract class JumpableVehicleBarMixin {
    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void injectExtractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.jumpableVehicleBarMixinRenderBackground.get()
        ) {
            ci.cancel();
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void injectRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.jumpableVehicleBarMixinRender.get()
        ) {
            ci.cancel();
        }
    }
}
