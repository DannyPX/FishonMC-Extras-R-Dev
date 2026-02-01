package dannypx.foe.screens.element;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.logic.InventoryHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.FishingRodNbtObject;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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

    private final Identifier HOTBAR_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/hotbar");
    private final int HOTBAR_WIDTH = 170;
    private final int HOTBAR_HEIGHT = 26;

    private final Identifier GEAR_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/gear");
    private final int GEAR_WIDTH = 60;
    private final int GEAR_HEIGHT = 24;

    private final Identifier SLOT_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/slot");
    private final int SLOT_WIDTH = 24;
    private final int SLOT_HEIGHT = 24;

    private final Identifier SELECTOR_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "elements/selector");
    private final int SELECTOR_WIDTH = 20;
    private final int SELECTOR_HEIGHT = 24;

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
        if(LoadingHandler.instance().isLoadingDone() && Configs.hudConfig.showHotbarElement.get()) {
            // Position
            if(!isCopy) {
                xPercent = Configs.hudConfig.hotbarElementXPosition.get() / 100f;
                yPercent = Configs.hudConfig.hotbarElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.hotbarElementAlignment.get()) {
                case BOTTOM_LEFT -> Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent);
                case BOTTOM -> Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent) - WIDTH / 2;
                case BOTTOM_RIGHT -> minecraftClient.getWindow().getScaledWidth()
                        - Math.round(minecraftClient.getWindow().getScaledWidth() * xPercent) - WIDTH;
                default -> 0;
            };
            int y = minecraftClient.getWindow().getScaledHeight()
                    - Math.round(minecraftClient.getWindow().getScaledHeight() * yPercent) - HEIGHT;

            this.renderHotbar(drawContext, x, y);
            this.renderSelector(drawContext, x, y);
            this.renderItems(drawContext, textRenderer, x, y);
            this.renderSelectedItemName(drawContext, textRenderer, x, y);
            if(Configs.hudConfig.showHotbarParts.get()) this.renderParts(drawContext, textRenderer, x, y);
            if(Configs.hudConfig.showHotbarArmor.get()) this.renderArmor(drawContext, textRenderer, x, y);
            if(Configs.hudConfig.showHotbarBait.get()) this.renderBait(drawContext, textRenderer, x, y);
        }
    }

    private void renderHotbar(DrawContext drawContext, int x, int y) {
        //region Texture
        int hotbarX = 25;
        int hotbarY = 25;

        drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
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
                ItemStack item = minecraftClient.player.getInventory().main.get(i);

                int count = item.getCount();
                Text countText = TextHelper.literal(TextHelper.smallText(String.valueOf(count)));
                int countWidth = textRenderer.getWidth(countText);

                drawContext.drawItem(item, x + itemX + (18 * i), y + itemY);

                drawContext.getMatrices().push();
                drawContext.getMatrices().translate(0.0F, 0.0F, 200.0F);
                if(count > 1) DrawHelper.drawText(drawContext, textRenderer, countText,
                        x + countX + (18 * i) - countWidth, y + countY,
                        true);
                drawContext.getMatrices().pop();
            }
        }
        //endregion
    }

    private void renderSelector(DrawContext drawContext, int x, int y) {

        if(minecraftClient.player != null) {
            //region Texture
            int selectorX = 29;
            int selectorY = 26;
            int index = minecraftClient.player.getInventory().selectedSlot;

            drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                    SELECTOR_TEXTURE,
                    x + selectorX + (18 * index) - 1, y + selectorY,
                    SELECTOR_WIDTH, SELECTOR_HEIGHT
            );
            //endregion
        }
    }

    private void renderSelectedItemName(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        if(minecraftClient.player != null) {
            int index = minecraftClient.player.getInventory().selectedSlot;
            ItemStack selectedStack = minecraftClient.player.getInventory().main.get(index);

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

    private void renderParts(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        //region Texture
        int gearX = 35;

        drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
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

    private void renderArmor(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        //region Texture
        int gearX = 125;

        drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                GEAR_TEXTURE,
                x + gearX, y,
                GEAR_WIDTH, GEAR_HEIGHT
        );
        //endregion

        //region Items
        if(minecraftClient.player != null) {
            int armorX = 129;
            int armorY = 4;

            ItemStack chestplate = minecraftClient.player.getInventory().armor.get(EquipmentSlot.CHEST.getEntitySlotId());
            ItemStack leggings = minecraftClient.player.getInventory().armor.get(EquipmentSlot.LEGS.getEntitySlotId());
            ItemStack boots = minecraftClient.player.getInventory().armor.get(EquipmentSlot.FEET.getEntitySlotId());

            drawContext.drawItem(chestplate, x + armorX, y + armorY);
            drawContext.drawItem(leggings, x + armorX + 18, y + armorY);
            drawContext.drawItem(boots, x + armorX + 36, y + armorY);
        }
        //endregion
    }

    private void renderBait(DrawContext drawContext, TextRenderer textRenderer, int x, int y) {
        //region Texture
        int baitY = 26;

        drawContext.drawGuiTexture(RenderLayer::getGuiTextured,
                SLOT_TEXTURE,
                x, y + baitY,
                SLOT_WIDTH, SLOT_HEIGHT
        );
        //endregion

        //region Items
        if(minecraftClient.player != null) {
            int partsX = 4;
            int partsY = 30;


            int countX = 21;
            int countY = 41;

            FishingRodNbtObject fishingRodNbtObject = InventoryHandler.instance().getCurrentFishingRod();
            NbtObject bait = fishingRodNbtObject.getTackleBox().isEmpty() ? null : fishingRodNbtObject.getTackleBox().getFirst();
            if(bait != null) {
                int count = bait.getCount();
                Text countText = TextHelper.literal(TextHelper.smallText(TextHelper.shortenNumber(count, 0)));
                int countWidth = textRenderer.getWidth(countText);


                drawContext.drawItem(bait.getItemStack(), x + partsX, y + partsY);

                drawContext.getMatrices().push();
                drawContext.getMatrices().translate(0.0F, 0.0F, 200.0F);
                if(count > 1) DrawHelper.drawText(drawContext, textRenderer, countText,
                        x + countX - countWidth, y + countY,
                        true);
                drawContext.getMatrices().pop();
            }
        }
        //endregion
    }
    //endregion
}
