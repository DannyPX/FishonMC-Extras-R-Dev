package dannypx.foe.mixin.inject;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Inject(method = "isOpen", at = @At("HEAD"), cancellable = true)
    private void injectIsOpen(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
