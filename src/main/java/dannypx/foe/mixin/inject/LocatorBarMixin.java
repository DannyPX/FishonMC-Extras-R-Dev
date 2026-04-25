package dannypx.foe.mixin.inject;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.logic.ConnectionHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocatorBarRenderer.class)
public abstract class LocatorBarMixin {
    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void injectExtractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.locatorBarMixinRenderBackground.get()
        ) {
            ci.cancel();
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void injectExtractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.locatorBarMixinRender.get()
        ) {
            ci.cancel();
        }
    }
}
