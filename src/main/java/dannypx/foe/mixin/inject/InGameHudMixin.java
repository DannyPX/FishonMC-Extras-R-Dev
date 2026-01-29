package dannypx.foe.mixin.inject;

import dannypx.foe.common.handler.fetch.TitleHandler;
import dannypx.foe.common.handler.logic.ConnectionHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "setTitle", at = @At("HEAD"))
    private void injectSetTitle(Text title, CallbackInfo ci) {
        if(LoadingHandler.instance().isLoadingDone()) {
            TitleHandler.instance().setTitle(title);
        }
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"))
    private void injectSetSubtitle(Text subtitle, CallbackInfo ci) {
        if(LoadingHandler.instance().isLoadingDone()) {
            TitleHandler.instance().setSubTitle(subtitle);
        }
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"), cancellable = true)
    private void injectRenderScoreboardSidebar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void injectRenderExperienceBar(DrawContext context, int x, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void injectRenderExperienceLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()) {
            ci.cancel();
        }
    }
}
