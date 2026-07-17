package dannypx.foe.helper;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import dannypx.foe.type.StringStyle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.joml.Matrix3x2f;

public class GuiGraphicsHelper {
    private static final AtomicInteger translationX = new AtomicInteger(0);

    public static void drawString(GuiGraphics guiGraphics, Font font, Component component, int x, int y, StringStyle ...stringStyles) {
        translationX.set(x);
        drawString(guiGraphics, font, component, y, stringStyles);
    }

    private static void drawString(GuiGraphics guiGraphics, Font font, Component component, int y, StringStyle ...stringStyles) {
        List<Component> siblings = component.getSiblings();

        boolean smallCaps = false;
        for (StringStyle style : stringStyles) {
            if (style == StringStyle.SMALL_CAPS) {
                smallCaps = true;
                break;
            }
        }

        if(siblings.isEmpty()) {
            drawString(guiGraphics, font, component.getString(), translationX.get(), y, component.getStyle(), stringStyles);

            int width;
            if(smallCaps) {
                width = font.width(Component.literal(TextHelper.smallCaps(component.getString())).setStyle(component.getStyle()));
            } else {
                width = font.width(component);
            }

            translationX.set(translationX.get() + width);
        } else {
            siblings.forEach(text1 -> drawString(guiGraphics, font, text1, y, stringStyles));
        }
    }

    private static void drawString(GuiGraphics guiGraphics, Font font, String text, int x, int y, Style style, StringStyle ...stringStyles) {
        drawString(guiGraphics, font, text.chars().mapToObj(c -> (char) c).collect(Collectors.toList()), x, y, style, stringStyles);
    }

    private static void drawString(GuiGraphics guiGraphics, Font font, List<Character> characterList, int x, int y, Style style, StringStyle ...stringStyles) {
        boolean shadow = false;
        boolean middle = false;
        boolean hasCustomFont = false;
        boolean smallCaps = false;

        for (StringStyle stringStyle : stringStyles) {
            switch (stringStyle) {
                case SHADOW -> shadow = true;
                case MIDDLE -> middle = true;
                case HAS_CUSTOM_FONT -> hasCustomFont = true;
                case SMALL_CAPS -> smallCaps = true;
            }
        }

        if (!characterList.isEmpty()) {
            String glyph = popNextGlyph(characterList);

            if (smallCaps) {
                glyph = TextHelper.smallCaps(glyph);
            }

            int cWidth = font.width(Component.literal(glyph).setStyle(style));

            int translateY = middle ? -1 : 0;

            int offsetY = 0;
            if(glyph.length() == 1) {
                if (TextHelper.isSmallNumber(glyph.charAt(0))) {
                    offsetY = 1;
                } else if (hasCustomFont && smallCaps && TextHelper.isRank(glyph.charAt(0))) {
                    offsetY = -1;
                } else if (TextHelper.isSmallLetter(glyph.charAt(0)) || (hasCustomFont && TextHelper.isCustomFont(glyph.charAt(0)))) {
                } else {
                    translateY = 0;
                }
            }

            guiGraphics.guiRenderState.submitText(
                    new GuiTextRenderState(
                            font,
                            Component.literal(glyph).setStyle(style).getVisualOrderText(),
                            new Matrix3x2f(guiGraphics.pose()),
                            x, y - offsetY + translateY, CommonColors.WHITE,
                            0, shadow, false, guiGraphics.scissorStack.peek()
                    )
            );

            drawString(guiGraphics, font, characterList, x + cWidth, y, style, stringStyles);
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

