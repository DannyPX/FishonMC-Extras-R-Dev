package dannypx.foe.handler.renderer;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.fetch.ClientPlayerHandler;
import dannypx.foe.handler.fetch.ScoreboardHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.handler.logic.RayCastHandler;
import dannypx.foe.handler.store.CustomHudDataHandler;
import dannypx.foe.helper.DrawHelper;
import dannypx.foe.item.NbtObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.*;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.screens.element.hud.*;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
    public void init(LayeredDrawerWrapper layeredDrawerWrapper) {
        addElements(layeredDrawerWrapper);
    }

    public void tick() {
        if(CustomHudDataHandler.instance().needsRenderUpdate) {
            customHudElements.clear();
            CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList.forEach((key, hud) -> customHudElements.add(Pair.of(key, new CustomHudElement(minecraftClient, hud, Text.literal(key)))));

            CustomHudDataHandler.instance().needsRenderUpdate = false;
        }
    }

    private void addElements(LayeredDrawerWrapper layeredDrawerWrapper) {
        elements.clear();
        elements.add(Pair.of("profile_hud", new ProfileElement(minecraftClient)));
        elements.add(Pair.of("location_hud", new LocationElement(minecraftClient)));
        elements.add(Pair.of("hotbar_hud", new HotbarElement(minecraftClient)));
        elements.add(Pair.of("pet_hud", new PetElement(minecraftClient)));
        elements.add(Pair.of("notifier_hud", new NotifierElement(minecraftClient)));
        elements.add(Pair.of("debug_field_hud", new _DebugField(minecraftClient)));

        elements.forEach(element -> layeredDrawerWrapper.attachLayerAfter(IdentifiedLayer.EXPERIENCE_LEVEL,
                Identifier.of(FishOnMCExtras.MOD_ID, element.value1()), (drawContext, tickCounter) -> {
                    if (Configs.mainConfig.enableMod.get()) element.value2().render(drawContext, tickCounter);
                }));

        layeredDrawerWrapper.attachLayerAfter(IdentifiedLayer.EXPERIENCE_LEVEL, Identifier.of(FishOnMCExtras.MOD_ID, "hud_screen"), (drawContext, renderTickCounter) -> {
            if (Configs.mainConfig.enableMod.get()) this.render(drawContext, renderTickCounter);
        });

        layeredDrawerWrapper.attachLayerAfter(IdentifiedLayer.SUBTITLES, Identifier.of(FishOnMCExtras.MOD_ID, "hud_screen_after_subtitles"), (drawContext, renderTickCounter) -> {
            if (Configs.mainConfig.enableMod.get()) this.renderAfterSubtitles(drawContext, renderTickCounter);
        });
    }

    private void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        customHudElements.forEach(element -> element.value2().render(drawContext, renderTickCounter));
    }

    private void renderAfterSubtitles(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        this.renderTooltip(drawContext);

        if(!LoadingHandler.instance().isLoadingDone()
                || ScoreboardHandler.instance().isNoScoreboard()
                || !ScoreboardHandler.instance().getLevel().getString().trim().equals(String.valueOf(ClientPlayerHandler.instance().getExperienceLevel()))
        ) this.renderLoading(drawContext);
    }



    private void renderLoading(DrawContext drawContext) {
        long time = System.currentTimeMillis();
        int dotCount = (int)((time / 1000) % 4);

        Text loadingText = Text.literal("Loading FOER" + ".".repeat(dotCount));

        DrawHelper.drawText(drawContext, minecraftClient.textRenderer, loadingText,
                minecraftClient.getWindow().getScaledWidth() - minecraftClient.textRenderer.getWidth(loadingText) - 8,
                minecraftClient.getWindow().getScaledHeight() - minecraftClient.textRenderer.fontHeight - 8,
                true, true, false, true
        );
    }

    private void renderTooltip(DrawContext drawContext) {
        if (Configs.mainConfig.enableMod.get()
                && LoadingHandler.instance().isLoadingDone()
                && RayCastHandler.instance().getItemFrameItem() != ItemStack.EMPTY) {
            Pair<Boolean, NbtObject> validatedItem = ValidateItem.isServerItem(RayCastHandler.instance().getItemFrameItem());
            if (validatedItem.value1()) {
                int itemX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2;
                int itemY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2;
                drawContext.drawItemTooltip(minecraftClient.textRenderer, validatedItem.value2().getItemStack(), itemX, itemY);
            }
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(

        );
    }
    //endregion
}
