package dannypx.foe.mixin.accessor;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Invoker(value = "setSectionDirty")
    void foer$setSectionDirty(int x, int y, int z, boolean important);
}
