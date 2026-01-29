package dannypx.foe.screens.element;

import dannypx.foe.common.handler.debug._DebugHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Quartet;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class _DebugField extends Element {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private static final int WIDTH = 100;
    private static final int HEIGHT = 16;
    //endregion

    public _DebugField(MinecraftClient minecraftClient) {
        super(WIDTH,
                HEIGHT,
                Configs.debugConfig.debugFieldXPosition.get() / 100f,
                Configs.debugConfig.debugFieldYPosition.get() / 100f,
                Configs.debugConfig.debugFieldAlignment.get(),
                Configs.debugConfig.debugFieldGroup.translation("Debug Field"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    public _DebugField(MinecraftClient minecraftClient, boolean isCopy) {
        super(WIDTH,
                HEIGHT,
                Configs.debugConfig.debugFieldXPosition.get() / 100f,
                Configs.debugConfig.debugFieldYPosition.get() / 100f,
                Configs.debugConfig.debugFieldAlignment.get(),
                Configs.debugConfig.debugFieldGroup.translation("Debug Field"),
                isCopy);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.debugConfig.debugFieldElement.get()
                && Configs.debugConfig.debugMode.get()
        ) {
            // Position
            if(!isCopy) {
                xPercent = Configs.debugConfig.debugFieldXPosition.get() / 100f;
                yPercent = Configs.debugConfig.debugFieldYPosition.get() / 100f;
            }

            int x = switch (Configs.debugConfig.debugFieldAlignment.get()) {
                case TOP_LEFT, BOTTOM_LEFT -> Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                case TOP_RIGHT, BOTTOM_RIGHT -> minecraftClient.getWindow().getScaledWidth()
                        - Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                default -> 0;
            };

            int y = switch (Configs.debugConfig.debugFieldAlignment.get()) {
                case TOP_LEFT, TOP_RIGHT -> Math.round(minecraftClient.getWindow().getScaledHeight() * yPercent);
                case BOTTOM_LEFT, BOTTOM_RIGHT -> minecraftClient.getWindow().getScaledHeight()
                        - Math.round(minecraftClient.getWindow().getScaledHeight() * yPercent);
                default -> 0;
            };

            this.renderText(drawContext, textRenderer, x, y);
        }
    }

    private void renderText(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        Text fieldText;

        Quartet<String, String, MutableText, MutableText> field = _DebugHandler.instance()._getField(
                Configs.debugConfig.debugFieldHandlerChoice.get(),
                Configs.debugConfig.debugFieldFieldChoice.get()
        );

        if(field == null) {
            fieldText = Text.empty().append(Text.literal(TextHelper.smallText("Field and Handler combination does not exist")).formatted(Formatting.RED));
        } else {
            fieldText = TextHelper.concat(
                    Text.literal(TextHelper.smallText("DEBUG ")).formatted(Formatting.RED),
                    Text.literal(field.v2()).formatted(Formatting.GRAY),
                    Text.literal(": ").formatted(Formatting.DARK_GRAY),
                    field.v3()
            );
        }

        int width = textRenderer.getWidth(fieldText);
        int height = textRenderer.fontHeight;

        switch (Configs.debugConfig.debugFieldAlignment.get()) {
            case TOP_LEFT -> {
                DrawHelper.drawText(drawContext, textRenderer, fieldText, x, y, true);
            }
            case TOP_RIGHT -> {
                DrawHelper.drawText(drawContext, textRenderer, fieldText, x - width, y, true);
            }
            case BOTTOM_LEFT -> {
                DrawHelper.drawText(drawContext, textRenderer, fieldText, x, y - height, true);
            }
            case BOTTOM_RIGHT -> {
                DrawHelper.drawText(drawContext, textRenderer, fieldText, x - width, y - height, true);
            }
        }
    }
    //endregion
}
