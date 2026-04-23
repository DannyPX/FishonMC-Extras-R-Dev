package dannypx.foe.helper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonColors;

public class GuiGraphicsHelper {
    private static final AtomicInteger translationX = new AtomicInteger(0);
    public static void drawString(GuiGraphics guiGraphics, Font font, Component component, int x, int y, boolean shadow, boolean middle, boolean hasCustomFont, boolean smallCaps) {
        translationX.set(x);
        drawString(guiGraphics, font, component, y, shadow, middle, hasCustomFont, smallCaps);
    }

    private static void drawString(GuiGraphics guiGraphics, Font font, Component component, int y, boolean shadow, boolean middle, boolean hasCustomFont, boolean smallCaps) {
        List<Component> siblings = component.getSiblings();

        if(siblings.isEmpty()) {
            drawString(guiGraphics, font, component.getString(), translationX.get(), y, component.getStyle(), shadow, middle, hasCustomFont, smallCaps);

            int width = font.width(component);
            if(smallCaps) {
                width = font.width(Component.literal(ComponentHelper.smallCaps(component.getString())).setStyle(component.getStyle()));
            }

            translationX.set(translationX.get() + width);
        } else {
            siblings.forEach(text1 -> drawString(guiGraphics, font, text1, y, shadow, middle, hasCustomFont, smallCaps));
        }
    }

    private static void drawString(GuiGraphics guiGraphics, Font font, String text, int x, int y, Style style, boolean shadow, boolean middle, boolean hasCustomFont, boolean smallCaps) {
        drawString(guiGraphics, font, text.chars().mapToObj(c -> (char) c).collect(Collectors.toList()), x, y, style, shadow, middle, hasCustomFont, smallCaps);
    }

    private static void drawString(GuiGraphics guiGraphics, Font font, List<Character> characterList, int x, int y, Style style, boolean shadow, boolean middle, boolean hasCustomFont, boolean smallCaps) {
        if (!characterList.isEmpty()) {
            String glyph = popNextGlyph(characterList);

            if (smallCaps) {
                glyph = ComponentHelper.smallCaps(glyph);
            }

            int cWidth = font.width(Component.literal(glyph).setStyle(style));

            int translateY = middle ? -1 : 0;

            int offsetY = 0;
            if(glyph.length() == 1) {
                if (ComponentHelper.isSmallNumber(glyph.charAt(0))) {
                    offsetY = 1;
                } else if (ComponentHelper.isSmallLetter(glyph.charAt(0)) || (hasCustomFont && ComponentHelper.isCustomFont(glyph.charAt(0)))) {

                } else {
                    translateY = 0;
                }
            }

            guiGraphics.drawString(
                    font,
                    Component.literal(glyph).setStyle(style),
                    x,
                    y - offsetY + translateY,
                    CommonColors.WHITE,
                    shadow
            );

            drawString(guiGraphics, font, characterList, x + cWidth, y, style, shadow, middle, hasCustomFont, smallCaps);
        }
    }

    private static String popNextGlyph(List<Character> characterList) {
        if (characterList.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        char first = characterList.removeFirst();
        sb.append(first);

        if (Character.isHighSurrogate(first) && !characterList.isEmpty() && Character.isLowSurrogate(characterList.get(0))) {
            sb.append(characterList.removeFirst());
        }

        while (!characterList.isEmpty()) {
            char next = characterList.getFirst();

            int codePoint;

            if (Character.isHighSurrogate(next) && characterList.size() > 1 && Character.isLowSurrogate(characterList.get(1))) {
                codePoint = Character.toCodePoint(next, characterList.get(1));
            } else {
                codePoint = next;
            }

            if (next == '\uFE0F' || next == '\uFE0E' || next == '\u200D'
                    || (codePoint >= 0x1_F3FB && codePoint <= 0x1F3FF)) {

                sb.append(characterList.removeFirst());

                if (Character.isSupplementaryCodePoint(codePoint)) {
                    sb.append(characterList.removeFirst());
                }

            } else {
                break;
            }
        }
        return sb.toString();
    }

    public static void drawHorizontalGradient(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int leftColor, int rightColor) {
        int width = x2 - x1;
        for (int i = 0; i < width; i++) {
            float t = i / (float) (width - 1);
            int r = (int) ( ((leftColor >> 16 & 0xFF) * (1 - t)) + ((rightColor >> 16 & 0xFF) * t) );
            int g = (int) ( ((leftColor >> 8 & 0xFF) * (1 - t)) + ((rightColor >> 8 & 0xFF) * t) );
            int b = (int) ( ((leftColor & 0xFF) * (1 - t)) + ((rightColor & 0xFF) * t) );
            int a = (int) ( ((leftColor >> 24 & 0xFF) * (1 - t)) + ((rightColor >> 24 & 0xFF) * t) );

            int color = (a << 24) | (r << 16) | (g << 8) | b;
            guiGraphics.fill(x1 + i, y1, x1 + i + 1, y2, color);
        }
    }

    public static void drawLine(GuiGraphics guiGraphics,
                                int x1, int y1,
                                int x2, int y2,
                                int color) {

        int dx = Math.abs(x2 - x1);
        int dy = -Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;

        while (true) {
            guiGraphics.fill(x1, y1, x1 + 1, y1 + 1, color);

            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 >= dy) { err += dy; x1 += sx; }
            if (e2 <= dx) { err += dx; y1 += sy; }
        }
    }
}

