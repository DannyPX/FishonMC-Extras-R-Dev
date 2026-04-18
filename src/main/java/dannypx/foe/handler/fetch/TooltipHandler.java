package dannypx.foe.handler.fetch;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.KeyBindHandler;
import dannypx.foe.helper.KeyBindHelper;
import dannypx.foe.helper.MathHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.item.ArmorNbtObject;
import dannypx.foe.item.NbtObject;
import dannypx.foe.item.PetNbtObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TooltipHandler extends Handler {
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
        if(validatedItem.value1()) {
            Pair<Boolean, PetNbtObject> validatedPet = ValidateItem.isPet(validatedItem.value2());
            if(validatedPet.value1()) this.setPetPercentages(validatedPet.value2(), texts);

            Pair<Boolean, ArmorNbtObject> validatedArmor = ValidateItem.isArmor(validatedItem.value2());
            if(validatedArmor.value1()) this.setArmorRolls(validatedArmor.value2(), texts);

            if(ValidateItem.isAuctionItem(validatedItem.value2())) {
                this.setPricesPerItem(validatedItem.value2(), texts);
            } else if(this.isTackleShopItem(texts)) {
                this.setPricesPerItemRaw(validatedItem.value2(), texts);
            }
        } else {
            if(itemStack.getItem() == Items.ENDER_EYE
                    && itemStack.get(DataComponentTypes.LORE) != null
                    && itemStack.get(DataComponentTypes.LORE).lines().get(0).getString().contains("Bonus Slot")
            ) {
                this.setArmorRoll(itemStack, texts);
            }
        }
    }

    private void setArmorRoll(ItemStack itemStack, List<Text> texts) {
        for (int i = 0; i < ArmorRollScreenHandler.instance().getRollList().size(); i++) {
            ItemStack listItem = ArmorRollScreenHandler.instance().getRollList().get(i);

            if(ItemStack.areItemsAndComponentsEqual(listItem, itemStack)) {
                Text border = NbtObject.getBorderText(itemStack);
                Text borderText = Text.literal(border.getString().trim())
                        .setStyle(border.getStyle())
                        .append("   ");

                int sizeOfLine = -1;
                for (int j = 0; j < texts.size(); j++) {
                    if(texts.get(j).getString().contains("(+")
                            && texts.get(j).getString().contains("%)")
                    ) sizeOfLine = j;
                }

                if(sizeOfLine != -1) {
                    ArmorNbtObject armor = ArmorRollScreenHandler.instance().getArmor();

                    int rolls = armor.getArmorRollRolls(i);
                    int tier = i + 1;
                    int money = ArmorNbtObject.calculateMoneyRolls(rolls, tier);

                    Text moneyRoll = TextHelper.concat(
                            borderText,
                            Text.literal(TextHelper.smallText("rolls: ")).formatted(Formatting.GRAY),
                            Text.literal(String.valueOf(rolls - 1)).formatted(Formatting.YELLOW),
                            Text.literal("x ").formatted(Formatting.WHITE),
                            Text.literal(TextHelper.smallText("| spent: ")).formatted(Formatting.GRAY),
                            Text.literal("$" + TextHelper.shortenNumber(money, 2)).formatted(Formatting.GREEN)
                    );

                    texts.add(sizeOfLine + 2, moneyRoll);
                }
            }
        }
    }

    private void setArmorRolls(ArmorNbtObject armorNbtObject, List<Text> texts) {
        Text border = Text.literal(armorNbtObject.getBorderText().getString().trim())
                .setStyle(armorNbtObject.getBorderText().getStyle())
                .append("   ");

        int tierLine = -1;
        int rightClickLine = -1;
        int qualityLine = -1;
        Text seperatorText = Text.empty();

        for (int i = 0; i < texts.size(); i++) {
            if(texts.get(i).getString().contains("Tier: ")) tierLine = i;
            if(texts.get(i).getString().contains("ʀɪɢʜᴛ ᴄʟɪᴄᴋ ᴛᴏ ʀᴏʟʟ ʙᴏɴᴜsᴇs")) rightClickLine = i;
            if(texts.get(i).getString().contains("ꞯᴜᴀʟɪᴛʏ: ")
                    || texts.get(i).getString().contains("ᴜᴀʟɪᴛʏ ")
            ) qualityLine = i;
            if(Objects.equals(seperatorText, Text.empty())
                    && texts.get(i).getString().contains("                                               ")
                    && texts.get(i).getSiblings().get(1).getStyle().isStrikethrough()
            ) seperatorText = texts.get(i);
        }

        if(rightClickLine != -1
                && armorNbtObject.isIdentified()
        ) {
            Text inspectText = TextHelper.concat(
                    Text.literal(TextHelper.smallText("Hold ")),
                    Text.literal(TextHelper.smallText(KeyBindHelper.getKeyText(Configs.keyBindConfig.inspectKeybind))),
                    Text.literal(TextHelper.smallText(" to see more info"))
            ).formatted(Formatting.DARK_GRAY);
            Text rollHintText = TextHelper.concat(
                    border,
                    inspectText
            );

            texts.add(rightClickLine + 1, rollHintText);
        }

        if(KeyBindHandler.instance().isPressingInspect()) {
            if(tierLine != -1) {
                for (int i = 4; i >= 0; i--) {
                    if(armorNbtObject.isArmorRollUnlocked(i)
                            && armorNbtObject.isArmorRollRolled(i)
                    ) {
                        int rolls = armorNbtObject.getArmorRollRolls(i);
                        int tier = i + 1;
                        int money = ArmorNbtObject.calculateMoneyRolls(rolls, tier);

                        Text moneyRoll = TextHelper.concat(
                                border,
                                Text.literal(TextHelper.smallText("  └ rolls: ")).formatted(Formatting.GRAY),
                                Text.literal(String.valueOf(rolls - 1)).formatted(Formatting.YELLOW),
                                Text.literal("x ").formatted(Formatting.WHITE),
                                Text.literal(TextHelper.smallText("| spent: ")).formatted(Formatting.GRAY),
                                Text.literal("$" + TextHelper.shortenNumber(money, 2)).formatted(Formatting.GREEN)
                        );

                        texts.add(tierLine + tier + 1, moneyRoll);
                    }
                }
            }

            if(qualityLine != -1
                    && armorNbtObject.isIdentified()
            ) {
                String username = null;

                if(armorNbtObject.getPlayerUUID() != null) {
                    username = GameProfileHandler.instance().getUsername(armorNbtObject.getPlayerUUID());
                }

                Text identifierrText = TextHelper.concat(
                        border,
                        Text.literal("Identifier:").formatted(Formatting.GRAY)
                );

                Text usernameText = TextHelper.concat(
                        border,
                        Text.literal(TextHelper.smallText("  Player: ")).formatted(Formatting.GRAY),
                        username != null ? Text.literal(username).formatted(Formatting.YELLOW)
                                : Text.literal("Loading").formatted(Formatting.DARK_GRAY)
                );

                texts.add(qualityLine + 1, usernameText);
                texts.add(qualityLine + 1, identifierrText);
                texts.add(qualityLine + 1, seperatorText);
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
