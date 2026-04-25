package dannypx.foe.mixin.inject;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.logic.ConnectionHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBarRenderer.class)
public abstract class ExperienceBarRendererMixin {
    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void injectExtractBackground(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.experienceBarRendererMixinRenderBackground.get()
        ) {
            ci.cancel();
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void injectExtractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.experienceBarRendererMixinRender.get()
        ) {
            ci.cancel();
        }
    }
}
