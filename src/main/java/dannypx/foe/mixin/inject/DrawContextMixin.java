package dannypx.foe.mixin.inject;

import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.logic.KeyBindHandler;
import dannypx.foe.handler.renderer.ItemRendererHandler;
import dannypx.foe.config.Configs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow public abstract MatrixStack getMatrices();

    @Shadow public abstract int drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow);

    @Inject(method = "drawStackCount", at = @At("HEAD"), cancellable = true)
    private void drawStackCountInject(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.rendererConfig.useSmallStackCountNumber.get()
                && Configs.mixinConfig.drawContextAlterDrawStackCount.get()
        ) {
            ItemRendererHandler.instance().drawStackCount((DrawContext) (Object) this, textRenderer, stack, x, y);
            ci.cancel();
        }
    }

    @Inject(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    private void drawStackOverlayInject(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.drawContextAlterDrawStackOverlay.get()
        ) {
            ItemRendererHandler.instance().drawRarityMarker((DrawContext) (Object) this, textRenderer, stack, x, y);
            ItemRendererHandler.instance().drawSearchItem((DrawContext) (Object) this, stack, x, y);
            ItemRendererHandler.instance().drawPetItemEquipped((DrawContext) (Object) this, stack, x, y);
            if(KeyBindHandler.instance().isPressingInspect()) {
                ItemRendererHandler.instance().drawFishSize((DrawContext) (Object) this, textRenderer, stack, x, y);
                ItemRendererHandler.instance().drawPetRating((DrawContext) (Object) this, textRenderer, stack, x, y);
                ItemRendererHandler.instance().drawArmorQuality((DrawContext) (Object) this, textRenderer, stack, x, y);
            }
        }
    }
}
