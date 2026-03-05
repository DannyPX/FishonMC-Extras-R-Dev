package dannypx.foe.screens.element.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.fetch.TabHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.handler.logic.PlaceholderHandler;
import dannypx.foe.common.handler.store.CustomHudDataHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.tuple.Triplet;
import dannypx.foe.screens.element.Element;
import dannypx.foe.screens.interfaces.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomHudElement extends Element implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    // isCentre, isSmall, Line
    private List<Triplet<Boolean, Boolean, Text>> textLines = new ArrayList<>();
    private Pair<Integer, Integer> contentDimensions = Pair.of(0, 0);

    private int boxWidth = 0;
    private int boxHeight = 0;

    private CustomHudDataHandler.CustomHud customHud;

    private static final Identifier BOX_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/sidebar_atlas.png");
    private static final int TEXTURE_WIDTH = 17;
    private static final int TEXTURE_HEIGHT = 11;
    private static final int BOX_PADDING = 5;
    private static final int MIN_WIDTH = 75;
    private static final int LINE_HEIGHT = MinecraftClient.getInstance().textRenderer.fontHeight + 1;
    //endregion

    public CustomHudElement(MinecraftClient minecraftClient, CustomHudDataHandler.CustomHud customHud, Text message) {
        super(75,
                50,
                customHud.xPos / 100f,
                customHud.yPos / 100f,
                customHud.alignment,
                message,
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
        this.customHud = customHud;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if(!customHud.showElement) { return; }

        int scaledWidth = (int) (minecraftClient.getWindow().getScaledWidth() * (1 / customHud.scale));
        int scaledHeight = (int) (minecraftClient.getWindow().getScaledHeight() * (1 / customHud.scale));

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(customHud.scale, customHud.scale, 1f);
        if(LoadingHandler.instance().isLoadingDone()
                && TabHandler.instance().isInInstance()
        ) {
            int x = switch (customHud.alignment) {
                case TOP_LEFT, BOTTOM_LEFT, LEFT -> Math.round(scaledWidth * xPos);
                case TOP_RIGHT, BOTTOM_RIGHT, RIGHT -> scaledWidth
                        - Math.round(scaledWidth * xPos);
                default -> 0;
            };

            int y = switch (customHud.alignment) {
                case TOP_LEFT, TOP_RIGHT, LEFT, RIGHT -> Math.round(scaledHeight * yPos);
                case BOTTOM_LEFT, BOTTOM_RIGHT -> scaledHeight
                        - Math.round(scaledHeight * yPos);
                default -> 0;
            };

            contentDimensions = this.assembleHud();
            boxWidth = contentDimensions.value1() + BOX_PADDING * 2 + PADDING * 2;
            boxHeight = contentDimensions.value2() + BOX_PADDING * 2 + PADDING_QUART * 2;
            if(!textLines.isEmpty()) {
                x = switch (customHud.alignment) {
                    case TOP_RIGHT, BOTTOM_RIGHT, RIGHT -> x - boxWidth;
                    default -> x;
                };

                y = switch (customHud.alignment) {
                    case BOTTOM_LEFT, BOTTOM_RIGHT -> y - boxHeight;
                    case LEFT, RIGHT -> y - boxHeight / 2;
                    default -> y;
                };

                this.renderBox(drawContext, tickCounter, x, y);
                this.renderText(drawContext, tickCounter, x, y);
            }
        }
        drawContext.getMatrices().pop();
    }

    private void renderText(DrawContext drawContext, RenderTickCounter tickCounter, int x, int y) {
        int textX = x + PADDING + BOX_PADDING;
        int textY = y + PADDING_QUART + BOX_PADDING;

        AtomicInteger line = new AtomicInteger(0);
        textLines.forEach(text -> {
            if(text.value1()) {
                DrawHelper.drawText(drawContext, textRenderer, text.value3(),
                        x + (boxWidth / 2) - TextHelper.getWidth(textRenderer, text.value3(), text.value2()) / 2,
                        textY + line.getAndIncrement() * LINE_HEIGHT,
                        true, text.value2(), true, text.value2()
                        );
            } else {
                DrawHelper.drawText(drawContext, textRenderer, text.value3(),
                        textX,
                        textY + line.getAndIncrement() * LINE_HEIGHT,
                        true, text.value2(), true, text.value2()
                );
            }
        });
    }

    private void renderBox(DrawContext drawContext, RenderTickCounter tickCounter, int x, int y) {
        int ATLAS_CORNER = 8;
        int ATLAS_BAR_WIDTH = 1;
        int ATLAS_BAR_HEIGHT = 5;
        int NIB_HEIGHT = 3;

        // Alpha Box
        drawContext.fill(
                x + BOX_PADDING, y + BOX_PADDING,
                x + this.boxWidth - BOX_PADDING, y + this.boxHeight - BOX_PADDING,
                0x7f000000
        );

        // Top Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x, y,
                0, NIB_HEIGHT,
                ATLAS_CORNER, ATLAS_CORNER,
                ATLAS_CORNER, ATLAS_CORNER,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Top
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x + ATLAS_CORNER, y,
                ATLAS_CORNER, NIB_HEIGHT,
                this.boxWidth - ATLAS_CORNER * 2, ATLAS_BAR_HEIGHT,
                ATLAS_BAR_WIDTH, ATLAS_BAR_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Top Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x + this.boxWidth - ATLAS_CORNER, y,
                ATLAS_CORNER + ATLAS_BAR_WIDTH, NIB_HEIGHT,
                ATLAS_CORNER, ATLAS_CORNER,
                ATLAS_CORNER, ATLAS_CORNER,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Bottom Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x, y + this.boxHeight - ATLAS_CORNER,
                0, 0,
                ATLAS_CORNER, ATLAS_CORNER,
                ATLAS_CORNER, ATLAS_CORNER,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Bottom
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x + ATLAS_CORNER, y + this.boxHeight - ATLAS_CORNER + NIB_HEIGHT,
                ATLAS_CORNER, NIB_HEIGHT,
                this.boxWidth - ATLAS_CORNER * 2, ATLAS_BAR_HEIGHT,
                ATLAS_BAR_WIDTH, ATLAS_BAR_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Bottom Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x + this.boxWidth - ATLAS_CORNER, y + this.boxHeight - ATLAS_CORNER,
                ATLAS_CORNER + ATLAS_BAR_WIDTH, 0,
                ATLAS_CORNER, ATLAS_CORNER,
                ATLAS_CORNER, ATLAS_CORNER,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
    }

    private Pair<Integer, Integer> assembleHud() {
        textLines.clear();

        AtomicBoolean hasData = new AtomicBoolean(false);

        customHud.textLines.forEach(line -> {
            String textString = line.value1().replace("&", "§");
            Pair<Boolean, MutableText> textLine = PlaceholderHandler.parsePlaceholderFromString(textString);
            if(textLine.value1()) {
                textLines.add(Triplet.of(line.value2(), line.value3(), textLine.value2()));
            }
            if(textLine.value1() && !textLine.value2().getString().isBlank()) {
                hasData.set(true);
            }
        });

        if(!hasData.get()) {
            textLines.clear();
        }

        return Pair.of(
                Math.max(MIN_WIDTH, textLines.stream()
                        .mapToInt(
                                line -> TextHelper.getWidth(textRenderer, line.value3(), line.value2())
                        ).max().orElse(0)),
                LINE_HEIGHT * textLines.size()
        );
    }
    //endregion
}
