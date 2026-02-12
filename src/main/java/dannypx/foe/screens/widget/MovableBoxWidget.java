package dannypx.foe.screens.widget;

import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Alignment;
import dannypx.foe.screens.element.Element;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.screens.interfaces.ScreenConstants;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class MovableBoxWidget extends ClickableWidget implements ScreenConstants {
    private final MinecraftClient minecraftClient;
    private final Callback callback;
    private final List<Alignment> alignmentList;
    private final String config;
    private Element element;

    private double deltaX = 0;
    private double deltaY = 0;
    private int originalX;
    private int originalY;

    public MovableBoxWidget(MinecraftClient minecraftClient,
                            Element element,
                            List<Alignment> alignmentList,
                            String config,
                            Callback callback) {
        super(1, 1, 1, 1, element.message);
        this.minecraftClient = minecraftClient;
        this.callback = callback;
        this.alignmentList = alignmentList;
        this.config = config;
        setup(element);
    }

    private void setup(Element element) {
        this.element = element;

        switch (element.alignment) {
            case LEFT, TOP_LEFT, BOTTOM_LEFT -> {
                this.setX(Math.round(minecraftClient.getWindow().getScaledWidth() * element.xPos));
                this.originalX = this.getX();
            }
            case RIGHT, BOTTOM_RIGHT, TOP_RIGHT -> {
                this.setX(minecraftClient.getWindow().getScaledWidth()
                        - Math.round(minecraftClient.getWindow().getScaledWidth() * element.xPos));
                this.originalX = minecraftClient.getWindow().getScaledWidth() - this.getX();
                this.setX(this.getX() - element.width);
            }
            case TOP, BOTTOM -> {
                this.setX(Math.round(minecraftClient.getWindow().getScaledWidth() * element.xPos));
                this.originalX = this.getX();
                this.setX(this.getX() - (element.width / 2));
            }
        }

        switch (element.alignment) {
            case TOP_LEFT, TOP, TOP_RIGHT -> {
                this.setY(Math.round(minecraftClient.getWindow().getScaledHeight() * element.yPos));
                this.originalY = this.getY();
            }
            case BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT -> {
                this.setY(minecraftClient.getWindow().getScaledHeight()
                        - Math.round(minecraftClient.getWindow().getScaledHeight() * element.yPos));
                this.originalY = minecraftClient.getWindow().getScaledHeight() - this.getY();
                this.setY(this.getY() - element.height);
            }
            case LEFT, RIGHT -> {
                this.setY(Math.round(minecraftClient.getWindow().getScaledHeight() * element.yPos));
                this.originalY = this.getY();
                this.setY(this.getY() - (element.height / 2));
            }
        }

        this.width = element.width;
        this.height = element.height;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBox(context);
        this.renderTooltip(context, mouseX, mouseY);
        this.renderAlignment(context);
        this.element.render(context, MinecraftClient.getInstance().getRenderTickCounter());
    }

    private void renderAlignment(DrawContext context) {
        int screenWidth = minecraftClient.getWindow().getScaledWidth();
        int screenHeight = minecraftClient.getWindow().getScaledHeight();

        switch (element.alignment) {
            case TOP_LEFT -> {
                context.fill(
                        getX() - 1,
                        getY() - 1,
                        getX() + 1,
                        getY() + 1,
                        0xFFFF0000
                );

                DrawHelper.drawLine(context,
                        getX() - 1, getY() - 1,
                        0,0,
                        0xFFFF0000
                );
            }
            case TOP_RIGHT -> {
                context.fill(
                        getX() - 1 + getWidth(),
                        getY() - 1,
                        getX() + 1 + getWidth(),
                        getY() + 1,
                        0xFFFF0000
                );

                DrawHelper.drawLine(context,
                        getX() + 1 + getWidth(), getY() - 1,
                        screenWidth,0,
                        0xFFFF0000
                );
            }
            case BOTTOM_LEFT -> {
                context.fill(
                        getX() - 1,
                        getY() - 1 + getHeight(),
                        getX() + 1,
                        getY() + 1 + getHeight(),
                        0xFFFF0000
                );

                DrawHelper.drawLine(context,
                        getX() - 1, getY() + 1 + getHeight(),
                        0, screenHeight,
                        0xFFFF0000
                );
            }
            case BOTTOM_RIGHT -> {
                context.fill(
                        getX() - 1 + getWidth(),
                        getY() - 1 + getHeight(),
                        getX() + 1 + getWidth(),
                        getY() + 1 + getHeight(),
                        0xFFFF0000
                );

                DrawHelper.drawLine(context,
                        getX() + 1 + getWidth(), getY() + 1 + getHeight(),
                        screenWidth, screenHeight,
                        0xFFFF0000
                );
            }
            case TOP -> {
                context.fill(
                        getX() - 1 + (getWidth() / 2),
                        getY() - 1,
                        getX() + 1 + (getWidth() / 2),
                        getY() + 1,
                        0xFFFF0000
                );

                DrawHelper.drawLine(context,
                        getX() + (getWidth() / 2), getY() - 1,
                        screenWidth / 2, 0,
                        0xFFFF0000
                );
            }
            case BOTTOM -> {
                context.fill(
                        getX() - 1 + (getWidth() / 2),
                        getY() - 1 + getHeight(),
                        getX() + 1 + (getWidth() / 2),
                        getY() + 1 + getHeight(),
                        0xFFFF0000
                );

                DrawHelper.drawLine(context,
                        getX() + (getWidth() / 2), getY() + 1 + getHeight(),
                        screenWidth / 2, screenHeight,
                        0xFFFF0000
                );
            }
            case LEFT -> {
                context.fill(
                        getX() - 1,
                        getY() - 1 + (getHeight() / 2),
                        getX() + 1,
                        getY() + 1 + (getHeight() / 2),
                        0xFFFF0000
                );

                DrawHelper.drawLine(context,
                        getX() - 1, getY() + (getHeight() / 2),
                        0, screenHeight / 2,
                        0xFFFF0000
                );
            }
            case RIGHT -> {
                context.fill(
                        getX() - 1 + getWidth(),
                        getY() - 1 + (getHeight() / 2),
                        getX() + 1 + getWidth(),
                        getY() + 1 + (getHeight() / 2),
                        0xFFFF0000
                );

                DrawHelper.drawLine(context,
                        getX() + 1 + getWidth(), getY() + (getHeight() / 2),
                        screenWidth, screenHeight / 2,
                        0xFFFF0000
                );
            }
        }
    }

    private void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        if(this.isHovered()) {
            List<Text> text = List.of(
                    Text.literal(element.message.getString()).formatted(Formatting.BOLD, Formatting.GOLD),
                    TextHelper.concat(Text.literal("X Position: ").formatted(Formatting.GRAY),
                            Text.literal(String.valueOf(Math.round(element.xPos * 100f)))),
                    TextHelper.concat(Text.literal("Y Position: ").formatted(Formatting.GRAY),
                            Text.literal(String.valueOf(Math.round(element.yPos * 100f)))),
                    TextHelper.concat(Text.literal("Alignment: ").formatted(Formatting.GRAY),
                            Text.literal(element.alignment.toString())),
                    Text.empty(),
                    Text.literal("Hold Left Click and Drag to change position").formatted(Formatting.ITALIC),
                    Text.literal("Right Click to cycle Alignment").formatted(Formatting.ITALIC),
                    Text.literal("Middle Click to open " + element.message.getString() + " config")
                            .formatted(Formatting.ITALIC)

            );

            context.drawTooltip(minecraftClient.textRenderer, text, mouseX, mouseY);
        }
    }

    private void renderBox(DrawContext context) {
        switch (element.alignment) {
            case LEFT, TOP_LEFT, BOTTOM_LEFT -> {
                DrawHelper.drawHorizontalGradient(context,
                        getX() - PADDING_QUART, getY() - PADDING_QUART,
                        getX() + getWidth() + PADDING_QUART, getY() + getHeight() + PADDING_QUART,
                        this.isHovered() ? 0xFFAAAAAA : 0xFF555555,
                        this.isHovered() ? 0x00AAAAAA : 0x00555555);
            }
            case RIGHT, TOP_RIGHT, BOTTOM_RIGHT -> {
                DrawHelper.drawHorizontalGradient(context,
                        getX() - PADDING_QUART, getY() - PADDING_QUART,
                        getX() + getWidth() + PADDING_QUART, getY() + getHeight() + PADDING_QUART,
                        this.isHovered() ? 0x00AAAAAA : 0x00555555,
                        this.isHovered() ? 0xFFAAAAAA : 0xFF555555);
            }
            case TOP -> {
                context.fillGradient(getX() - PADDING_QUART, getY() - PADDING_QUART,
                        getX() + getWidth() + PADDING_QUART, getY() + getHeight() + PADDING_QUART,
                        this.isHovered() ? 0xFFAAAAAA : 0xFF555555,
                        this.isHovered() ? 0x00AAAAAA : 0x00555555);
            }
            case BOTTOM -> {
                context.fillGradient(getX() - PADDING_QUART, getY() - PADDING_QUART,
                        getX() + getWidth() + PADDING_QUART, getY() + getHeight() + PADDING_QUART,
                        this.isHovered() ? 0x00AAAAAA : 0x00555555,
                        this.isHovered() ? 0xFFAAAAAA : 0xFF555555);
            }
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.isMouseOver(mouseX, mouseY)) {
            if (button == 0) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                return true;
            } else if (button == 1) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());

                element.alignment = this.nextAlignment();
                this.setup(element);

                callback.onRelease(Math.round(element.xPos * 100f),
                        Math.round(element.yPos * 100f), element.alignment);
                return false;
            } else if (button == 2) {
                ConfigApiJava.INSTANCE.openScreen(this.config);
            }
        }
        return false;
    }



    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        super.onDrag(mouseX, mouseY, deltaX, deltaY);

        int currentWidth = minecraftClient.getWindow().getScaledWidth();
        int currentHeight = minecraftClient.getWindow().getScaledHeight();

        switch (element.alignment) {
            case LEFT, TOP, BOTTOM, TOP_LEFT, BOTTOM_LEFT-> {
                this.deltaX += deltaX;
            }
            case RIGHT, BOTTOM_RIGHT, TOP_RIGHT-> {
                this.deltaX -= deltaX;
            }
        }
        switch (element.alignment) {
            case TOP_LEFT, TOP, TOP_RIGHT, LEFT, RIGHT -> {
                this.deltaY += deltaY;
            }
            case BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT -> {
                this.deltaY -= deltaY;
            }
        }

        int calculatedPercentX = Math.clamp(Math.round((float) (originalX + this.deltaX) / (float) currentWidth * 100F), 0, 100);
        int calculatedPercentY = Math.clamp(Math.round((float) (originalY + this.deltaY) / (float) currentHeight * 100F), 0, 100);

        element.setXPercent((float) calculatedPercentX / 100F);
        element.setYPercent((float) calculatedPercentY / 100F);

        switch (element.alignment) {
            case LEFT, TOP_LEFT, BOTTOM_LEFT -> {
                this.setX(Math.round(minecraftClient.getWindow().getScaledWidth() * element.xPos));
            }
            case RIGHT, BOTTOM_RIGHT, TOP_RIGHT -> {
                this.setX(minecraftClient.getWindow().getScaledWidth()
                        - Math.round(minecraftClient.getWindow().getScaledWidth() * element.xPos)
                        - element.width);
            }
            case TOP, BOTTOM -> {
                this.setX(Math.round(minecraftClient.getWindow().getScaledWidth() * element.xPos) - (element.width / 2));
            }
        }

        switch (element.alignment) {
            case TOP_LEFT, TOP, TOP_RIGHT -> {
                this.setY(Math.round(minecraftClient.getWindow().getScaledHeight() * element.yPos));
            }
            case BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT -> {
                this.setY(minecraftClient.getWindow().getScaledHeight()
                        - Math.round(minecraftClient.getWindow().getScaledHeight() * element.yPos)
                        - element.height);
            }
            case LEFT, RIGHT -> {
                this.setY(Math.round(minecraftClient.getWindow().getScaledHeight() * element.yPos) - (element.height / 2));
            }
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);

        switch (element.alignment) {
            case LEFT, TOP_LEFT, BOTTOM_LEFT-> {
                this.originalX = getX();
            }
            case RIGHT, BOTTOM_RIGHT, TOP_RIGHT-> {
                this.originalX = minecraftClient.getWindow().getScaledWidth() - (getX() + element.width);
            }
            case TOP, BOTTOM -> {
                this.originalX = getX() + (element.width / 2);
            }
        }

        switch (element.alignment) {
            case TOP_LEFT, TOP, TOP_RIGHT -> {
                this.originalY = getY();
            }
            case BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT -> {
                this.originalY = minecraftClient.getWindow().getScaledHeight() - (getY() + element.height);
            }
            case LEFT, RIGHT -> {
                this.originalY = getY() + (element.height / 2);
            }
        }

        this.deltaX = 0;
        this.deltaY = 0;

        this.callback.onRelease(Math.round(element.xPos * 100f),
                Math.round(element.yPos * 100f),
                element.alignment);
    }

    public interface Callback {
        void onRelease(int xPercent, int yPercent, Alignment alignment);
    }

    private Alignment nextAlignment() {
        int size = alignmentList.size();
        int index = alignmentList.indexOf(element.alignment);
        if(index + 1 != size) {
            return alignmentList.get(index + 1);
        } else {
            return alignmentList.getFirst();
        }
    }
}
