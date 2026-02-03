package dannypx.foe.screens.element;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.logic.InventoryHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class PetElement extends Element {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private static final int TEXTURE_WIDTH = 160;
    private static final int TEXTURE_HEIGHT = 37;

    private final Identifier PET_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/pet");
    private final Identifier PET_TEXTURE_FLIP = Identifier.of(FishOnMCExtras.MOD_ID, "elements/pet_flip");
    //endregion

    public PetElement(MinecraftClient minecraftClient) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.petElementXPosition.get() / 100f,
                Configs.hudConfig.petElementYPosition.get() / 100f,
                Configs.hudConfig.petElementAlignment.get(),
                Configs.hudConfig.petElementGroup.translation("Pet Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    public PetElement(MinecraftClient minecraftClient, boolean isCopy) {
        super(TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                Configs.hudConfig.petElementXPosition.get() / 100f,
                Configs.hudConfig.petElementYPosition.get() / 100f,
                Configs.hudConfig.petElementAlignment.get(),
                Configs.hudConfig.petElementGroup.translation("Pet Element"),
                isCopy);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.hudConfig.showPetElement.get()
        ) {
            // Position
            if(!isCopy) {
                xPercent = Configs.hudConfig.petElementXPosition.get() / 100f;
                yPercent = Configs.hudConfig.petElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.petElementAlignment.get()) {
                case TOP_LEFT -> Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                case TOP_RIGHT -> minecraftClient.getWindow().getScaledWidth()
                        - Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                default -> 0;
            };
            int y = Math.round(minecraftClient.getWindow().getScaledHeight() * yPercent);

            this.renderTexture(drawContext, x, y);
            this.renderText(drawContext, textRenderer, x, y);
            this.renderPetIcon(drawContext, x, y);
        }
    }

    private void renderPetIcon(DrawContext drawContext, int x, int y) {
        if(minecraftClient.player != null && InventoryHandler.instance().hasPet()) {
            ItemStack pet = InventoryHandler.instance().getCurrentPet().getItemStack();

            drawContext.getMatrices().push();
            switch (Configs.hudConfig.petElementAlignment.get()) {
                case TOP_LEFT -> {
                    drawContext.getMatrices().translate(x + 7, y + 7, 0);
                    drawContext.getMatrices().scale(1.5f, 1.5f, 1f);
                }
                case TOP_RIGHT -> {
                    drawContext.getMatrices().translate(x - 7 - 24, y + 7, 0);
                    drawContext.getMatrices().scale(1.5f, 1.5f, 1f);
                }
            }
            drawContext.drawItem(pet, 0, 0);
            drawContext.getMatrices().pop();
        }
    }

    private void renderText(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        int text1x = 40;
        int text1y = 9;

        if(InventoryHandler.instance().hasPet()) {
            Text petRarity = TextHelper.concat(
                    Text.literal(InventoryHandler.instance().getCurrentPet().getRarityText()),
                    Text.literal(" ")
            );
            int petRarityWidth = textRenderer.getWidth(petRarity);

            Text pet = TextHelper.literal(InventoryHandler.instance().getCurrentPet().getName());
            int petWidth = textRenderer.getWidth(pet);

            int text2x = 40;
            int text2y = 21;

            Text level = Text.literal(
                    String.valueOf(InventoryHandler.instance().getCurrentPet().getLevel())
            ).formatted(Formatting.GREEN);
            int bars = 20;
            int progress = (int) (bars * InventoryHandler.instance().getCurrentPet().getProgress());
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

            switch (Configs.hudConfig.petElementAlignment.get()) {
                case TOP_LEFT -> {
                    DrawHelper.drawText(drawContext,
                            textRenderer,
                            petRarity,
                            x + text1x, y + text1y,
                            true,
                            true);

                    drawContext.drawText(textRenderer,
                            pet,
                            x + text1x + petRarityWidth, y + text1y,
                            0xFFFFFF,
                            true);

                    DrawHelper.drawText(drawContext, textRenderer,
                            levelText,
                            x + text2x, y + text2y,
                            true);
                }
                case TOP_RIGHT -> {
                    DrawHelper.drawText(drawContext,
                            textRenderer,
                            petRarity,
                            x - text1x - petRarityWidth - petWidth, y + text1y,
                            true,
                            true);

                    drawContext.drawText(textRenderer,
                            pet,
                            x - text1x - petWidth, y + text1y,
                            0xFFFFFF,
                            true);

                    DrawHelper.drawText(drawContext, textRenderer,
                            levelText,
                            x - text2x - levelWidth, y + text2y,
                            true);
                }
            }
        } else {
            Text pet = TextHelper.literal("No pet equipped").formatted(Formatting.GRAY);
            int petWidth = textRenderer.getWidth(pet);

            switch (Configs.hudConfig.petElementAlignment.get()) {
                case TOP_LEFT -> {
                    drawContext.drawText(textRenderer,
                            pet,
                            x + text1x, y + text1y,
                            0xFFFFFF,
                            true);

                }
                case TOP_RIGHT -> {
                    drawContext.drawText(textRenderer,
                            pet,
                            x - text1x - petWidth, y + text1y,
                            0xFFFFFF,
                            true);

                }
            }
        }
    }

    private void renderTexture(DrawContext drawContext, int x, int y) {
        switch (Configs.hudConfig.petElementAlignment.get()) {
            case TOP_LEFT -> {
                drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                        PET_TEXTURE,
                        x, y,
                        width, height
                );
            }
            case TOP_RIGHT -> {
                drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                        PET_TEXTURE_FLIP,
                        x - width, y,
                        width, height
                );
            }
        }
    }
    //endregion
}
