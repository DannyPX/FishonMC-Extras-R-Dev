package dannypx.foe.screens.element.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.fetch.LocalPlayerHandler;
import dannypx.foe.handler.fetch.ScoreboardHandler;
import dannypx.foe.handler.fetch.TabOverlayHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.helper.ComponentHelper;
import dannypx.foe.helper.GuiGraphicsHelper;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.Element;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

public class ProfileElement extends Element {
    //region Fields
    private final Minecraft minecraft;
    private final Font font;

    private static final int TEXTURE_WIDTH = 160;
    private static final int TEXTURE_HEIGHT = 44;

    private static final Identifier PROFILE_TEXTURE = Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "elements/profile");
    private static final Identifier PROFILE_TEXTURE_FLIP = Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "elements/profile_flip");
    //endregion

    public ProfileElement(Minecraft minecraft) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.profileElementXPosition.get() / 100f,
                Configs.hudConfig.profileElementYPosition.get() / 100f,
                Configs.hudConfig.profileElementAlignment.get(),
                Configs.hudConfig.profileElementGroup.translation("Profile Element"),
                false);
        this.minecraft = minecraft;
        this.font = minecraft.font;
    }

    public ProfileElement(Minecraft minecraft, boolean isCopy) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.profileElementXPosition.get() / 100f,
                Configs.hudConfig.profileElementYPosition.get() / 100f,
                Configs.hudConfig.profileElementAlignment.get(),
                Configs.hudConfig.profileElementGroup.translation("Profile Element"),
                isCopy);
        this.minecraft = minecraft;
        this.font = minecraft.font;
    }

    //region Methods
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int scaledWidth = (int) (minecraft.getWindow().getGuiScaledWidth() * (1 / Configs.hudConfig.profileElementScale.get()));
        int scaledHeight = (int) (minecraft.getWindow().getGuiScaledHeight() * (1 / Configs.hudConfig.profileElementScale.get()));

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(Configs.hudConfig.profileElementScale.get(), Configs.hudConfig.profileElementScale.get());
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.hudConfig.showProfileElement.get()
                && TabOverlayHandler.instance().isInInstance()
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

            this.renderTexture(guiGraphics, x, y);
            this.renderComponent(guiGraphics, font, x, y);
            this.renderHead(guiGraphics, x, y);
        }
        guiGraphics.pose().popMatrix();
    }

    private void renderHead(GuiGraphics guiGraphics, int x, int y) {
        if(minecraft.player != null) {
            Identifier SKIN_TEXTURE = minecraft.player.getSkin().body().texturePath();
            switch (Configs.hudConfig.profileElementAlignment.get()) {
                case TOP_LEFT -> {
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                            SKIN_TEXTURE,
                            x + 8, y + 8,
                            8, 8,
                            21, 21,
                            8, 8,
                            64, 64
                    );

                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                            SKIN_TEXTURE,
                            x + 7, y + 7,
                            40, 8,
                            23, 23,
                            8, 8,
                            64, 64
                    );
                }
                case TOP_RIGHT -> {
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                            SKIN_TEXTURE,
                            x - 8 - 21, y + 8,
                            8, 8,
                            21, 21,
                            8, 8,
                            64, 64
                    );

                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
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

    private void renderComponent(GuiGraphics guiGraphics, Font font, int x, int y) {
        int component1x = 40;
        int component1y = 9;

        Component player = TabOverlayHandler.instance().getPlayerName();
        int playerWidth = font.width(player);

        int component2x = 40;
        int component2y = 21;

        Component level = ScoreboardHandler.instance().getLevel().getString().isBlank()
                ? Component.literal("0").withStyle(ChatFormatting.DARK_GRAY)
                : ScoreboardHandler.instance().getLevel();
        int bars = 20;
        int progress = (int) (bars * LocalPlayerHandler.instance().getExperienceProgress());
        int progressLeft = bars - progress;
        Component progressComponent = Component.literal(" ".repeat(progress))
                .withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.GOLD);
        Component progressLeftComponent = Component.literal(" ".repeat(progressLeft))
                .withStyle(ChatFormatting.STRIKETHROUGH, ChatFormatting.DARK_GRAY);

        Component levelComponent = ComponentHelper.concat(
                Component.literal("LV. ").withStyle(ChatFormatting.GRAY),
                level,
                Component.literal(" [").withStyle(ChatFormatting.DARK_GRAY),
                progressComponent,
                progressLeftComponent,
                Component.literal("]").withStyle(ChatFormatting.DARK_GRAY)
        );
        int levelWidth = font.width(ComponentHelper.smallText(levelComponent.getString()));

        int component3x = 48;
        int component3y = 34;

        Component wallet = ScoreboardHandler.instance().getWallet();
        Component walletComponent = !wallet.getString().isEmpty()
                ? ComponentHelper.concat(
                        Component.literal("\uF012 "),
                        wallet
                )
                : Component.empty().append("\uF012 ");
        int walletWidth = font.width(ComponentHelper.smallText(walletComponent.getString()));

        int component4x = 110;
        int component4y = 34;

        Component creditsComponent = ComponentHelper.concat(
                Component.literal("\uF00C "),
                ScoreboardHandler.instance().getCredits()
        );
        int creditsWidth = font.width(ComponentHelper.smallText(creditsComponent.getString()));

        switch (Configs.hudConfig.profileElementAlignment.get()) {
            case TOP_LEFT -> {
                guiGraphics.drawString(font,
                        player,
                        x + component1x, y + component1y,
                        CommonColors.WHITE,
                        true);

                GuiGraphicsHelper.drawText(guiGraphics, font,
                        levelComponent,
                        x + component2x, y + component2y,
                        true,
                        true,
                        false,
                        true);

                GuiGraphicsHelper.drawText(guiGraphics, font,
                        walletComponent,
                        x + component3x, y + component3y,
                        true,
                        true,
                        false,
                        true);

                GuiGraphicsHelper.drawText(guiGraphics, font,
                        creditsComponent,
                        x + component4x, y + component4y,
                        true,
                        true,
                        false,
                        true);
            }
            case TOP_RIGHT -> {
                guiGraphics.drawString(font,
                        player,
                        x - component1x - playerWidth, y + component1y,
                        CommonColors.WHITE,
                        true);

                GuiGraphicsHelper.drawText(guiGraphics, font,
                        levelComponent,
                        x - component2x - levelWidth, y + component2y,
                        true,
                        true,
                        false,
                        true);

                GuiGraphicsHelper.drawText(guiGraphics, font,
                        walletComponent,
                        x - component3x - walletWidth, y + component3y,
                        true,
                        true,
                        false,
                        true);

                GuiGraphicsHelper.drawText(guiGraphics, font,
                        creditsComponent,
                        x - component4x - creditsWidth, y + component4y,
                        true,
                        true,
                        false,
                        true);
            }
        }
    }

    private void renderTexture(GuiGraphics guiGraphics, int x, int y) {
        switch (Configs.hudConfig.profileElementAlignment.get()) {
            case TOP_LEFT -> guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    PROFILE_TEXTURE,
                    x, y,
                    width, height
            );
            case TOP_RIGHT -> guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    PROFILE_TEXTURE_FLIP,
                    x - width, y,
                    width, height
            );
        }
    }
    //endregion
}
