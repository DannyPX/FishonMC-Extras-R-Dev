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
    private static final AtomicInteger translationX = new AtomicInteger(0);
    public static void drawText(DrawContext drawContext, TextRenderer textRenderer, Text text, int x, int y, boolean shadow, boolean middle, boolean hasCustomFont, boolean smallText) {
        translationX.set(x);
        drawText(drawContext, textRenderer, text, y, shadow, middle, hasCustomFont, smallText);
    }

    private static void drawText(DrawContext drawContext, TextRenderer textRenderer, Text text, int y, boolean shadow, boolean middle, boolean hasCustomFont, boolean smallText) {
        List<Text> siblings = text.getSiblings();

        if(siblings.isEmpty()) {
            drawText(drawContext, textRenderer, text.getString(), translationX.get(), y, text.getStyle(), shadow, middle, hasCustomFont, smallText);

            int width = textRenderer.getWidth(text);
            if(smallText) {
                width = textRenderer.getWidth(Text.literal(TextHelper.smallText(text.getString())).setStyle(text.getStyle()));
            }

            translationX.set(translationX.get() + width);
        } else {
            siblings.forEach(text1 -> {
                drawText(drawContext, textRenderer, text1, y, shadow, middle, hasCustomFont, smallText);
            });
        }
    }

    public static void drawText(DrawContext drawContext, TextRenderer textRenderer, String text, int x, int y, Style style, boolean shadow, boolean middle, boolean hasCustomFont, boolean smallText) {
        drawText(drawContext, textRenderer, text.chars().mapToObj(c -> (char) c).collect(Collectors.toList()), x, y, style, shadow, middle, hasCustomFont, smallText);
    }

    public static void drawText(DrawContext drawContext, TextRenderer textRenderer, List<Character> text, int x, int y, Style style, boolean shadow, boolean middle, boolean hasCustomFont, boolean smallText) {
        if (!text.isEmpty()) {
            String glyph = popNextGlyph(text);

            if (smallText) {
                glyph = TextHelper.smallText(glyph);
            }

            int cWidth = textRenderer.getWidth(Text.literal(glyph).setStyle(style));

            int translateY = middle ? -1 : 0;

            int offsetY = 0;
            if(glyph.length() == 1) {
                if (TextHelper.isSmallNumber(glyph.charAt(0))) {
                    offsetY = 1;
                } else if (TextHelper.isSmallLetter(glyph.charAt(0)) || (hasCustomFont && TextHelper.isCustomFont(glyph.charAt(0)))) {

                } else {
                    translateY = 0;
                }
            }

            drawContext.drawText(
                    textRenderer,
                    Text.literal(glyph).setStyle(style),
                    x,
                    y - offsetY + translateY,
                    0xFFFFFF,
                    shadow
            );

            drawText(drawContext, textRenderer, text, x + cWidth, y, style, shadow, middle, hasCustomFont, smallText);
        }
    }

    private static String popNextGlyph(List<Character> text) {
        if (text.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        char first = text.removeFirst();
        sb.append(first);

        if (Character.isHighSurrogate(first) && !text.isEmpty() && Character.isLowSurrogate(text.get(0))) {
            sb.append(text.removeFirst());
        }

        while (!text.isEmpty()) {
            char next = text.getFirst();

            int codePoint;

            if (Character.isHighSurrogate(next) && text.size() > 1 && Character.isLowSurrogate(text.get(1))) {
                codePoint = Character.toCodePoint(next, text.get(1));
            } else {
                codePoint = next;
            }

            if (next == '\uFE0F' || next == '\uFE0E' || next == '\u200D'
                    || (codePoint >= 0x1F3FB && codePoint <= 0x1F3FF)) {

                sb.append(text.removeFirst());

                if (Character.isSupplementaryCodePoint(codePoint)) {
                    sb.append(text.removeFirst());
                }

            } else {
                break;
            }
        }
        return sb.toString();
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

    public static void drawLine(DrawContext context,
                                int x1, int y1,
                                int x2, int y2,
                                int color) {

        int dx = Math.abs(x2 - x1);
        int dy = -Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;

        while (true) {
            context.fill(x1, y1, x1 + 1, y1 + 1, color);

            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 >= dy) { err += dy; x1 += sx; }
            if (e2 <= dx) { err += dx; y1 += sy; }
        }
    }
}

