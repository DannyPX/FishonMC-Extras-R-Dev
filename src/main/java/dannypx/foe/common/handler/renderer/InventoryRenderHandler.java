package dannypx.foe.common.handler.renderer;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.handler.store.ConstantDataHandler;
import dannypx.foe.common.handler.store.StatsDataHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.*;
import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.screens.widget.StatListWidget;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class InventoryRenderHandler extends Handler {
    private static InventoryRenderHandler INSTANCE = new InventoryRenderHandler();

    public static InventoryRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryRenderHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final TextRenderer textRenderer = minecraftClient.textRenderer;

    List<Pair<String, Element>> elements = new ArrayList<>();

    private StatListWidget statList;
    private final int STAT_WIDTH = 160;

    private final int INVENTORY_TRANSLATION = 94;
    private final int INVENTORY_TOP = 83;
    private final int INVENTORY_HEIGHT = 166;
    //endregion

    //region Methods
    public void init(Screen screen) {
        this.initElements();
        this.initWidgets(screen);
    }

    public void render(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        if(!LoadingHandler.instance().isLoadingDone()
                && Configs.inventoryScreenConfig.showStatsElement.get()
                && Configs.mainConfig.enableMod.get()
        ) {
            return;
        }

        elements.forEach(element -> element.v2().render(drawContext, MinecraftClient.getInstance().getRenderTickCounter()));
        if(this.statList != null) {
            this.statList.render(drawContext, mouseX, mouseY, tickDelta);
        }
        this.renderStatBoxHeaderText(drawContext);
    }

    private void renderStatBoxHeaderText(DrawContext drawContext) {
        Text headerText = Text.literal("Fishing Statistics").formatted(Formatting.BOLD);
        int headerWidth = textRenderer.getWidth(
                Text.literal(TextHelper.smallText(headerText.getString())).setStyle(headerText.getStyle())
        );

        DrawHelper.drawText(drawContext, textRenderer, headerText,
                minecraftClient.getWindow().getScaledWidth() / 2 + INVENTORY_TRANSLATION
                        + (STAT_WIDTH - ((STAT_WIDTH / 4) / 3)) / 2 - headerWidth / 2,
                minecraftClient.getWindow().getScaledHeight() / 2
                        - INVENTORY_TOP - 10 + 4 - textRenderer.fontHeight / 2,
                true, true, true, true
        );
    }

    private void initWidgets(Screen screen) {
        if(!LoadingHandler.instance().isLoadingDone()
                && Configs.inventoryScreenConfig.showStatsElement.get()
                && Configs.mainConfig.enableMod.get()
        ) {
            return;
        }

        List<ClickableWidget> widgets = new ArrayList<>();

        widgets.add(getStatList());

        widgets.forEach(Screens.getButtons(screen)::add);
    }

    public void onMouseScrolled(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(this.statList.isMouseOver(mouseX, mouseY)) {
            this.statList.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
    }

    private ClickableWidget getStatList() {
        statList = new StatListWidget(
                minecraftClient,
                STAT_WIDTH,
                INVENTORY_HEIGHT - 16 * 3,
                minecraftClient.getWindow().getScaledWidth() / 2 + INVENTORY_TRANSLATION + ScreenConstants.PADDING_HALF,
                minecraftClient.getWindow().getScaledHeight() / 2 - INVENTORY_TOP + 16 * 2,
                16
        );

        // Fish Data
        statList.addEntry(new StatListWidget.StatEntry(Text.literal("-- Fishes --").styled(style -> style.withBold(true)), true));
        statList.addEntry(new StatListWidget.StatEntry(Text.literal(""), Text.literal("Caught").formatted(Formatting.GRAY), Text.empty(), Text.literal("Dryst.").formatted(Formatting.GRAY), List.of(), false));
        statList.addEntry(new StatListWidget.StatEntry(
                Text.literal(""),
                Text.literal("Total"),
                TextHelper.literal(StatsDataHandler.instance().getStatsData().fishTotal),
                Text.literal(""),
                List.of(),
                false));
        statList.addEntry(new StatListWidget.StatEntry(Text.literal(""), true));
        StatsDataHandler.instance().getStatsData().fishData.forEach((category, fieldStats) -> {
            fieldStats.forEach((field, stat) -> {
                if(Objects.equals(field, "normal")) return;
                statList.addEntry(new StatListWidget.StatEntry(Text.literal(category),
                        ConstantDataHandler.instance().getConstantData().fishData.getOrDefault(category, new HashMap<>()).getOrDefault(field, Text.literal(field)),
                        TextHelper.literal(stat.amount()),
                        TextHelper.literal(StatsDataHandler.instance().getStatsData().fishTotal - stat.caughtOn()),
                        List.of(),
                        false
                ));
            });
            statList.addEntry(new StatListWidget.StatEntry(Text.literal(""), true));
        });

        statList.addEntry(new StatListWidget.StatEntry(Text.literal(""), true));
        statList.addEntry(new StatListWidget.StatEntry(Text.literal("-- Pets --").styled(style -> style.withBold(true)), true));
        statList.addEntry(new StatListWidget.StatEntry(Text.literal(""), Text.literal("Caught").formatted(Formatting.GRAY), Text.empty(), Text.literal("Dryst.").formatted(Formatting.GRAY), List.of(), false));
        statList.addEntry(new StatListWidget.StatEntry(
                Text.literal(""),
                Text.literal("Total"),
                TextHelper.literal(StatsDataHandler.instance().getStatsData().petTotal),
                Text.literal(""),
                List.of(),
                false));
        statList.addEntry(new StatListWidget.StatEntry(Text.literal(""), true));
        StatsDataHandler.instance().getStatsData().petData.forEach((category, fieldStats) -> {
            fieldStats.forEach((field, stat) -> {
                statList.addEntry(new StatListWidget.StatEntry(Text.literal(category),
                        ConstantDataHandler.instance().getConstantData().petData.getOrDefault(category, new HashMap<>()).getOrDefault(field, Text.literal(field)),
                        TextHelper.literal(stat.amount()),
                        TextHelper.literal(StatsDataHandler.instance().getStatsData().fishTotal - stat.caughtOn()),
                        List.of(),
                        false
                ));
            });
            statList.addEntry(new StatListWidget.StatEntry(Text.literal("").formatted(Formatting.GRAY), true));
        });

        statList.addEntry(new StatListWidget.StatEntry(Text.literal("").formatted(Formatting.GRAY), true));
        statList.addEntry(new StatListWidget.StatEntry(Text.literal("-- Other Items --").styled(style -> style.withBold(true)), true));
        statList.addEntry(new StatListWidget.StatEntry(Text.literal(""), Text.literal("Caught").formatted(Formatting.GRAY), Text.empty(), Text.literal("Dryst.").formatted(Formatting.GRAY), List.of(), false));
        StatsDataHandler.instance().getStatsData().itemData.forEach((category, stat) -> {
            statList.addEntry(new StatListWidget.StatEntry(Text.literal(category),
                    Text.literal(category),
                    TextHelper.literal(stat.amount()),
                    TextHelper.literal(StatsDataHandler.instance().getStatsData().fishTotal - stat.caughtOn()),
                    ConstantDataHandler.instance().getConstantData().itemData.getOrDefault(category, List.of()),
                    false
            ));
        });

        statList.setScrollY(16);
        return statList;
    }

    private void initElements() {
        elements.clear();
        elements.add(Pair.of("header_box", new BoxElement(minecraftClient,
                minecraftClient.getWindow().getScaledWidth() / 2 + INVENTORY_TRANSLATION + (STAT_WIDTH - ((STAT_WIDTH / 4) / 3)) / 2 - 65,
                minecraftClient.getWindow().getScaledHeight() / 2 - INVENTORY_TOP - 20 + 4,
                130, 20, true)));

        elements.add(Pair.of("stat_box", new BoxElement(minecraftClient,
                minecraftClient.getWindow().getScaledWidth() / 2 + INVENTORY_TRANSLATION,
                minecraftClient.getWindow().getScaledHeight() / 2 - INVENTORY_TOP,
                STAT_WIDTH - ((STAT_WIDTH / 4) / 3), INVENTORY_HEIGHT, false)));
        // Add elements here
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
