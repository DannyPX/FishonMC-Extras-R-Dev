package dannypx.foe.mixin.inject;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dannypx.foe.config.Configs;
import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.renderer.HoldingFishRenderHandler;
import dannypx.foe.item.FishTagObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonHandsAndItemsRendererMixin {
    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float inverseArmHeight, float attackValue, HumanoidArm arm, PlayerRenderState playerState);

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void injectSubmitArmWithItem(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.rendererConfig.useBothHandsHoldingFishFirstPerson.get()
                && Configs.mixinConfig.avatarRenderMixinExtractRenderState.get()
        ) {
            if (hand != InteractionHand.MAIN_HAND || itemStack.isEmpty()) return;

            AvatarRenderState avatarRenderState = playerState.avatarRenderState;
            if (avatarRenderState == null) return;

            Pair<Boolean, FishTagObject> validatedFish = ValidateItem.isFish(itemStack);
            if (!validatedFish.value1()) return;

            float totalScale = HoldingFishRenderHandler.getTotalScale(validatedFish);

            if (!avatarRenderState.isInvisible) {
                for (HumanoidArm arm : HumanoidArm.values()) {
                    float side = arm == HumanoidArm.RIGHT ? 1.0f : -1.0f;
                    poseStack.pushPose();
                    poseStack.translate(
                            -side * HoldingFishRenderHandler.FISH_ARM_FIRST_PERSON_INWARD,
                            HoldingFishRenderHandler.FISH_ARM_FIRST_PERSON_UP,
                            -HoldingFishRenderHandler.FISH_ARM_FIRST_PERSON_FORWARD
                    );
                    this.renderPlayerArm(poseStack, submitNodeCollector, lightCoords, inverseArmHeight, 0.0F, arm, playerState);
                    poseStack.popPose();
                }
            }

            poseStack.pushPose();
            poseStack.translate(
                    HoldingFishRenderHandler.FISH_FIRST_PERSON_X,
                    HoldingFishRenderHandler.FISH_FIRST_PERSON_Y - inverseArmHeight * HoldingFishRenderHandler.FIRST_PERSON_FISH_EQUIP_DROP,
                    HoldingFishRenderHandler.FISH_FIRST_PERSON_Z
            );
            poseStack.rotateDegrees(Axis.ZP, HoldingFishRenderHandler.FISH_ROLL);
            poseStack.scale(totalScale, totalScale, totalScale);
            ItemStackRenderState guiRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                    guiRenderState,
                    itemStack,
                    ItemDisplayContext.GUI,
                    Minecraft.getInstance().level,
                    Minecraft.getInstance().player,
                    Minecraft.getInstance().player.getId() + ItemDisplayContext.GUI.ordinal()
            );
            guiRenderState.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();

            ci.cancel();
        }
    }
}
