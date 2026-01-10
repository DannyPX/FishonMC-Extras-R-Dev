package dannypx.foe.screens.debug;

import dannypx.foe.common.handler.debug._DebugHandler;
import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import dannypx.foe.common.widget.ButtonListWidget;
import dannypx.foe.screens.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugHandlerScreen extends Screen implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final Screen parent;

    private ButtonListWidget handlerList;
    private Map<String, Pair<MutableText, Tooltip>> selectedHandler;
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
                            button -> selectedHandler = _DebugHandler.instance()._getFields().get(handler)
                    ).width(BUTTON_WIDTH).build()
            ));
        });

        return handlerList;
    }

    private void renderHandlerFields(DrawContext context, int mouseX, int mouseY, float delta) {
        if(selectedHandler != null) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            selectedHandler.forEach((name, value) -> {
                context.drawText(textRenderer,
                        TextHelper.concat(
                                Text.literal(name).formatted(Formatting.BOLD),
                                Text.literal(": "),
                                value.v1().formatted(Formatting.GRAY)
                        ),
                        (BUTTON_WIDTH + PADDING * 2) + PADDING,
                        PADDING + (textRenderer.fontHeight + LINE_SPACING) * atomicInteger.getAndIncrement(),
                        0xFFFFFF,
                        true
                );
            });
        }
    }

    @Override
    public void close() {
        this.minecraftClient.setScreen(parent);
    }
    //endregion
}
