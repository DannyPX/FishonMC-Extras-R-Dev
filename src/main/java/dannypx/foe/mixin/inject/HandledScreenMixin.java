package dannypx.foe.mixin.inject;

import dannypx.foe.common.handler.logic.ConnectionHandler;
import dannypx.foe.common.handler.logic.SearchHandler;
import dannypx.foe.config.Configs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;close()V"), cancellable = true)
    private void injectKeypressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.handledScreenMixinGroupKeyPressed.get()
                && SearchHandler.instance().isFocused()) {
            cir.setReturnValue(true);
        }
    }
}
