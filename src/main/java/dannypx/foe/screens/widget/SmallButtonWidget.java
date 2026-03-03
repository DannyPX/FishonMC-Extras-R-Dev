package dannypx.foe.screens.widget;

import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import dannypx.foe.screens.element.BoxElement;
import dannypx.foe.screens.element.Element;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmallButtonWidget extends ClickableWidget {
    MinecraftClient minecraftClient = MinecraftClient.getInstance();


    private final ClickCallback clickCallback;
    private final String icon;
    List<Pair<String, Element>> elements = new ArrayList<>();

    public SmallButtonWidget(int x, int y, int width, int height, String icon, @Nullable Tooltip tooltip, Text message, ClickCallback clickCallback) {
        super(x, y, width, height, message);
        this.icon = icon;
        this.clickCallback = clickCallback;
        this.setTooltip(tooltip);
        this.init();
    }

    private void init() {
        this.initElements();
    }

    private void initElements() {
        elements.clear();
        elements.add(Pair.of("button_box", new BoxElement(minecraftClient,
                getX(),
                getY(),
                width, height, true)));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBox(context);
        this.renderIcon(context);
    }

    private void renderBox(DrawContext context) {
        elements.forEach(element -> element.v2().render(context, minecraftClient.getRenderTickCounter()));
    }

    private void renderIcon(DrawContext context) {
        int textWidth = minecraftClient.textRenderer.getWidth(TextHelper.smallText(icon));
        DrawHelper.drawText(context,
                minecraftClient.textRenderer,
                Text.literal(icon),
                getX() + (width / 2) - textWidth / 2, getY() + (height / 2) - minecraftClient.textRenderer.fontHeight / 2,
                true,
                true,
                false,
                true
        );
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        if(clickCallback != null) {
            this.clickCallback.onClick(this);
        }
    }

    public interface ClickCallback {
        void onClick(SmallButtonWidget smallButtonWidget);
    }
}
