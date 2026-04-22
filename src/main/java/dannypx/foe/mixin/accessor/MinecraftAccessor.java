package dannypx.foe.mixin.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.server.Services;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor
    Services getServices();
}
