package dannypx.foe.screens;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.debug.DebugHandlerScreen;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends DefaultModScreen {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    private static final Identifier ICON_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/icon");
    //endregion

    //region Methods
    public MainScreen(Screen parent) {
        super(parent, Text.literal("Main Screen"));
    }

    @Override
    protected void init() {
        super.init();
        this.renderWidgets();
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        int screenWidth = minecraftClient.getWindow().getScaledWidth();
        int screenHeight = minecraftClient.getWindow().getScaledHeight();

        int size = 200;

        drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                ICON_TEXTURE,
                screenWidth / 2 - size / 2, screenHeight / 2 - size + 32,
                size, size
        );

        drawContext.drawHorizontalLine(screenWidth / 2 - size / 2, screenWidth / 2 + size / 2, screenHeight / 2, 0xFFAAAAAA);

        drawContext.drawText(textRenderer, Text.literal(FishOnMCExtras.VERSION).formatted(Formatting.DARK_GRAY), PADDING_QUART, height - textRenderer.fontHeight - PADDING_QUART, 0xFFFFFF, true);
    }

    private void renderWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        if(Configs.debugConfig.debugMode.get()) widgets.add(debugButton());
        widgets.add(configButton());
        widgets.add(moveHudButton());
        

        widgets.forEach(this::addDrawableChild);
    }

    private ButtonWidget configButton() {
        return ButtonWidget.builder(Text.literal("Config Screen"), button ->
                        ConfigApiJava.INSTANCE.openScreen(FishOnMCExtras.MOD_ID))
                .position(width / 2 - BUTTON_WIDTH / 2, height / 2 + (BUTTON_HEIGHT + PADDING))
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.of(Text.literal("Open Config Screen")))
                .build();
    }

    private ButtonWidget moveHudButton() {
        return ButtonWidget.builder(Text.literal("Move HUD Elements Screen"), button ->
                        minecraftClient.setScreen(new MoveElementScreen(minecraftClient.currentScreen)))
                .position(width / 2 - BUTTON_WIDTH / 2, height / 2 + (BUTTON_HEIGHT + PADDING) * 2)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.of(Text.literal("Open Move HUD Elements Screen")))
                .build();
    }

    private ButtonWidget debugButton() {
        return ButtonWidget.builder(Text.literal("Debug Screen"), button ->
                        minecraftClient.setScreen(new DebugHandlerScreen(minecraftClient.currentScreen)))
                .position(width / 2 - BUTTON_WIDTH / 2, height / 2 + (BUTTON_HEIGHT + PADDING) * 3)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.of(Text.literal("Open Debug Screen")))
                .build();
    }
    //endregion
}
