package dannypx.foe.mixin.inject;

import dannypx.foe.common.handler.logic.CrewHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onPlayerList", at = @At("TAIL"))
    private void injectOnPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
            if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                CrewHandler.instance().onPlayerJoin(entry.profileId());
            }
        }
    }

    @Inject(method = "onPlayerRemove", at = @At("TAIL"))
    private void injectOnPlayerRemove(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
        for (UUID uuid : packet.profileIds()) {
            CrewHandler.instance().onPlayerLeave(uuid);
        }
    }
}
