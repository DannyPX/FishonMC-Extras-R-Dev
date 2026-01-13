package dannypx.foe.common.render_module.helper;

import dannypx.foe.common.helper.TextHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Only use drawText() when using text that also has small text in it
 */
public class DrawHelper {

    public static void drawText(DrawContext drawContext, TextRenderer textRenderer, Text text, int x, int y) {
        drawText(drawContext, textRenderer, text, x, y, false, true);
    }

    public static void drawText(DrawContext drawContext, TextRenderer textRenderer, Text text, int x, int y, boolean middle) {
        drawText(drawContext, textRenderer, text, x, y, middle, true);
    }

    public static void drawText(DrawContext drawContext, TextRenderer textRenderer, Text text, int x, int y, boolean middle, boolean shadow) {
        AtomicInteger translateX = new AtomicInteger(0);
        text.getSiblings().forEach(t -> {
            drawText(drawContext, textRenderer, t.getString(), x + translateX.get(), y, t.getStyle(), middle, shadow);
            translateX.addAndGet(textRenderer.getWidth(t));
        });
    }

    public static void drawText(DrawContext drawContext, TextRenderer textRenderer, String text, int x, int y, Style style, boolean middle, boolean shadow) {
        drawText(drawContext, textRenderer, text.chars().mapToObj(c -> (char) c).collect(Collectors.toList()), x, y, style, middle, shadow);
    }

    public static void drawText(DrawContext drawContext, TextRenderer textRenderer, List<Character> text, int x, int y, Style style, boolean middle, boolean shadow) {
        if(!text.isEmpty()) {
            char c = text.getFirst();
            text.removeFirst();
            int cWidth = textRenderer.getWidth(String.valueOf(c));
            int translateY = middle ? -1 : 0;
            if(TextHelper.isSmallNumber(c)) {
                drawContext.drawText(textRenderer, Text.literal(String.valueOf(c)).setStyle(style), x, y - 1 + translateY, 0xFFFFFF, shadow);
            } else if (TextHelper.isSmallLetter(c)) {
                drawContext.drawText(textRenderer, Text.literal(String.valueOf(c)).setStyle(style), x, y + translateY, 0xFFFFFF, shadow);
            } else {
                drawContext.drawText(textRenderer, Text.literal(String.valueOf(c)).setStyle(style), x, y, 0xFFFFFF, shadow);
            }
            drawText(drawContext, textRenderer, text, x + cWidth, y, style, shadow, middle);
        }
    }
}
