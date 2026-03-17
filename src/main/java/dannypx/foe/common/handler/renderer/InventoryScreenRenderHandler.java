package dannypx.foe.common.handler.renderer;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.ScreenHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.handler.store.ConstantDataHandler;
import dannypx.foe.common.handler.store.CustomButtonDataHandler;
import dannypx.foe.common.handler.store.StatsDataHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.CustomButtonMakerScreen;
import dannypx.foe.screens.MainScreen;
import dannypx.foe.screens.element.*;
import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.screens.widget.SmallButtonWidget;
import dannypx.foe.screens.widget.StatListWidget;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class InventoryScreenRenderHandler extends ScreenHandler {
    private static InventoryScreenRenderHandler INSTANCE = new InventoryScreenRenderHandler();

    public static InventoryScreenRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryScreenRenderHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final TextRenderer textRenderer = minecraftClient.textRenderer;

    List<Pair<String, Element>> elements = new ArrayList<>();
    List<ClickableWidget> widgets = new ArrayList<>();

    SmallButtonWidget buttonMenuToggle;
    List<ClickableWidget> buttons = new ArrayList<>();
    Pair<String, Element> buttonBox;
    final int buttonsPerRow = 8;
    int buttonBoxRows = 0;
    int buttonSize = 18;
    int buttonSizeAndPadding = 20;

    private StatListWidget statList;
    private final int STAT_WIDTH = 160;

    private final int INVENTORY_TRANSLATION = 94;
    private final int INVENTORY_TOP = 83;
    private final int INVENTORY_HEIGHT = 166;
    //endregion

    //region Methods
    public void init(Screen screen) {
        buttonBoxRows = (CustomButtonDataHandler.instance().getCustomButtonData().buttonList.getOrDefault(FishOnMCExtras.INVENTORY_SCREEN, Pair.of(new ArrayList<>(), false)).value1().size() + buttonsPerRow - 1) / buttonsPerRow;

        this.initElements();
        this.initWidgets(screen);
    }

    public void render(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.inventoryScreenConfig.showStatsElement.get()
                && Configs.mainConfig.enableMod.get()
        ) {
            elements.forEach(element -> element.value2().render(drawContext, MinecraftClient.getInstance().getRenderTickCounter()));
            if(this.statList != null) {
                this.statList.render(drawContext, mouseX, mouseY, tickDelta);
            }
            this.renderStatBoxHeaderText(drawContext);
            this.renderButtonBoxText(drawContext);
        }
    }

    private void renderButtonBoxText(DrawContext drawContext) {
        if(CustomButtonDataHandler.instance().getCustomButtonData().buttonList.getOrDefault(FishOnMCExtras.INVENTORY_SCREEN, Pair.of(new ArrayList<>(), false)).value2()
                && buttonBoxRows == 0
        ) {
            drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("You have no custom buttons").formatted(Formatting.ITALIC, Formatting.GRAY),
                    minecraftClient.getWindow().getScaledWidth() / 2,
                    minecraftClient.getWindow().getScaledHeight() / 2 + INVENTORY_HEIGHT / 2 + 8 - textRenderer.fontHeight / 2,
                    0xFFFFFF);
        }
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
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.mainConfig.enableMod.get()
        ) {
            widgets.clear();
            buttons.clear();

            if(Configs.inventoryScreenConfig.showStatsElement.get()) widgets.add(getStatList());

            widgets.add(new SmallButtonWidget(
                    minecraftClient.getWindow().getScaledWidth() / 2 + 66,
                    minecraftClient.getWindow().getScaledHeight() / 2 - 23,
                    14, 14,
                    "F",
                    Tooltip.of(Text.literal("Open FOER Menu")),
                    Text.literal("FOER Button"),
                    (button) -> {
                        minecraftClient.setScreen(new MainScreen(minecraftClient.currentScreen));
                    }
            ));

            widgets.add(new SmallButtonWidget(
                    minecraftClient.getWindow().getScaledWidth() / 2 + 50,
                    minecraftClient.getWindow().getScaledHeight() / 2 - 23,
                    14, 14,
                    "B",
                    Tooltip.of(Text.literal("Edit Inventory Buttons")),
                    Text.literal("Edit Inventory Buttons"),
                    (button) -> {
                        minecraftClient.setScreen(new CustomButtonMakerScreen(minecraftClient.currentScreen, FishOnMCExtras.INVENTORY_SCREEN));
                    }
            ));

            for (int i = 0; i < CustomButtonDataHandler.instance().getCustomButtonData().buttonList.getOrDefault(FishOnMCExtras.INVENTORY_SCREEN, Pair.of(new ArrayList<>(), false)).value1().size(); i++) {
                CustomButtonDataHandler.CustomButton button = CustomButtonDataHandler.instance().getCustomButtonData().buttonList.getOrDefault(FishOnMCExtras.INVENTORY_SCREEN, Pair.of(new ArrayList<>(), false)).value1().get(i);
                if(button.showButton) {
                    int row = i / buttonsPerRow;
                    int column = i % buttonsPerRow;

                    MutableText tooltip = TextHelper.parseLegacyWithStyle(button.name.replace("&", "§")).value1();

                    if(!button.description.isBlank()) {
                        tooltip.append(Text.literal("\n\n")).append(TextHelper.parseLegacyWithStyle(button.description.replace("&", "§")).value1());
                    }

                    buttons.add(new SmallButtonWidget(
                            minecraftClient.getWindow().getScaledWidth() / 2 - 84 + 5 + (column * 20) + 1,
                            minecraftClient.getWindow().getScaledHeight() / 2 + INVENTORY_HEIGHT / 2 + (row * 20) + 1,
                            buttonSize, buttonSize,
                            button.icon,
                            Tooltip.of(tooltip),
                            Text.literal(button.name),
                            (buttonWidget) -> {
                                if(minecraftClient.player != null) {
                                    minecraftClient.player.networkHandler.sendChatCommand(button.action.substring(1));
                                }
                            }
                    ));
                }
            }

            if(CustomButtonDataHandler.instance().getCustomButtonData().buttonList.getOrDefault(FishOnMCExtras.INVENTORY_SCREEN, Pair.of(new ArrayList<>(), false)).value2()) {
                buttonMenuToggle = getCloseButtonMenuButton(screen);
                widgets.addAll(buttons);
            } else {
                buttonMenuToggle = getOpenButtonMenuButton(screen);
            }
            widgets.add(buttonMenuToggle);

            widgets.forEach(Screens.getButtons(screen)::add);
        }
    }

    public SmallButtonWidget getOpenButtonMenuButton(Screen screen) {
        return new SmallButtonWidget(
                minecraftClient.getWindow().getScaledWidth() / 2 - 20,
                minecraftClient.getWindow().getScaledHeight() / 2 + INVENTORY_HEIGHT / 2 - 3,
                40, 12,
                "⏷",
                Tooltip.of(Text.literal("Open Button Menu")),
                Text.literal("Open Button Menu Button"),
                (button) -> {
                    Screens.getButtons(screen).remove(buttonMenuToggle);
                    buttonMenuToggle = getCloseButtonMenuButton(screen);
                    Screens.getButtons(screen).add(buttonMenuToggle);

                    elements.add(buttonBox);
                    Screens.getButtons(screen).addAll(buttons);

                    CustomButtonDataHandler.instance().updateButton(FishOnMCExtras.INVENTORY_SCREEN, true);
                }
        );
    }

    public SmallButtonWidget getCloseButtonMenuButton(Screen screen) {
        return new SmallButtonWidget(
                minecraftClient.getWindow().getScaledWidth() / 2 - 20,
                buttonBoxRows != 0 ? minecraftClient.getWindow().getScaledHeight() / 2 + INVENTORY_HEIGHT / 2 + (buttonBoxRows * 20) + 1
                : minecraftClient.getWindow().getScaledHeight() / 2 + INVENTORY_HEIGHT / 2 + 17,
                40, 12,
                "⏶",
                Tooltip.of(Text.literal("Close Button Menu")),
                Text.literal("Close Button Menu Button"),
                (button) -> {
                    Screens.getButtons(screen).remove(buttonMenuToggle);
                    buttonMenuToggle = getOpenButtonMenuButton(screen);
                    Screens.getButtons(screen).add(buttonMenuToggle);

                    elements.remove(buttonBox);
                    Screens.getButtons(screen).removeAll(buttons);

                    CustomButtonDataHandler.instance().updateButton(FishOnMCExtras.INVENTORY_SCREEN, false);
                }
        );
    }

    public void onMouseScrolled(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(this.statList != null
                && Configs.inventoryScreenConfig.showStatsElement.get()
                && this.statList.isMouseOver(mouseX, mouseY)
        ) {
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
                130, 20, true, false)));

        elements.add(Pair.of("stat_box", new BoxElement(minecraftClient,
                minecraftClient.getWindow().getScaledWidth() / 2 + INVENTORY_TRANSLATION,
                minecraftClient.getWindow().getScaledHeight() / 2 - INVENTORY_TOP,
                STAT_WIDTH - ((STAT_WIDTH / 4) / 3), INVENTORY_HEIGHT, false, false)));

        buttonBox = Pair.of("button_box", new BoxElement(minecraftClient,
                minecraftClient.getWindow().getScaledWidth() / 2 - 84,
                minecraftClient.getWindow().getScaledHeight() / 2 + INVENTORY_HEIGHT / 2 - 5,
                0,
                170, Math.max(11 + buttonBoxRows * buttonSizeAndPadding, 11 + 16), false, false, false, true, true, true));
        if(CustomButtonDataHandler.instance().getCustomButtonData().buttonList.getOrDefault(FishOnMCExtras.INVENTORY_SCREEN, Pair.of(new ArrayList<>(), false)).value2()) {
            elements.add(buttonBox);
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
