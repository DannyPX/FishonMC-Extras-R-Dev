package dannypx.foe.screens.element.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.fetch.ClientPlayerHandler;
import dannypx.foe.handler.fetch.ScoreboardHandler;
import dannypx.foe.handler.fetch.TabHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.helper.DrawHelper;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.Element;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ProfileElement extends Element {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private static final int TEXTURE_WIDTH = 160;
    private static final int TEXTURE_HEIGHT = 44;

    private static final Identifier PROFILE_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/profile");
    private static final Identifier PROFILE_TEXTURE_FLIP = Identifier.of(FishOnMCExtras.MOD_ID, "elements/profile_flip");
    //endregion

    public ProfileElement(MinecraftClient minecraftClient) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.profileElementXPosition.get() / 100f,
                Configs.hudConfig.profileElementYPosition.get() / 100f,
                Configs.hudConfig.profileElementAlignment.get(),
                Configs.hudConfig.profileElementGroup.translation("Profile Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    public ProfileElement(MinecraftClient minecraftClient, boolean isCopy) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
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
        int scaledWidth = (int) (minecraftClient.getWindow().getScaledWidth() * (1 / Configs.hudConfig.profileElementScale.get()));
        int scaledHeight = (int) (minecraftClient.getWindow().getScaledHeight() * (1 / Configs.hudConfig.profileElementScale.get()));

        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().scale(Configs.hudConfig.profileElementScale.get(), Configs.hudConfig.profileElementScale.get());
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.hudConfig.showProfileElement.get()
                && TabHandler.instance().isInInstance()
        ) {
            // Position
            if(!isCopy) {
                xPos = Configs.hudConfig.profileElementXPosition.get() / 100f;
                yPos = Configs.hudConfig.profileElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.profileElementAlignment.get()) {
                case TOP_LEFT -> Math.round(scaledWidth * xPos);
                case TOP_RIGHT -> scaledWidth
                        - Math.round(scaledWidth * xPos);
                default -> 0;
            };
            int y = Math.round(scaledHeight * yPos);

            this.renderTexture(drawContext, x, y);
            this.renderText(drawContext, textRenderer, x, y);
            this.renderHead(drawContext, x, y);
        }
        drawContext.getMatrices().popMatrix();
    }

    private void renderHead(DrawContext drawContext, int x, int y) {
        if(minecraftClient.player != null) {
            Identifier SKIN_TEXTURE = minecraftClient.player.getSkin().body().texturePath();
            switch (Configs.hudConfig.profileElementAlignment.get()) {
                case TOP_LEFT -> {
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                            SKIN_TEXTURE,
                            x + 8, y + 8,
                            8, 8,
                            21, 21,
                            8, 8,
                            64, 64
                    );

                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                            SKIN_TEXTURE,
                            x + 7, y + 7,
                            40, 8,
                            23, 23,
                            8, 8,
                            64, 64
                    );
                }
                case TOP_RIGHT -> {
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                            SKIN_TEXTURE,
                            x - 8 - 21, y + 8,
                            8, 8,
                            21, 21,
                            8, 8,
                            64, 64
                    );

                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
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

        Text level = ScoreboardHandler.instance().getLevel().getString().isBlank()
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
                Text.literal("LV. ").formatted(Formatting.GRAY),
                level,
                Text.literal(" [").formatted(Formatting.DARK_GRAY),
                progressText,
                progressLeftText,
                Text.literal("]").formatted(Formatting.DARK_GRAY)
        );
        int levelWidth = textRenderer.getWidth(TextHelper.smallText(levelText.getString()));

        int text3x = 48;
        int text3y = 34;

        Text wallet = ScoreboardHandler.instance().getWallet();
        Text walletText = !wallet.getString().isEmpty()
                ? TextHelper.concat(
                        Text.literal("\uF012 "),
                        wallet
                )
                : Text.empty().append("\uF012 ");
        int walletWidth = textRenderer.getWidth(TextHelper.smallText(walletText.getString()));

        int text4x = 110;
        int text4y = 34;

        Text creditsText = TextHelper.concat(
                Text.literal("\uF00C "),
                ScoreboardHandler.instance().getCredits()
        );
        int creditsWidth = textRenderer.getWidth(TextHelper.smallText(creditsText.getString()));

        switch (Configs.hudConfig.profileElementAlignment.get()) {
            case TOP_LEFT -> {
                drawContext.drawText(textRenderer,
                        player,
                        x + text1x, y + text1y,
                        Colors.WHITE,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        levelText,
                        x + text2x, y + text2y,
                        true,
                        true,
                        false,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        walletText,
                        x + text3x, y + text3y,
                        true,
                        true,
                        false,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        creditsText,
                        x + text4x, y + text4y,
                        true,
                        true,
                        false,
                        true);
            }
            case TOP_RIGHT -> {
                drawContext.drawText(textRenderer,
                        player,
                        x - text1x - playerWidth, y + text1y,
                        Colors.WHITE,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        levelText,
                        x - text2x - levelWidth, y + text2y,
                        true,
                        true,
                        false,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        walletText,
                        x - text3x - walletWidth, y + text3y,
                        true,
                        true,
                        false,
                        true);

                DrawHelper.drawText(drawContext, textRenderer,
                        creditsText,
                        x - text4x - creditsWidth, y + text4y,
                        true,
                        true,
                        false,
                        true);
            }
        }
    }

    private void renderTexture(DrawContext drawContext, int x, int y) {
        switch (Configs.hudConfig.profileElementAlignment.get()) {
            case TOP_LEFT -> drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED,
                    PROFILE_TEXTURE,
                    x, y,
                    width, height
            );
            case TOP_RIGHT -> drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED,
                    PROFILE_TEXTURE_FLIP,
                    x - width, y,
                    width, height
            );
        }
    }
    //endregion
}
