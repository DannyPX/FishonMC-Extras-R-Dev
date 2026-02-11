package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.helper.MathHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.PetNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;

public class TooltipHandler {
    private static TooltipHandler INSTANCE = new TooltipHandler();

    public static TooltipHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TooltipHandler();
        }
        return INSTANCE;
    }

    //region Fields
    //endregion

    //region Methods
    public void fetchTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> texts) {
        Pair<Boolean, NbtObject> validatedItem = ValidateItem.isServerItem(itemStack);
        if(validatedItem.v1()) {
            Pair<Boolean, PetNbtObject> validatedPet = ValidateItem.isPet(validatedItem.v2());
            if(validatedPet.v1()) this.setPetPercentages(validatedPet.v2(), texts);

            if(ValidateItem.isAuctionItem(validatedItem.v2())) {
                this.setPricesPerItem(validatedItem.v2(), texts);
            } else if(this.isTackleShopItem(texts)) {
                this.setPricesPerItemRaw(validatedItem.v2(), texts);
            }
        }
    }

    private boolean isTackleShopItem(List<Text> texts) {
        if(texts.size() < NbtObject.SHOP_PRICE_LINE + 2) {
            return false;
        }

        int priceLine = MinecraftClient.getInstance().options.advancedItemTooltips ? NbtObject.SHOP_PRICE_LINE + 2 : NbtObject.SHOP_PRICE_LINE;

        Text priceText = texts.get(texts.size() - priceLine);
        return priceText.getString().contains("Price: $");
    }

    private void setPricesPerItem(NbtObject nbtObject, List<Text> texts) {
        if(texts.size() < NbtObject.SHOP_PRICE_LINE + 2) {
            return;
        }

        if(nbtObject.getCount() > 1) {
            float price = nbtObject.getMoney();

            this.setPrice(nbtObject, texts, price);
        }
    }


    private void setPricesPerItemRaw(NbtObject nbtObject, List<Text> texts) {
        if(texts.size() < NbtObject.SHOP_PRICE_LINE + 2) {
            return;
        }
        int priceLine = MinecraftClient.getInstance().options.advancedItemTooltips ? NbtObject.SHOP_PRICE_LINE + 2 : NbtObject.SHOP_PRICE_LINE;

        Text priceText = texts.get(texts.size() - priceLine).copy();
        float price = TextHelper.toIntFromString(priceText.getString().substring(priceText.getString().indexOf("$") + 1));

        this.setPrice(nbtObject, texts, price);
    }

    private void setPrice(NbtObject nbtObject, List<Text> texts, float price) {
        int priceLine = MinecraftClient.getInstance().options.advancedItemTooltips ? NbtObject.SHOP_PRICE_LINE + 2 : NbtObject.SHOP_PRICE_LINE;

        if(price != 0f) {
            float pricePerItem = price / nbtObject.getCount();
            String pricePerItemString = TextHelper.shortenNumber(pricePerItem, 2);

            Text pricePerItemText = TextHelper.concat(
                    Text.literal(" (").formatted(Formatting.DARK_GRAY),
                    Text.literal("$").formatted(Formatting.DARK_GREEN),
                    Text.literal(pricePerItemString).formatted(Formatting.DARK_GREEN),
                    Text.literal(TextHelper.smallText(" per item")).formatted(Formatting.GRAY),
                    Text.literal(")").formatted(Formatting.DARK_GRAY)
            );


            texts.set(texts.size() - priceLine, TextHelper.concat(
                    texts.get(texts.size() - priceLine),
                    pricePerItemText
            ));
        }
    }

    private void setPetPercentages(PetNbtObject pet, List<Text> texts) {
        Text cBaseLuckText = TextHelper.concat(texts.get(PetNbtObject.C_BASE_LUCK_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getClimatePercentMaxLuck(), 1))).formatted(Formatting.DARK_GRAY);
        Text cBaseScaleText = TextHelper.concat(texts.get(PetNbtObject.C_BASE_SCALE_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getClimatePercentMaxScale(), 1))).formatted(Formatting.DARK_GRAY);
        Text lBaseLuckText = TextHelper.concat(texts.get(PetNbtObject.L_BASE_LUCK_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getLocationPercentMaxLuck(), 1))).formatted(Formatting.DARK_GRAY);
        Text lBaseScaleText = TextHelper.concat(texts.get(PetNbtObject.L_BASE_SCALE_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getLocationPercentMaxScale(), 1))).formatted(Formatting.DARK_GRAY);
        Style style = texts.get(PetNbtObject.RATING_LINE + 1).getSiblings().getLast().getStyle();
        Text totalText = TextHelper.concat(texts.get(PetNbtObject.RATING_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getTotalPercent(), 1))).setStyle(style);

        texts.set(PetNbtObject.C_BASE_LUCK_LINE + 1, cBaseLuckText);
        texts.set(PetNbtObject.C_BASE_SCALE_LINE + 1, cBaseScaleText);
        texts.set(PetNbtObject.L_BASE_LUCK_LINE + 1, lBaseLuckText);
        texts.set(PetNbtObject.L_BASE_SCALE_LINE + 1, lBaseScaleText);
        texts.set(PetNbtObject.RATING_LINE + 1, totalText);
    }

    private Text getPercentText(String percent) {
        return TextHelper.concat(
                Text.literal(" ("),
                Text.literal(percent),
                Text.literal("%)")

        );
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
