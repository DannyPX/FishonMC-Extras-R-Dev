package dannypx.foe.mixin.inject;

import dannypx.foe.common.entity.FishingBobberEntityModel;
import dannypx.foe.config.Configs;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntityRenderer.class)
public class FishingBobberEntityRendererMixin {
    @Unique
    private Model bobberModel;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectInit(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.bobberModel = new FishingBobberEntityModel<>(context.getPart(FishingBobberEntityModel.MODEL_LAYER));
    }

    @Inject(method = "vertex", at = @At("HEAD"), cancellable = true)
    private static void injectVertex(CallbackInfo ci) {
        if(Configs.rendererConfig.showNewBobber.get()
                && Configs.mixinConfig.fishingBobberEntityRendererVertex.get()
        ) {
            ci.cancel();
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/FishingBobberEntityState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("RETURN"))
    private void injectRender(FishingBobberEntityState fishingBobberEntityState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if(Configs.rendererConfig.showNewBobber.get()
                && Configs.mixinConfig.fishingBobberEntityRendererRender.get()
        ) {
            matrixStack.push();
            matrixStack.translate(0f, -0.0075f, 0f);
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(FishingBobberEntityModel.TEXTURE));
            this.bobberModel.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, Colors.WHITE);
            matrixStack.pop();
        }
    }
}
