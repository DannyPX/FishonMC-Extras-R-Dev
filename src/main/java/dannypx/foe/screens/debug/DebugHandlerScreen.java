package dannypx.foe.screens.debug;

import dannypx.foe.handler.debug._DebugHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.screens.DefaultModScreen;
import dannypx.foe.screens.widget.ButtonListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugHandlerScreen extends DefaultModScreen {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    private ButtonListWidget handlerList;
    private String selectedHandler;
    private String hoveredName = "";
    private Pair<MutableText, MutableText> hoveredValue = Pair.of(Text.empty(), Text.empty());

    private final List<String> handlerNames;
    private Map<String, Map<String, Pair<MutableText, MutableText>>> handlerFields;
    //endregion

    //region Methods
    public DebugHandlerScreen(Screen parent) {
        super(parent, Text.literal("Debug Handler Screen"));
        handlerNames = _DebugHandler.instance()._getHandlerNames();
    }

    @Override
    protected void init() {
        super.init();
        this.renderWidgets();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.updateFields();
        this.handlerList.render(context, mouseX, mouseY, delta);
        this.renderHandlerFields(context, mouseX, mouseY, delta);
    }

    private void updateFields() {
        handlerFields = _DebugHandler.instance()._getFields();
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
        handlerNames.forEach(handler -> handlerList.addEntry(new ButtonListWidget.ButtonEntry(
                ButtonWidget.builder(
                        Text.literal(handler.replace("dannypx.foe.handler.", "")),
                        button -> selectedHandler = handler
                ).width(BUTTON_WIDTH).build()
        )));

        return handlerList;
    }

    private void renderHandlerFields(DrawContext context, int mouseX, int mouseY, float delta) {
        if(selectedHandler != null) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            handlerFields.get(selectedHandler).forEach((name, value) -> {
                Text text = TextHelper.concat(
                        Text.literal(name).formatted(Formatting.BOLD),
                        Text.literal(": "),
                        value.value1()
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
                if(mouseX >= textx && mouseX <= textx + textwidth
                        && mouseY >= texty && mouseY <= texty + textHeight) {
                    if(!Objects.equals(value.value2(), Text.empty())) {
                        context.drawTooltip(textRenderer, Tooltip.of(value.value2()).getLines(minecraftClient), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
                    }

                    hoveredName = name;
                    hoveredValue = value;
                }
            });
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        this.copyText(keyCode, modifiers);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void copyText(int key, int modifiers) {
        boolean ctrl = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0
                || (modifiers & GLFW.GLFW_MOD_SUPER) != 0;

        if(ctrl && key == GLFW.GLFW_KEY_C) {
            String json;
            if(Objects.equals(hoveredValue.value2(), Text.empty())) {
                json = TextHelper.textToJsonPretty(hoveredValue.value1());
            } else {
                json = hoveredValue.value2().getString();
            }

            minecraftClient.keyboard.setClipboard(json);

            SystemToast.add(minecraftClient.getToastManager(),
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    Text.literal("Fish On Extras Rebirth"),
                    Text.literal("Copied " + hoveredName + " JSON"));
        }
    }
    //endregion
}
