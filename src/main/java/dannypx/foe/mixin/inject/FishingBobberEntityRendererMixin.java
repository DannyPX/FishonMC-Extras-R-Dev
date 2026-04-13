package dannypx.foe.mixin.inject;

import dannypx.foe.entity.FishingBobberEntityModel;
import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.config.Configs;
import dannypx.foe.interfaces.IFishingBobberEntity;
import dannypx.foe.interfaces.IFishingBobberEntityState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntityRenderer.class)
public abstract class FishingBobberEntityRendererMixin {
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

        ItemStack bait = ((IFishingBobberEntityState) fishingBobberEntityState).foer$getBaitStack();
        if(ConnectionHandler.instance().isOnServer()
                && Configs.rendererConfig.showBaitOnBobber.get()
                && bait != null
                && !bait.isEmpty()
                && !((IFishingBobberEntityState) fishingBobberEntityState).foer$isDisabledBait()
        ) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();

            matrixStack.push();
            matrixStack.translate(0, -0.4, 0);

            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-minecraftClient.getEntityRenderDispatcher().camera.getYaw()));

            minecraftClient.getItemRenderer().renderItem(
                    bait,
                    ItemDisplayContext.GROUND,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    matrixStack,
                    vertexConsumerProvider,
                    null,
                    0
            );

            matrixStack.pop();
        }
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/projectile/FishingBobberEntity;Lnet/minecraft/client/render/entity/state/FishingBobberEntityState;F)V", at = @At("TAIL"))
    private void injectUpdateRenderState(FishingBobberEntity fishingBobberEntity, FishingBobberEntityState fishingBobberEntityState, float f, CallbackInfo ci) {
        ((IFishingBobberEntityState) fishingBobberEntityState).foer$setBaitStack(((IFishingBobberEntity) fishingBobberEntity).foer$getBaitStack());
        ((IFishingBobberEntityState) fishingBobberEntityState).foer$setDisabledBait(((IFishingBobberEntity) fishingBobberEntity).foer$isDisabledBait());
    }
}
