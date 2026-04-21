package dannypx.foe.mixin.inject;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.logic.ConnectionHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBar.class)
public abstract class ExperienceBarMixin {
    @Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
    private void injectRenderBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
        ) {
            ci.cancel();
        }
    }

    @Inject(method = "renderAddons", at = @At("HEAD"), cancellable = true)
    private void injectRenderAddons(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
        ) {
            ci.cancel();
        }
    }
}
