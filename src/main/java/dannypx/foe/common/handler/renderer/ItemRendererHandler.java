package dannypx.foe.common.handler.renderer;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
    //endregion

    //region Methods
    public void drawRarityMarker(DrawContext drawContext, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if(!Configs.rendererConfig.showMarker.get()) {
            return;
        }

        Pair<Boolean, NbtObject> validateItem = ValidateItem.isServerItem(stack);

        if(validateItem.v1()
                && !this.checkIfBlacklisted(validateItem.v2())
                && !validateItem.v2().getRarity().isBlank()
                && !Objects.equals(validateItem.v2().getRarityText(), Text.empty())
                && !validateItem.v2().getRarityText().getString().isBlank()
        ) {
            Text rarityText = Text.literal(validateItem.v2().getRarityText().getString());

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

        int count = validatedItem.v2().getCount();
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
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
        );
    }
    //endregion
}
