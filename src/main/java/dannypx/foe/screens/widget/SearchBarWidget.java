package dannypx.foe.screens.widget;

import dannypx.foe.common.handler.logic.SearchHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchBarWidget extends TextFieldWidget {
    private final List<Text> hoverInfo;
    private TextRenderer textRenderer;

    public SearchBarWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, List<Text> hoverInfo) {
        super(textRenderer, x, y, width, height, null, text);
        this.hoverInfo = hoverInfo;
        this.textRenderer = textRenderer;
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.renderWidget(drawContext, mouseX, mouseY, delta);
        SearchHandler.instance().setFocused(this.isFocused());

        // Hover Info
        if(this.isHovered() && this.isFocused()) {
            drawContext.getMatrices().push();
            try {
                float scale = .75f;
                drawContext.getMatrices().translate(0, this.getBottom(), 320);
                drawContext.getMatrices().scale(scale, scale, 1);

                int padding = 4;
                int lineHeight = textRenderer.fontHeight + 1;
                int length = hoverInfo.stream().map(textRenderer::getWidth).max(Integer::compareTo).orElse(0);
                int lines = hoverInfo.size() * lineHeight;
                int x = (int) (((float) MinecraftClient.getInstance().getWindow().getScaledWidth() / 2) * (1 / scale));

                drawContext.fill(x - length / 2 - padding, 0, x + length / 2 + padding, padding * 2 + lines, Colors.BLACK);
                drawContext.drawBorder(x - length / 2 - padding, 0, padding * 2 + length, padding * 2 + lines, Colors.GRAY);

                AtomicInteger count = new AtomicInteger(0);
                hoverInfo.forEach(text -> {
                    drawContext.drawText(textRenderer, text, x - length / 2, padding + count.getAndIncrement() * lineHeight, 0xFFFFFF, true);
                });
            } finally {
                drawContext.getMatrices().pop();
            }
        }
    }
}
