package dannypx.foe;

import dannypx.foe.common.handler.fetch.BossBarHandler;
import dannypx.foe.common.handler.logic.*;
import dannypx.foe.common.handler.fetch.ClientPlayerHandler;
import dannypx.foe.common.handler.fetch.ScoreboardHandler;
import dannypx.foe.common.handler.fetch.TabHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.debug.DebugHandlerScreen;
import dannypx.foe.screens.hud.HudRenderHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class FishOnMCExtrasClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.onInit();

        ScreenEvents.BEFORE_INIT.register(this::onBeforeInitScreen);
        ClientPlayConnectionEvents.JOIN.register(this::onJoin);
        ClientPlayConnectionEvents.DISCONNECT.register(this::onLeave);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        HudLayerRegistrationCallback.EVENT.register(this::onHudRenderCallback);
        ScreenEvents.AFTER_INIT.register(this::onAfterInitScreen);
        UseItemCallback.EVENT.register(this::onUseItem);
    }

    private ActionResult onUseItem(PlayerEntity player, World world, Hand hand) {
        InventoryHandler.instance().onUseItem(hand);

        return ActionResult.PASS;
    }

    private void onAfterInitScreen(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        //TODO
        // Check if screen is instance of x (e.g. InventoryScreen), then call the register
        if(screen instanceof InventoryScreen) {
            // ScreenEvents.afterRender(screen).register(InventoryScreenHandler.instance().afterRender());
        }
    }

    private void onBeforeInitScreen(MinecraftClient minecraftClient, Screen screen, int scaledWidth, int scaledHeight) {
        ScreenKeyboardEvents.afterKeyPress(screen).register((screen1, key, modifiers, modifiers2) -> afterKeyPress(screen1, key, modifiers2));
    }

    private void afterKeyPress(Screen screen, int key, int modifiers) {
        if(screen instanceof DebugHandlerScreen debugHandlerScreen) {
            debugHandlerScreen.copyText(key, modifiers);
        }
    }

    private void onHudRenderCallback(LayeredDrawerWrapper layeredDrawerWrapper) {
        HudRenderHandler.instance().init(layeredDrawerWrapper);
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
        if(!LoadingHandler.instance().isError()
                && minecraftClient.getCurrentServerEntry() != null
                // Check if on server before ticking
                && ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
        ) {
            // Check if done loading
            if(LoadingHandler.instance().isLoadingDone()) {
                // Fetch
                if(Configs.handlerConfig.tabHandler.get()) TabHandler.instance().tick();
                if(Configs.handlerConfig.scoreboardHandler.get()) ScoreboardHandler.instance().tick();
                if(Configs.handlerConfig.clientPlayerHandler.get()) ClientPlayerHandler.instance().tick();
                if(Configs.handlerConfig.bossBarHandler.get()) BossBarHandler.instance().tick();
                if(Configs.handlerConfig.inventoryHandler.get()) InventoryHandler.instance().tick();

                // Logic
                if(Configs.handlerConfig.keyBindHandler.get()) KeyBindHandler.instance().tick();
                if(Configs.handlerConfig.catchingHandler.get()) CatchingHandler.instance().tick();

            } else {
                if(Configs.handlerConfig.loadingHandler.get()) LoadingHandler.instance().tick();
            }
        }
    }
}
