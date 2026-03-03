package dannypx.foe.screens;

import dannypx.foe.screens.interfaces.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class DefaultModScreen extends Screen implements ScreenConstants {
    final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    final Screen parentScreen;
    final boolean isMiddle;

    protected DefaultModScreen(Screen parent, Text title) {
        super(title);
        this.parentScreen = parent;
        this.isMiddle = false;
    }

    protected DefaultModScreen(Screen parent, Text title, boolean isMiddle) {
        super(title);
        this.parentScreen = parent;
        this.isMiddle = isMiddle;
    }

    @Override
    protected void init() {
        super.init();
        this.renderDefaultWidgets();
    }

    private void renderDefaultWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        widgets.add(this.backButton());

        widgets.forEach(this::addDrawableChild);
    }

    private ButtonWidget backButton() {
        return !isMiddle ? ButtonWidget.builder(Text.literal("Return"), button ->
                        this.close())
                .position(width - PADDING_HALF - 50, height - PADDING_HALF - BUTTON_HEIGHT)
                .size(50, BUTTON_HEIGHT)
                .build()
                : ButtonWidget.builder(Text.literal("Return"), button ->
                        this.close())
                .position(width / 2 - 50 / 2, height / 2 - BUTTON_HEIGHT / 2)
                .size(50, BUTTON_HEIGHT)
                .build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.minecraftClient.setScreen(this.parentScreen);
    }
}
