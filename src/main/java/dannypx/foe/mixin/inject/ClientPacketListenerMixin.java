package dannypx.foe.mixin.inject;

import com.llamalad7.mixinextras.sugar.Local;
import dannypx.foe.handler.logic.CatchingHandler;
import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.logic.CrewHandler;
import dannypx.foe.config.Configs;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    private List<UUID> uuids = new ArrayList<>();

    @Inject(method = "handlePlayerInfoUpdate", at = @At("TAIL"))
    private void injectHandlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.clientPacketListenerMixinHandlePlayerInfoUpdate.get()
        ) {
            for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
                if (packet.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)) {
                    CrewHandler.instance().onPlayerJoin(entry.profileId());
                }
            }
        }
    }

    @Inject(method = "handlePlayerInfoRemove", at = @At("TAIL"))
    private void injectHandlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.clientPacketListenerMixinHandlePlayerInfoRemove.get()
        ) {
            for (UUID uuid : packet.profileIds()) {
                CrewHandler.instance().onPlayerLeave(uuid);
            }
        }
    }

    @Inject(method = "handleSetEntityData", at = @At("TAIL"))
    private void injectPostAddEntitySoundInstance(ClientboundSetEntityDataPacket clientboundSetEntityDataPacket, CallbackInfo ci, @Local Entity entity) {
        if(entity instanceof Display.TextDisplay textDisplay
                && textDisplay.getText().getString().startsWith("CATCH SUMMARY")
                && !uuids.contains(textDisplay.getUUID())
        ) {
            String[] lines = textDisplay.getText().getString().split("\n");
            if(lines.length > 6) {
                CatchingHandler.instance().scanFishNameListener(lines[lines.length - 6]);
                CatchingHandler.instance().scanFishListener();

                uuids.add(textDisplay.getUUID());
            }
        }
    }
}
