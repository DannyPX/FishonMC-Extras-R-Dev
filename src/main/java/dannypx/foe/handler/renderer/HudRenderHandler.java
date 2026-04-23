package dannypx.foe.handler.renderer;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.fetch.LocalPlayerHandler;
import dannypx.foe.handler.fetch.ScoreboardHandler;
import dannypx.foe.handler.logic.ConnectionHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.handler.logic.HitResultHandler;
import dannypx.foe.handler.store.CustomHudDataHandler;
import dannypx.foe.helper.GuiGraphicsHelper;
import dannypx.foe.item.TagObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.*;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.screens.element.hud.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HudRenderHandler extends Handler {
    private static HudRenderHandler INSTANCE = new HudRenderHandler();

    public static HudRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new HudRenderHandler();
        }
        return INSTANCE;
    }

    //region Fields
    List<Pair<String, Element>> elements = new ArrayList<>();
    List<Pair<String, Element>> customHudElements = new ArrayList<>();
    //endregion

    //region Methods
    public void initializeHudRenderer() {
        renderElements();
    }

    public void tick() {
        if(CustomHudDataHandler.instance().needsRenderUpdate) {
            customHudElements.clear();
            CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList.forEach((key, hud) -> customHudElements.add(Pair.of(key, new CustomHudElement(minecraft, hud, Component.literal(key)))));

            CustomHudDataHandler.instance().needsRenderUpdate = false;
        }
    }

    private void renderElements() {
        if(elements.isEmpty()) {
            elements.add(Pair.of("profile_hud", new ProfileElement(minecraft)));
            elements.add(Pair.of("location_hud", new LocationElement(minecraft)));
            elements.add(Pair.of("hotbar_hud", new HotbarElement(minecraft)));
            elements.add(Pair.of("pet_hud", new PetElement(minecraft)));
            elements.add(Pair.of("notifier_hud", new NotifierElement(minecraft)));
            elements.add(Pair.of("debug_field_hud", new _DebugField(minecraft)));
        }

        LoggerHandler._debug("Register Default Elements");
        elements.forEach(element -> HudElementRegistry.attachElementBefore(VanillaHudElements.EXPERIENCE_LEVEL, Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, element.value1()), (guiGraphicsExtractor, deltaTracker) -> {
            if (Configs.mainConfig.enableMod.get()) element.value2().extractRenderState(guiGraphicsExtractor, deltaTracker);
        }));

        LoggerHandler._debug("Register Custom Elements");
        HudElementRegistry.attachElementBefore(VanillaHudElements.EXPERIENCE_LEVEL, Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "hud_screen"), (guiGraphicsExtractor, deltaTracker) -> {
            if (Configs.mainConfig.enableMod.get()) this.render(guiGraphicsExtractor, deltaTracker);
        });

        LoggerHandler._debug("Register Misc Elements");
        HudElementRegistry.attachElementBefore(VanillaHudElements.PLAYER_LIST, Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "hud_screen_after_subtitles"), (guiGraphicsExtractor, deltaTracker) -> {
            if (Configs.mainConfig.enableMod.get()) this.renderAfterSubtitles(guiGraphicsExtractor, deltaTracker);
        });
    }

    private void render(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker) {
        customHudElements.forEach(element -> element.value2().extractRenderState(guiGraphicsExtractor, deltaTracker));
    }

    private void renderAfterSubtitles(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker) {
        this.renderTooltip(guiGraphicsExtractor);

        if((!LoadingHandler.instance().isLoadingDone()
                || ScoreboardHandler.instance().isNoScoreboard()
                || !ScoreboardHandler.instance().getLevel().getString().trim().equals(String.valueOf(LocalPlayerHandler.instance().getExperienceLevel()))) &&
                ConnectionHandler.instance().isOnServer()
        ) this.renderLoading(guiGraphicsExtractor);
    }

    private void renderLoading(GuiGraphicsExtractor guiGraphicsExtractor) {
        long time = System.currentTimeMillis();
        int dotCount = (int)((time / 1000) % 4);

        Component loadingComponent = Component.literal("Loading FOER" + ".".repeat(dotCount));

        GuiGraphicsHelper.text(guiGraphicsExtractor, minecraft.font, loadingComponent,
                minecraft.getWindow().getGuiScaledWidth() - minecraft.font.width(loadingComponent) - 8,
                minecraft.getWindow().getGuiScaledHeight() - minecraft.font.lineHeight - 8,
                true, true, false, true
        );
    }

    private void renderTooltip(GuiGraphicsExtractor guiGraphicsExtractor) {
        if (Configs.mainConfig.enableMod.get()
                && LoadingHandler.instance().isLoadingDone()
                && HitResultHandler.instance().getItemFrameItem() != ItemStack.EMPTY) {
            Pair<Boolean, TagObject> validatedItem = ValidateItem.isServerItem(HitResultHandler.instance().getItemFrameItem());
            if (validatedItem.value1()) {
                int itemX = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
                int itemY = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2;
                guiGraphicsExtractor.setTooltipForNextFrame(minecraft.font, validatedItem.value2().getItemStack(), itemX, itemY);
            }
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(

        );
    }
    //endregion
}
