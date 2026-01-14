package dannypx.foe.screens.debug;

import dannypx.foe.common.handler.debug._DebugHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.screens.widget.ButtonListWidget;
import dannypx.foe.screens.interfaces.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugHandlerScreen extends Screen implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final Screen parent;

    private ButtonListWidget handlerList;
    private String selectedHandler;
    //endregion

    //region Methods
    public DebugHandlerScreen(Screen parent) {
        super(Text.literal("Debug Handler Screen"));
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
        this.handlerList.render(context, mouseX, mouseY, delta);
        this.renderHandlerFields(context, mouseX, mouseY, delta);
    }

    private void renderWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        //Scrollable Handler List Widget
        widgets.add(getHandlerList());

        widgets.forEach(this::addDrawableChild);
    }

    private ClickableWidget getHandlerList() {
        handlerList = new ButtonListWidget(
                client,
                (BUTTON_WIDTH + PADDING * 2),
                height,
                0,
                BUTTON_HEIGHT + PADDING_HALF,
                BUTTON_HEIGHT,
                "Handlers"
        );

        // Add buttons
        _DebugHandler.instance()._getHandlers().forEach(handler -> {
            handlerList.addEntry(new ButtonListWidget.ButtonEntry(
                    ButtonWidget.builder(
                            Text.literal(handler.replace("dannypx.foe.common.handler.", "")),
                            button -> selectedHandler = handler
                    ).width(BUTTON_WIDTH).build()
            ));
        });

        return handlerList;
    }

    private void renderHandlerFields(DrawContext context, int mouseX, int mouseY, float delta) {
        if(selectedHandler != null) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            _DebugHandler.instance()._getFields().get(selectedHandler).forEach((name, value) -> {
                Text text = TextHelper.concat(
                        Text.literal(name).formatted(Formatting.BOLD),
                        Text.literal(": "),
                        value.v1()
                );
                // Get Text Coordinates and Bounds
                int textx = (BUTTON_WIDTH + PADDING * 2) + PADDING;
                int texty = PADDING + (textRenderer.fontHeight + LINE_SPACING) * atomicInteger.getAndIncrement();
                int textwidth = textRenderer.getWidth(text);
                int textHeight = textRenderer.fontHeight;
                int color = 0xFFFFFF;

                // Draw Text
                context.drawText(textRenderer, text, textx, texty, color, true);

                // Draw Tooltip
                if(value.v2() != null && mouseX >= textx && mouseX <= textx + textwidth
                        && mouseY >= texty && mouseY <= texty + textHeight) {
                    context.drawTooltip(textRenderer, value.v2().getLines(minecraftClient), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
                }
            });
        }
    }

    @Override
    public void close() {
        this.minecraftClient.setScreen(parent);
    }
    //endregion
}
