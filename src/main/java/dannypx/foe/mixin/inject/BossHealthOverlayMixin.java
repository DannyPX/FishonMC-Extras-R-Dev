package dannypx.foe.mixin.inject;

import dannypx.foe.handler.logic.ConnectionHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import dannypx.foe.config.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void injectRender(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.bossHealthOverlayMixinRender.get()
        ) {
            ci.cancel();
        }
    }
}
