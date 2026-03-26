package dannypx.foe.mixin.accessor;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Invoker(value = "scheduleChunkRender")
    void foer$scheduleChunkRender(int x, int y, int z, boolean important);
}
