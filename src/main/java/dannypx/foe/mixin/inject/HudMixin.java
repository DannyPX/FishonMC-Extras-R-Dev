package dannypx.foe.mixin.inject;

import dannypx.foe.handler.fetch.TitleHandler;
import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import dannypx.foe.config.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudMixin {
    @Inject(method = "setTitle", at = @At("HEAD"))
    private void injectSetTitle(Component title, CallbackInfo ci) {
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.mixinConfig.hudMixinSetTitle.get()
        ) {
            TitleHandler.instance().setTitle(title);
        }
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"))
    private void injectSetSubtitle(Component subtitle, CallbackInfo ci) {
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.mixinConfig.hudMixinSetSubtitle.get()
        ) {
            TitleHandler.instance().setSubTitle(subtitle);
        }
    }

    @Inject(method = "extractSelectedItemName", at = @At("HEAD"),  cancellable = true)
    private void injectExtractSelectedItemName(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.hudMixinRenderSelectedItemName.get()
        ) {
            ci.cancel();
        }
    }

    @Inject(method = "extractScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void injectExtractScoreboardSidebar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.hudMixinRenderScoreBoardSidebar.get()
        ) {
            ci.cancel();
        }
    }

    @Inject(method = "extractItemHotbar", at = @At("HEAD"), cancellable = true)
    private void injectExtractItemHotbar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.hudMixinRenderItemHotbar.get()
        ) {
            ci.cancel();
        }
    }
}
