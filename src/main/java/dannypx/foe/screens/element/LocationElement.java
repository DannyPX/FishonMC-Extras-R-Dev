package dannypx.foe.screens.element;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.fetch.BossBarHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.config.Configs;
import dannypx.foe.common.helper.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class LocationElement extends Element {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private static final int TEXTURE_WIDTH = 160;
    private static final int TEXTURE_HEIGHT = 36;

    private final Identifier LOCATION_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/location");
    private final Identifier LOCATION_TEXTURE_FLIP = Identifier.of(FishOnMCExtras.MOD_ID, "elements/location_flip");
    //endregion

    public LocationElement(MinecraftClient minecraftClient) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.locationElementXPosition.get() / 100f,
                Configs.hudConfig.locationElementYPosition.get() / 100f,
                Configs.hudConfig.locationElementAlignment.get(),
                Configs.hudConfig.locationElementGroup.translation("Location Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    public LocationElement(MinecraftClient minecraftClient, boolean isCopy) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.locationElementXPosition.get() / 100f,
                Configs.hudConfig.locationElementYPosition.get() / 100f,
                Configs.hudConfig.locationElementAlignment.get(),
                Configs.hudConfig.locationElementGroup.translation("Location Element"),
                isCopy);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if(LoadingHandler.instance().isLoadingDone() && Configs.hudConfig.showLocationElement.get()) {
            // Position
            if(!isCopy) {
                xPercent = Configs.hudConfig.locationElementXPosition.get() / 100f;
                yPercent = Configs.hudConfig.locationElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.locationElementAlignment.get()) {
                case TOP_LEFT -> Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                case TOP_RIGHT -> minecraftClient.getWindow().getScaledWidth()
                        - Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                default -> 0;
            };
            int y = Math.round(minecraftClient.getWindow().getScaledHeight() * yPercent);

            this.renderTexture(drawContext, x, y);
            this.renderText(drawContext, textRenderer, x, y);
        }
    }

    private void renderText(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        int text1x = 24;
        int text1y = 7;

        Text temperature = BossBarHandler.instance().getTemperature();
        Text weather = TextHelper.concat(
                BossBarHandler.instance().getWeather(),
                Text.literal(" "),
                Text.literal(TextHelper.smallText(temperature.getString())).setStyle(temperature.getStyle())
        );
        int weatherWidth = textRenderer.getWidth(weather);

        int text2x = 52;
        int text2y = 7;

        Text location = BossBarHandler.instance().getLocation();
        Text locationText = Text.literal(TextHelper.smallText(location.getString())).setStyle(location.getStyle());
        Text subLocation = BossBarHandler.instance().getSubLocation();
        Text subLocationText = Text.literal(TextHelper.smallText(subLocation.getString()))
                .setStyle(subLocation.getStyle());

        Text locationTotal = switch (Configs.hudConfig.locationElementAlignment.get()) {
            case TOP_LEFT -> subLocationText.getString().isBlank() ? TextHelper.concat(locationText) : TextHelper.concat(
                    locationText,
                    Text.literal(" | ").formatted(Formatting.DARK_GRAY),
                    subLocationText
            );
            case TOP_RIGHT -> subLocationText.getString().isBlank() ? TextHelper.concat(locationText) : TextHelper.concat(
                    subLocationText,
                    Text.literal(" | ").formatted(Formatting.DARK_GRAY),
                    locationText
            );
            default -> Text.empty();
        };
        int locationWidth = textRenderer.getWidth(locationTotal);

        int text3x = 16;
        int text3y = 26;

        Text time = BossBarHandler.instance().getTime();
        Text timeText = TextHelper.concat(Text.literal(TextHelper.smallText(time.getString())).setStyle(time.getStyle()));
        int timeWidth = textRenderer.getWidth(timeText);

        switch (Configs.hudConfig.locationElementAlignment.get()) {
            case TOP_LEFT -> {
                DrawHelper.drawText(drawContext, textRenderer,
                        weather,
                        x + text1x - (weatherWidth / 2), y + text1y,
                        true,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        locationTotal,
                        x + text2x, y + text2y,
                        true,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        timeText,
                        x + text3x, y + text3y,
                        true,
                        true);
            }
            case TOP_RIGHT -> {
                DrawHelper.drawText(drawContext, textRenderer,
                        weather,
                        x - text1x - (weatherWidth / 2), y + text1y,
                        true,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        locationTotal,
                        x - text2x - locationWidth, y + text2y,
                        true,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        timeText,
                        x - text3x - timeWidth, y + text3y,
                        true,
                        true);
            }
        }
    }

    private void renderTexture(DrawContext drawContext, int x, int y) {
        switch (Configs.hudConfig.locationElementAlignment.get()) {
            case TOP_LEFT -> {
                drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                        LOCATION_TEXTURE,
                        x, y,
                        width, height
                );
            }
            case TOP_RIGHT -> {
                drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                        LOCATION_TEXTURE_FLIP,
                        x - width, y,
                        width, height
                );
            }
        }
    }
    //endregion
}
