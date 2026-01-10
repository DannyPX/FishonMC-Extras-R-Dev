package dannypx.foe;

import dannypx.foe.common.handler.logic.KeyBindHandler;
import dannypx.foe.common.handler.fetch.ClientPlayerHandler;
import dannypx.foe.common.handler.fetch.ScoreboardHandler;
import dannypx.foe.common.handler.fetch.TabHandler;
import dannypx.foe.common.handler.logic.ConnectionHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.handler.io.DataFileHandler;
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
        this.onInit();
        ClientPlayConnectionEvents.JOIN.register(this::onJoin);
        ClientPlayConnectionEvents.DISCONNECT.register(this::onLeave);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
    }

    private void onInit() {
        KeyBindHandler.instance().init();
    }

    private void onLeave(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        ConnectionHandler.instance().onLeave();
        LoadingHandler.instance().onLeave();
    }

    private void onJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient minecraftClient) {
        ConnectionHandler.instance().init();
        //onJoin when on server
        if(ConnectionHandler.instance().isOnServer()) {
            ProfileDataHandler.instance().init();
            DataFileHandler.instance().init();
            LoadingHandler.instance().init();
        }
    }

    private void onEndClientTick(MinecraftClient minecraftClient) {
        if(minecraftClient.getCurrentServerEntry() != null
                // Check if on server before ticking
                && ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
        ) {
            // Check if done loading
            if(LoadingHandler.instance().isLoadingDone()) {
                if(Configs.dataHandlerConfig.keyBindHandler.get()) KeyBindHandler.instance().tick();
                if(Configs.dataHandlerConfig.tabHandler.get()) TabHandler.instance().tick();
                if(Configs.dataHandlerConfig.scoreboardHandler.get()) ScoreboardHandler.instance().tick();
                if(Configs.dataHandlerConfig.clientPlayerHandler.get()) ClientPlayerHandler.instance().tick();
            } else {
                if(Configs.dataHandlerConfig.loadingHandler.get()) LoadingHandler.instance().tick();
            }
        }
    }
}
