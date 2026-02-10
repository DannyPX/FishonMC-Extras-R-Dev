package dannypx.foe.screens.element;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.screens.interfaces.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class NotificationElement extends Element implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;
    private int x = 0;
    private int y = 0;
    private final ItemStack itemStack;
    private final int rows;
    private final int columns;
    private final List<Text> textList;

    private final int contentHeight;
    private final int contentWidth;

    private static final Identifier BOX_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/box_atlas.png");
    private static final int LINE_HEIGHT = 1;
    private static final int BOX_PADDING = 5 + PADDING_QUART;

    private boolean isError = false;
    //endregion

    public NotificationElement(MinecraftClient minecraftClient, int width, ItemStack itemStack, int rows, int columns, List<Text> textList) {
        super(0,
                0,
                0,
                0,
                null,
                Text.literal("Notification Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
        this.itemStack = itemStack;
        this.rows = rows;
        this.columns = columns;
        this.textList = textList;

        contentHeight = Math.max(16 + PADDING_QUART * 2, rows * (textRenderer.fontHeight + LINE_HEIGHT) - LINE_HEIGHT);
        this.height = BOX_PADDING * 2 + contentHeight;


        this.width = width;
        contentWidth = width - BOX_PADDING * 2;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if(isError) {
            return;
        }
        this.renderBox(drawContext);
        this.renderItem(drawContext, itemStack);
        this.renderText(drawContext, textRenderer, textList, rows, columns);
    }

    private void renderText(DrawContext drawContext, TextRenderer textRenderer, List<Text> textList, int rows, int columns) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int textX = itemStack.isEmpty()
                        ? x + BOX_PADDING + (contentWidth / columns) * column
                        : x + BOX_PADDING + 16 + PADDING_QUART + ((contentWidth - (16 + PADDING_QUART)) / columns) * column;
                int textY = rows == 1
                        ? y + this.height / 2 - textRenderer.fontHeight / 2
                        : y + BOX_PADDING + (textRenderer.fontHeight + LINE_HEIGHT) * row;

                if((row * columns) + column + 1 > textList.size()) {
                    LoggerHandler.error("Index" + ((row * columns) + column) + " out of bounds for length " + textList.size());
                    isError = true;
                    return;
                }

                Text text = textList.get((row * columns) + column);

                if(!Objects.equals(text, Text.empty())) {
                    DrawHelper.drawText(drawContext, textRenderer, text,
                            textX, textY,
                            true, true, true, true);
                }
            }
        }
    }

    private void renderItem(DrawContext drawContext, ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            drawContext.drawItem(itemStack, x + BOX_PADDING, y + this.height / 2 - 16 / 2);
        }
    }

    private void renderBox(DrawContext drawContext) {
        // Top Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                this.x, this.y,
                0, 0,
                5, 5,
                5, 5,
                15,15
        );

        // Top
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                this.x + 5, this.y,
                5, 0,
                this.width - 10, 5,
                5, 5,
                15, 15
        );

        // Top Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x + this.width - 5, this.y,
                10, 0,
                5, 5,
                5, 5,
                15, 15
        );

        // Centre Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                this.x, this.y + 5,
                0, 5,
                5, this.height - 10,
                5, 5,
                15,15
        );

        // Centre
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                this.x + 5, this.y + 5,
                5, 5,
                this.width - 10, this.height - 10,
                5, 5,
                15, 15
        );

        // Centre Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x + this.width - 5, this.y + 5,
                10, 5,
                5, this.height - 10,
                5, 5,
                15, 15
        );

        // Bottom Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                this.x, y + this.height - 5,
                0, 10,
                5, 5,
                5, 5,
                15,15
        );

        // Bottom
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                this.x + 5, y + this.height - 5,
                5, 10,
                this.width - 10, 5,
                5, 5,
                15, 15
        );

        // Bottom Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                BOX_TEXTURE,
                x + this.width - 5, y + this.height - 5,
                10, 10,
                5, 5,
                5, 5,
                15, 15
        );
    }
    //endregion
}
