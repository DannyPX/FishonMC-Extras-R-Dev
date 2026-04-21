package dannypx.foe.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ApiServices;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor
    ApiServices getApiServices();
}
