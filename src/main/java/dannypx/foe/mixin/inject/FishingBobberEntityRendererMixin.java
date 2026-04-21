package dannypx.foe.mixin.inject;

import dannypx.foe.entity.FishingBobberEntityModel;
import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.config.Configs;
import dannypx.foe.interfaces.IFishingBobberEntity;
import dannypx.foe.interfaces.IFishingBobberEntityState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
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
    private Model<FishingBobberEntityState> bobberModel;

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

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/FishingBobberEntityState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("RETURN"))
    private void injectRender(FishingBobberEntityState fishingBobberEntityState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if(Configs.rendererConfig.showNewBobber.get()
                && Configs.mixinConfig.fishingBobberEntityRendererRender.get()
        ) {
            matrixStack.push();
            matrixStack.translate(0f, -0.0075f, 0f);
            orderedRenderCommandQueue.submitModel(this.bobberModel, fishingBobberEntityState, matrixStack, FishingBobberEntityModel.RENDER_LAYER, fishingBobberEntityState.light, OverlayTexture.DEFAULT_UV, fishingBobberEntityState.outlineColor, null);
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
            if(minecraftClient.getEntityRenderDispatcher().camera != null) {
                matrixStack.push();
                matrixStack.translate(0, -0.4, 0);

                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-minecraftClient.getEntityRenderDispatcher().camera.getYaw()));

                ItemModelManager itemModelManager = minecraftClient.getItemModelManager();
                KeyedItemRenderState itemRenderState = new KeyedItemRenderState();

                itemModelManager.clearAndUpdate(itemRenderState, bait, ItemDisplayContext.GROUND, minecraftClient.world, null, 0);
                itemRenderState.render(matrixStack, orderedRenderCommandQueue, fishingBobberEntityState.light, OverlayTexture.DEFAULT_UV, Colors.BLACK);

                matrixStack.pop();
            }
        }
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/projectile/FishingBobberEntity;Lnet/minecraft/client/render/entity/state/FishingBobberEntityState;F)V", at = @At("TAIL"))
    private void injectUpdateRenderState(FishingBobberEntity fishingBobberEntity, FishingBobberEntityState fishingBobberEntityState, float f, CallbackInfo ci) {
        ((IFishingBobberEntityState) fishingBobberEntityState).foer$setBaitStack(((IFishingBobberEntity) fishingBobberEntity).foer$getBaitStack());
        ((IFishingBobberEntityState) fishingBobberEntityState).foer$setDisabledBait(((IFishingBobberEntity) fishingBobberEntity).foer$isDisabledBait());
    }
}
