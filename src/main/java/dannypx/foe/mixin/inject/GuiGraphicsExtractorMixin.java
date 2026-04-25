package dannypx.foe.mixin.inject;

import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.logic.KeyBindHandler;
import dannypx.foe.handler.renderer.ItemRendererHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import dannypx.foe.config.Configs;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {
    @Shadow public abstract Matrix3x2fStack pose();

    @Inject(method = "itemCount", at = @At("HEAD"), cancellable = true)
    private void itemCountInject(Font font, ItemStack itemStack, int x, int y, String countText, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.rendererConfig.useSmallStackCountNumber.get()
                && Configs.mixinConfig.guiGraphicsMixinRenderItemCount.get()
        ) {
            ItemRendererHandler.instance().drawStackCount((GuiGraphicsExtractor) (Object) this, font, itemStack, x, y);
            ci.cancel();
        }
    }

    @Inject(method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;pushMatrix()Lorg/joml/Matrix3x2fStack;"))
    private void itemDecorationsInject(Font font, ItemStack itemStack, int x, int y, String countText, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.guiGraphicsMixinRenderItemDecorations.get()
        ) {
            ItemRendererHandler.instance().drawRarityMarker((GuiGraphicsExtractor) (Object) this, font, itemStack, x, y);
            ItemRendererHandler.instance().drawSearchItem((GuiGraphicsExtractor) (Object) this, itemStack, x, y);
            ItemRendererHandler.instance().drawPetItemEquipped((GuiGraphicsExtractor) (Object) this, itemStack, x, y);
            if(KeyBindHandler.instance().isPressingInspect()) {
                ItemRendererHandler.instance().drawFishSize((GuiGraphicsExtractor) (Object) this, font, itemStack, x, y);
                ItemRendererHandler.instance().drawPetRating((GuiGraphicsExtractor) (Object) this, font, itemStack, x, y);
                ItemRendererHandler.instance().drawArmorQuality((GuiGraphicsExtractor) (Object) this, font, itemStack, x, y);
            }
        }
    }
}
