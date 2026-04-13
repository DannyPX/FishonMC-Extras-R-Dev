package dannypx.foe.mixin.inject;

import dannypx.foe.handler.logic.LightHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Inject(method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I", at = @At("TAIL"), cancellable = true)
    private static void injectGetLightmapCoordinates(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if(!world.getBlockState(pos).isSolidBlock(world, pos)) {
            cir.setReturnValue(LightHandler.instance().calculateBobberLight(pos, cir.getReturnValue()));
        }
    }
}
