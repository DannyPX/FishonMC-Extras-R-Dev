package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.helper.MathHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.PetNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
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
        Pair<Boolean, PetNbtObject> validatedPet = ValidateItem.isPet(itemStack);
        if(validatedPet.v1()) {
            this.setPetPercentages(validatedPet.v2(), texts);
        }
    }

    private void setPetPercentages(PetNbtObject pet, List<Text> texts) {
        Text cBaseLuckText = TextHelper.concat(texts.get(PetNbtObject.C_BASE_LUCK_TOOLTIP_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getClimatePercentMaxLuck(), 1)));
        Text cBaseScaleText = TextHelper.concat(texts.get(PetNbtObject.C_BASE_SCALE_TOOLTIP_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getClimatePercentMaxScale(), 1)));
        Text lBaseLuckText = TextHelper.concat(texts.get(PetNbtObject.L_BASE_LUCK_TOOLTIP_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getLocationPercentMaxLuck(), 1)));
        Text lBaseScaleText = TextHelper.concat(texts.get(PetNbtObject.L_BASE_SCALE_TOOLTIP_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getLocationPercentMaxScale(), 1)));
        Text totalText = TextHelper.concat(texts.get(PetNbtObject.RATING_LINE + 1),
                this.getPercentText(MathHelper.percentToString(pet.getTotalPercent(), 1)));

        texts.set(PetNbtObject.C_BASE_LUCK_TOOLTIP_LINE + 1, cBaseLuckText);
        texts.set(PetNbtObject.C_BASE_SCALE_TOOLTIP_LINE + 1, cBaseScaleText);
        texts.set(PetNbtObject.L_BASE_LUCK_TOOLTIP_LINE + 1, lBaseLuckText);
        texts.set(PetNbtObject.L_BASE_SCALE_TOOLTIP_LINE + 1, lBaseScaleText);
        texts.set(PetNbtObject.RATING_LINE + 1, totalText);
    }

    private Text getPercentText(String percent) {
        return TextHelper.concat(
                Text.literal(" ("),
                Text.literal(percent),
                Text.literal("%)")

        ).formatted(Formatting.DARK_GRAY);
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
