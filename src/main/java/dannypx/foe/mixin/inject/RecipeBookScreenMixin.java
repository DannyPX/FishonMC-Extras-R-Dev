package dannypx.foe.mixin.inject;

import dannypx.foe.common.handler.logic.ConnectionHandler;
import dannypx.foe.config.Configs;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookScreen.class)
public class RecipeBookScreenMixin {
    @Inject(method = "addRecipeBook", at = @At("HEAD"), cancellable = true)
    private void injectAddRecipeBook(CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer() && Configs.mainConfig.enableMod.get()) {
            ci.cancel();
        }
    }
}
