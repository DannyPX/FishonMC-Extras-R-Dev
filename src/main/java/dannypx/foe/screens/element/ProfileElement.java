package dannypx.foe.screens.element;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.fetch.ClientPlayerHandler;
import dannypx.foe.common.handler.fetch.ScoreboardHandler;
import dannypx.foe.common.handler.fetch.TabHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.screens.helper.DrawHelper;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ProfileElement extends Element {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private final Identifier PROFILE_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/profile");
    private final Identifier PROFILE_TEXTURE_FLIP = Identifier.of(FishOnMCExtras.MOD_ID, "elements/profile_flip");
    //endregion

    public ProfileElement(MinecraftClient minecraftClient) {
        super(160,
                44,
                Configs.hudConfig.profileElementXPosition.get() / 100f,
                Configs.hudConfig.profileElementYPosition.get() / 100f,
                Configs.hudConfig.profileElementAlignment.get(),
                Configs.hudConfig.profileElementGroup.translation("Profile Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    public ProfileElement(MinecraftClient minecraftClient, boolean isCopy) {
        super(160,
                44,
                Configs.hudConfig.profileElementXPosition.get() / 100f,
                Configs.hudConfig.profileElementYPosition.get() / 100f,
                Configs.hudConfig.profileElementAlignment.get(),
                Configs.hudConfig.profileElementGroup.translation("Profile Element"),
                isCopy);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if(LoadingHandler.instance().isLoadingDone() && Configs.hudConfig.showProfileElement.get()) {
            // Position
            if(!isCopy) {
                xPercent = Configs.hudConfig.profileElementXPosition.get() / 100f;
                yPercent = Configs.hudConfig.profileElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.profileElementAlignment.get()) {
                case LEFT -> Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                case RIGHT -> minecraftClient.getWindow().getScaledWidth()
                        - Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                default -> 0;
            };
            int y = Math.round(minecraftClient.getWindow().getScaledHeight() * yPercent);

            this.renderTexture(drawContext, x, y);
            this.renderText(drawContext, textRenderer, x, y);
            this.renderHead(drawContext, x, y);
        }
    }

    private void renderHead(DrawContext drawContext, int x, int y) {
        if(minecraftClient.player != null) {
            Identifier SKIN_TEXTURE = minecraftClient.player.getSkinTextures().texture();
            switch (Configs.hudConfig.profileElementAlignment.get()) {
                case LEFT -> {
                    drawContext.drawTexture(RenderLayer::getGuiTextured,
                            SKIN_TEXTURE,
                            x + 8, y + 8,
                            8, 8,
                            21, 21,
                            8, 8,
                            64, 64
                    );

                    drawContext.drawTexture(RenderLayer::getGuiTextured,
                            SKIN_TEXTURE,
                            x + 7, y + 7,
                            40, 8,
                            23, 23,
                            8, 8,
                            64, 64
                    );
                }
                case RIGHT -> {
                    drawContext.drawTexture(RenderLayer::getGuiTextured,
                            SKIN_TEXTURE,
                            x - 8 - 21, y + 8,
                            8, 8,
                            21, 21,
                            8, 8,
                            64, 64
                    );

                    drawContext.drawTexture(RenderLayer::getGuiTextured,
                            SKIN_TEXTURE,
                            x - 7 - 23, y + 7,
                            40, 8,
                            23, 23,
                            8, 8,
                            64, 64
                    );
                }
            }

        }
    }

    private void renderText(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        int text1x = 40;
        int text1y = 9;

        Text player = TabHandler.instance().getPlayerName();
        int playerWidth = textRenderer.getWidth(player);

        int text2x = 40;
        int text2y = 21;

        Text level = ScoreboardHandler.instance().getLevel().getString().isEmpty()
                ? Text.literal("0").formatted(Formatting.DARK_GRAY)
                : ScoreboardHandler.instance().getLevel();
        int bars = 20;
        int progress = (int) (bars * ClientPlayerHandler.instance().getExperienceProgress());
        int progressLeft = bars - progress;
        Text progressText = Text.literal(" ".repeat(progress))
                .formatted(Formatting.STRIKETHROUGH, Formatting.GOLD);
        Text progressLeftText = Text.literal(" ".repeat(progressLeft))
                .formatted(Formatting.STRIKETHROUGH, Formatting.DARK_GRAY);

        Text levelText = TextHelper.concat(
                Text.literal(TextHelper.smallText("LV. ")).formatted(Formatting.GRAY),
                Text.literal(TextHelper.smallText(level.getString())).setStyle(level.getStyle()),
                Text.literal(" [").formatted(Formatting.DARK_GRAY),
                progressText,
                progressLeftText,
                Text.literal("]").formatted(Formatting.DARK_GRAY)
        );
        int levelWidth = textRenderer.getWidth(levelText);

        int text3x = 48;
        int text3y = 34;

        Text wallet = ScoreboardHandler.instance().getWallet();
        Text walletText = !wallet.getString().isEmpty()
                ? TextHelper.concat(
                        Text.literal("\uF012 "),
                        Text.literal(TextHelper.smallText(wallet.getString().substring(1)))
                                .withColor(wallet.getStyle().getColor() != null
                                        ? wallet.getStyle().getColor().getRgb() : 0xFFFFFF)
                )
                : Text.empty().append("\uF012 ");
        int walletWidth = textRenderer.getWidth(walletText);

        int text4x = 110;
        int text4y = 34;

        Text credits = ScoreboardHandler.instance().getCredits();
        Text creditsText = TextHelper.concat(
                Text.literal("\uF00C "),
                Text.literal(TextHelper.smallText(credits.getString()))
                        .withColor(credits.getStyle().getColor() != null
                                ? credits.getStyle().getColor().getRgb() : 0xFFFFFF)
        );
        int creditsWidth = textRenderer.getWidth(creditsText);

        switch (Configs.hudConfig.profileElementAlignment.get()) {
            case LEFT -> {
                drawContext.drawText(textRenderer,
                        player,
                        x + text1x, y + text1y,
                        0xFFFFFF,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        levelText,
                        x + text2x, y + text2y,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        walletText,
                        x + text3x, y + text3y,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        creditsText,
                        x + text4x, y + text4y,
                        true);
            }
            case RIGHT -> {
                drawContext.drawText(textRenderer,
                        player,
                        x - text1x - playerWidth, y + text1y,
                        0xFFFFFF,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        levelText,
                        x - text2x - levelWidth, y + text2y,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        walletText,
                        x - text3x - walletWidth, y + text3y,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        creditsText,
                        x - text4x - creditsWidth, y + text4y,
                        true);
            }
        }
    }

    private void renderTexture(DrawContext drawContext, int x, int y) {
        switch (Configs.hudConfig.profileElementAlignment.get()) {
            case LEFT -> {
                drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                        PROFILE_TEXTURE,
                        x, y,
                        width, height
                );
            }
            case RIGHT -> {
                drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                        PROFILE_TEXTURE_FLIP,
                        x - width, y,
                        width, height
                );
            }
        }
    }
    //endregion
}
