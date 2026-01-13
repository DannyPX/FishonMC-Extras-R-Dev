package dannypx.foe.screens;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.config.Configs;
import dannypx.foe.config.DataHandlerConfig;
import dannypx.foe.screens.debug.DebugHandlerScreen;
import dannypx.foe.screens.interfaces.ScreenConstants;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends Screen implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final Screen parent;
    //endregion

    //region Methods
    public MainScreen(Screen parent) {
        super(Text.literal("Screen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.renderWidgets();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        //TODO Better button placement
        if(Configs.mainConfig.debugMode.get()) widgets.add(debugButton());
        if(Configs.mainConfig.debugMode.get()) widgets.add(configButton());

        widgets.forEach(this::addDrawableChild);
    }

    private ButtonWidget debugButton() {
        return ButtonWidget.builder(Text.literal("Debug Screen"), button ->
                        minecraftClient.setScreen(new DebugHandlerScreen(minecraftClient.currentScreen)))
                .position(width - PADDING - BUTTON_WIDTH, height - (PADDING + BUTTON_HEIGHT) * 2)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.of(Text.literal("Open Debug Screen")))
                .build();
    }

    private ButtonWidget configButton() {
        return ButtonWidget.builder(Text.literal("Config Screen"), button ->
                        ConfigApiJava.INSTANCE.openScreen(FishOnMCExtras.MOD_ID))
                .position(width - PADDING - BUTTON_WIDTH, height - (PADDING + BUTTON_HEIGHT))
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .tooltip(Tooltip.of(Text.literal("Open Config Screen")))
                .build();
    }

    @Override
    public void close() {
        this.minecraftClient.setScreen(parent);
    }
    //endregion
}
