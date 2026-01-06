package dannypx.foe;

import dannypx.foe.common.data.fetch.TabHandler;
import dannypx.foe.common.data.logic.ConnectionHandler;
import dannypx.foe.common.data.logic.LoadingHandler;
import dannypx.foe.common.data.store.ProfileHandler;
import dannypx.foe.common.io.DataFileHandler;
import dannypx.foe.config.Configs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class FishOnMCExtrasClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register(this::onJoin);
        ClientPlayConnectionEvents.DISCONNECT.register(this::onLeave);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);

    }

    private void onLeave(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        ConnectionHandler.instance().onLeave();
        LoadingHandler.instance().onLeave();
    }

    private void onJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient minecraftClient) {
        ConnectionHandler.instance().onJoin();
        //onJoin when on server
        if(ConnectionHandler.instance().isOnServer()) {
            ProfileHandler.instance().init();
            DataFileHandler.instance().init();
            LoadingHandler.instance().onJoin();
        }
    }

    private void onEndClientTick(MinecraftClient minecraftClient) {
        if(minecraftClient.getCurrentServerEntry() != null
                // Check if on server before ticking
                && ConnectionHandler.instance().isOnServer()
        ) {
            // Check if done loading
            if(LoadingHandler.instance().isLoadingDone()) {
                if(Configs.dataHandlerConfig.fetchHandlerSection.tabHandler.get()) TabHandler.instance().tick();
            } else {
                LoadingHandler.instance().tick();
            }
        }
    }
}
