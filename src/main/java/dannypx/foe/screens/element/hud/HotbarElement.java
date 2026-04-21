package dannypx.foe.screens.element.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.fetch.TabHandler;
import dannypx.foe.handler.logic.InventoryHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.helper.DrawHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.item.FishingRodNbtObject;
import dannypx.foe.item.NbtObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.Element;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public class HotbarElement extends Element {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private static final int WIDTH = 220;
    private static final int HEIGHT = 51;

    private static final Identifier HOTBAR_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/hotbar");
    private static final int HOTBAR_WIDTH = 170;
    private static final int HOTBAR_HEIGHT = 26;

    private static final Identifier GEAR_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/gear");
    private static final int GEAR_WIDTH = 60;
    private static final int GEAR_HEIGHT = 24;

    private static final Identifier SLOT_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/slot");
    private static final int SLOT_WIDTH = 24;
    private static final int SLOT_HEIGHT = 24;

    private static final Identifier SELECTOR_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/selector");
    private static final int SELECTOR_WIDTH = 20;
    private static final int SELECTOR_HEIGHT = 24;

    private int selectedSlot = -1;
    private long heldItemTooltipFade = 0L;
    //endregion

    public HotbarElement(MinecraftClient minecraftClient) {
        super(WIDTH,
                HEIGHT,
                Configs.hudConfig.hotbarElementXPosition.get() / 100f,
                Configs.hudConfig.hotbarElementYPosition.get() / 100f,
                Configs.hudConfig.hotbarElementAlignment.get(),
                Configs.hudConfig.hotbarElementGroup.translation("Hotbar Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    public HotbarElement(MinecraftClient minecraftClient, boolean isCopy) {
        super(WIDTH,
                HEIGHT,
                Configs.hudConfig.hotbarElementXPosition.get() / 100f,
                Configs.hudConfig.hotbarElementYPosition.get() / 100f,
                Configs.hudConfig.hotbarElementAlignment.get(),
                Configs.hudConfig.hotbarElementGroup.translation("Hotbar Element"),
                isCopy);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        int scaledWidth = (int) (minecraftClient.getWindow().getScaledWidth() * (1 / Configs.hudConfig.hotbarElementScale.get()));
        int scaledHeight = (int) (minecraftClient.getWindow().getScaledHeight() * (1 / Configs.hudConfig.hotbarElementScale.get()));

        drawContext.getMatrices().pushMatrix();
        drawContext.getMatrices().scale(Configs.hudConfig.hotbarElementScale.get(), Configs.hudConfig.hotbarElementScale.get());

        if(LoadingHandler.instance().isLoadingDone() && Configs.hudConfig.showHotbarElement.get()) {
            // Position
            if(!isCopy) {
                xPos = Configs.hudConfig.hotbarElementXPosition.get() / 100f;
                yPos = Configs.hudConfig.hotbarElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.hotbarElementAlignment.get()) {
                case BOTTOM_LEFT -> Math.round(scaledWidth * xPos);
                case BOTTOM -> Math.round(scaledWidth * xPos) - WIDTH / 2;
                case BOTTOM_RIGHT -> scaledWidth
                        - Math.round(scaledWidth * xPos) - WIDTH;
                default -> 0;
            };
            int y = scaledHeight
                    - Math.round(scaledHeight * yPos) - HEIGHT;

            this.renderHotbar(drawContext, x, y);
            this.renderSelector(drawContext, x, y);
            this.renderItems(drawContext, textRenderer, x, y);
            this.renderSelectedItemName(drawContext, textRenderer, x, y);
            if(Configs.hudConfig.showHotbarParts.get() && TabHandler.instance().isInInstance()) this.renderParts(drawContext, x, y);
            if(Configs.hudConfig.showHotbarArmor.get() && TabHandler.instance().isInInstance()) this.renderArmor(drawContext, x, y);
            if(Configs.hudConfig.showHotbarBait.get() && TabHandler.instance().isInInstance()) this.renderBait(drawContext, textRenderer, x, y);
        }
        drawContext.getMatrices().popMatrix();
    }

    private void renderHotbar(DrawContext drawContext, int x, int y) {
        //region Texture
        int hotbarX = 25;
        int hotbarY = 25;

        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED,
                HOTBAR_TEXTURE,
                x + hotbarX, y + hotbarY,
                HOTBAR_WIDTH, HOTBAR_HEIGHT
        );
        //endregion
    }

    private void renderItems(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        //region Items
        if(minecraftClient.player != null) {
            int itemX = 30;
            int itemY = 30;

            int countX = 47;
            int countY = 41;

            for(int i = 0; i < 9; i++) {
                ItemStack item = minecraftClient.player.getInventory().getMainStacks().get(i);
                Pair<Boolean, NbtObject> validatedItem = ValidateItem.isServerItem(item);

                drawContext.drawItem(item, x + itemX + (18 * i), y + itemY);

                if(Configs.rendererConfig.useSmallStackCountNumber.get()) {
                    int count = validatedItem.value2().getCount();
                    Text countText = TextHelper.literal(TextHelper.smallText(TextHelper.shortenNumber(count, 0)));
                    int countWidth = textRenderer.getWidth(countText);

                    if(count > 1) DrawHelper.drawText(drawContext, textRenderer, countText,
                            x + countX + (18 * i) - countWidth, y + countY,
                            true,
                            true,
                            false,
                            false);
                } else {
                    int count = item.getCount();
                    Text countText = TextHelper.literal(count);
                    int countWidth = textRenderer.getWidth(countText);

                    if(count > 1) DrawHelper.drawText(drawContext, textRenderer, countText,
                            x + countX + (18 * i) - countWidth, y + countY - 2,
                            true,
                            true,
                            false,
                            false);
                }
            }
        }
        //endregion
    }

    private void renderSelector(DrawContext drawContext, int x, int y) {

        if(minecraftClient.player != null) {
            //region Texture
            int selectorX = 29;
            int selectorY = 26;
            int index = minecraftClient.player.getInventory().getSelectedSlot();

            drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED,
                    SELECTOR_TEXTURE,
                    x + selectorX + (18 * index) - 1, y + selectorY,
                    SELECTOR_WIDTH, SELECTOR_HEIGHT
            );
            //endregion
        }
    }

    private void renderSelectedItemName(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        if(minecraftClient.player != null) {
            int index = minecraftClient.player.getInventory().getSelectedSlot();
            ItemStack selectedStack = minecraftClient.player.getInventory().getMainStacks().get(index);

            int itemNameX = x + (WIDTH / 2);
            int itemNameY = y - textRenderer.fontHeight - 2;
            int width = textRenderer.getWidth(selectedStack.getName());

            if(selectedSlot != index) {
                selectedSlot = index;
                this.heldItemTooltipFade = System.currentTimeMillis();
            }

            itemNameX = itemNameX - (textRenderer.getWidth(selectedStack.getName()) / 2);

            if (selectedStack.isEmpty()) {
                this.heldItemTooltipFade = 0;
            } else if (System.currentTimeMillis() < this.heldItemTooltipFade + 2000L) {
                long time = Math.min(500, this.heldItemTooltipFade + 2000 - System.currentTimeMillis());
                float alpha = Math.min((((float) time) / 500) * 255, 255);
                if(alpha > 5f) {
                    drawContext.drawTextWithBackground(textRenderer, selectedStack.getName(), itemNameX, itemNameY, width, ColorHelper.withAlpha((int) alpha, Colors.WHITE));
                }
            }
        }
    }

    private void renderParts(DrawContext drawContext, int x, int y) {
        //region Texture
        int gearX = 35;

        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED,
                GEAR_TEXTURE,
                x + gearX, y,
                GEAR_WIDTH, GEAR_HEIGHT
        );
        //endregion

        //region Items
        if(minecraftClient.player != null) {
            int partsX = 39;
            int partsY = 4;

            FishingRodNbtObject fishingRodNbtObject = InventoryHandler.instance().getCurrentFishingRod();
            ItemStack reel = fishingRodNbtObject.getReelItem().isEmpty() ? ItemStack.EMPTY : fishingRodNbtObject.getReelItem().getFirst().getItemStack();
            ItemStack pole = fishingRodNbtObject.getPoleItem().isEmpty() ? ItemStack.EMPTY : fishingRodNbtObject.getPoleItem().getFirst().getItemStack();
            ItemStack line = fishingRodNbtObject.getLineItem().isEmpty() ? ItemStack.EMPTY : fishingRodNbtObject.getLineItem().getFirst().getItemStack();

            drawContext.drawItem(reel, x + partsX, y + partsY);
            drawContext.drawItem(pole, x + partsX + 18, y + partsY);
            drawContext.drawItem(line, x + partsX + 36, y + partsY);
        }
        //endregion
    }

    private void renderArmor(DrawContext drawContext, int x, int y) {
        //region Texture
        int gearX = 125;

        drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED,
                GEAR_TEXTURE,
                x + gearX, y,
                GEAR_WIDTH, GEAR_HEIGHT
        );
        //endregion

        //region Items
        if(minecraftClient.player != null) {
            int armorX = 129;
            int armorY = 4;

            ItemStack chestplate = minecraftClient.player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack leggings = minecraftClient.player.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack boots = minecraftClient.player.getEquippedStack(EquipmentSlot.FEET);

            drawContext.drawItem(chestplate, x + armorX, y + armorY);
            drawContext.drawItem(leggings, x + armorX + 18, y + armorY);
            drawContext.drawItem(boots, x + armorX + 36, y + armorY);
        }
        //endregion
    }

    private void renderBait(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        if(minecraftClient.player != null) {
            int partsX = 4;
            int partsY = 30;


            int countX = 21;
            int countY = 41;

            FishingRodNbtObject fishingRodNbtObject = InventoryHandler.instance().getCurrentFishingRod();
            NbtObject bait = fishingRodNbtObject.getTackleBox().isEmpty() ? null : fishingRodNbtObject.getTackleBox().getFirst();
            if(bait != null) {
                //region Texture
                int baitY = 26;

                drawContext.drawGuiTexture(RenderPipelines.GUI_TEXTURED,
                        SLOT_TEXTURE,
                        x, y + baitY,
                        SLOT_WIDTH, SLOT_HEIGHT
                );
                //endregion
                //region Items
                drawContext.drawItem(bait.getItemStack(), x + partsX, y + partsY);

                if(Configs.rendererConfig.useSmallStackCountNumber.get()) {
                    int count = bait.getCount();
                    Text countText = TextHelper.literal(TextHelper.smallText(TextHelper.shortenNumber(count, 0)));
                    int countWidth = textRenderer.getWidth(countText);

                    if(count > 1) DrawHelper.drawText(drawContext, textRenderer, countText,
                            x + countX - countWidth, y + countY,
                            true,
                            true,
                            false,
                            true);

                    if(Configs.hudConfig.showBaitLock.get()
                            && fishingRodNbtObject.getDisableBait()
                    ) {
                        drawContext.drawText(textRenderer, Text.literal("\uD83D\uDD12"), x + 2, y + baitY, Colors.WHITE, true);
                    }
                }
                //endregion
            }
        }
    }
    //endregion
}
