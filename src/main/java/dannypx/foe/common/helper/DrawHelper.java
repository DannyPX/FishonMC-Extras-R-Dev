package dannypx.foe.common.helper;

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
        if (!text.isEmpty()) {
            char c = text.getFirst();
            text.removeFirst();
            int cWidth = textRenderer.getWidth(String.valueOf(c));
            int translateY = middle ? -1 : 0;
            if (TextHelper.isSmallNumber(c)) {
                drawContext.drawText(textRenderer, Text.literal(String.valueOf(c)).setStyle(style), x, y - 1 + translateY, 0xFFFFFF, shadow);
            } else if (TextHelper.isSmallLetter(c)) {
                drawContext.drawText(textRenderer, Text.literal(String.valueOf(c)).setStyle(style), x, y + translateY, 0xFFFFFF, shadow);
            } else {
                drawContext.drawText(textRenderer, Text.literal(String.valueOf(c)).setStyle(style), x, y, 0xFFFFFF, shadow);
            }
            drawText(drawContext, textRenderer, text, x + cWidth, y, style, shadow, middle);
        }
    }

    public static void drawHorizontalGradient(DrawContext context, int x1, int y1, int x2, int y2, int leftColor, int rightColor) {
        int width = x2 - x1;
        for (int i = 0; i < width; i++) {
            float t = i / (float) (width - 1);
            int r = (int) ( ((leftColor >> 16 & 0xFF) * (1 - t)) + ((rightColor >> 16 & 0xFF) * t) );
            int g = (int) ( ((leftColor >> 8 & 0xFF) * (1 - t)) + ((rightColor >> 8 & 0xFF) * t) );
            int b = (int) ( ((leftColor & 0xFF) * (1 - t)) + ((rightColor & 0xFF) * t) );
            int a = (int) ( ((leftColor >> 24 & 0xFF) * (1 - t)) + ((rightColor >> 24 & 0xFF) * t) );

            int color = (a << 24) | (r << 16) | (g << 8) | b;
            context.fill(x1 + i, y1, x1 + i + 1, y2, color);
        }
    }
}

