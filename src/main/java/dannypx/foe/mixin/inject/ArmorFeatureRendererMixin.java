package dannypx.foe.mixin.inject;

import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.config.Configs;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<S extends BipedEntityRenderState> {
    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    public void injectRenderArmor(MatrixStack matrices, OrderedRenderCommandQueue queue, ItemStack stack, EquipmentSlot slot, int light, S state, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && (slot == EquipmentSlot.CHEST
                || slot == EquipmentSlot.LEGS
                || slot == EquipmentSlot.FEET)
                && Configs.rendererConfig.hideArmor.get()
                && Configs.mixinConfig.armorFeatureRendererRenderArmor.get()
        ) {
            ci.cancel();
        }
    }
}
