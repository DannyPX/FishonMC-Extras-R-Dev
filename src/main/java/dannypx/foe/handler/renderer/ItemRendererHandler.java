package dannypx.foe.handler.renderer;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.SearchHandler;
import dannypx.foe.helper.DrawHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.item.NbtObject;
import dannypx.foe.item.PetNbtObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemRendererHandler extends Handler {
    private static ItemRendererHandler INSTANCE = new ItemRendererHandler();

    public static ItemRendererHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemRendererHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final Identifier petItemMarker = Identifier.of(FishOnMCExtras.MOD_ID, "icons/pet_item");
    //endregion

    //region Methods
    public void drawRarityMarker(DrawContext drawContext, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if(!Configs.rendererConfig.showRarityMarker.get()) {
            return;
        }

        Pair<Boolean, NbtObject> validateItem = ValidateItem.isServerItem(stack);

        if(validateItem.value1()
                && !this.checkIfBlacklisted(validateItem.value2())
                && !validateItem.value2().getRarity().isBlank()
                && !Objects.equals(validateItem.value2().getRarityText(), Text.empty())
                && !validateItem.value2().getRarityText().getString().isBlank()
        ) {
            Text rarityText = Text.literal(validateItem.value2().getRarityText().getString());

            int markerX = x;
            int markerY = y - 1;

            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.0F, 0.0F, 200.0F);

            //TOP
            int bgX = markerX;
            int bgY = markerY - 1;
            drawContext.enableScissor(bgX, bgY + 2, bgX + 2, bgY + 4);
            drawContext.drawText(textRenderer, rarityText, bgX, bgY, 0xAAAAAA, false);
            drawContext.disableScissor();

            //BOTTOM
            bgY = markerY + 1;
            drawContext.enableScissor(bgX, bgY + 2, bgX + 2, bgY + 4);
            drawContext.drawText(textRenderer, rarityText, bgX, bgY, 0xAAAAAA, false);
            drawContext.disableScissor();

            //LEFT
            bgX = markerX - 1;
            bgY = markerY;
            drawContext.enableScissor(bgX, bgY + 2, bgX + 2, bgY + 4);
            drawContext.drawText(textRenderer, rarityText, bgX, bgY, 0xAAAAAA, false);
            drawContext.disableScissor();

            //RIGHT
            bgX = markerX + 1;
            drawContext.enableScissor(bgX, bgY + 2, bgX + 2, bgY + 4);
            drawContext.drawText(textRenderer, rarityText, bgX, bgY, 0xAAAAAA, false);
            drawContext.disableScissor();

            drawContext.enableScissor(markerX, bgY + 2, markerX + 2, bgY + 4);
            drawContext.drawText(textRenderer, rarityText, markerX, markerY, 0xFFFFFF, false);
            drawContext.disableScissor();

            drawContext.getMatrices().pop();
        }
    }

    private boolean checkIfBlacklisted(NbtObject nbtObject) {
        if(!Configs.rendererConfig.blackListItems.get().isBlank()) {
            List<String> blacklistedItems = Arrays.stream(Configs.rendererConfig.blackListItems.get().split(",")).map(String::trim).toList();
            return blacklistedItems.contains(nbtObject.getType());
        }
        return false;
    }

    public void drawStackCount(DrawContext drawContext, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        Pair<Boolean, NbtObject> validatedItem = ValidateItem.isServerItem(stack);

        int count = validatedItem.value2().getCount();
        Text countText = TextHelper.literal(TextHelper.smallText(TextHelper.shortenNumber(count, 0)));
        int countWidth = textRenderer.getWidth(countText);

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0.0F, 0.0F, 200.0F);
        if(count > 1) DrawHelper.drawText(drawContext, textRenderer, countText,
                x + 19 - 2 - countWidth, y + 6 + 4,
                true,
                true,
                false,
                true
        );
        drawContext.getMatrices().pop();
    }

    public void drawSearchItem(DrawContext drawContext, ItemStack stack, int x, int y) {
        if(SearchHandler.instance().isOnScreen()
                && SearchHandler.instance().filterItem(stack)) {
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.0F, 0.0F, 180.0F);

            drawContext.drawBorder(x, y, 16, 16, Colors.RED);

            drawContext.getMatrices().pop();
        }
    }


    public void drawPetItemEquipped(DrawContext drawContext, ItemStack stack, int x, int y) {
        if(!Configs.rendererConfig.showPetEquippedMarker.get()) {
            return;
        }

        Pair<Boolean, PetNbtObject> validatedPet = ValidateItem.isPet(stack);

        if(validatedPet.value1() && (validatedPet.value2().contains(PetNbtObject.ITEM) || validatedPet.value2().contains(PetNbtObject.SKIN))) {
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.0F, 0.0F, 200.0F);
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, petItemMarker, x, y, 16, 16, 0xFFFFFFFF);
            drawContext.getMatrices().pop();
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
        );
    }
    //endregion
}
