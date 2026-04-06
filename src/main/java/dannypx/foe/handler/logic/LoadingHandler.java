package dannypx.foe.handler.logic;

import dannypx.foe.handler.Handler;
import dannypx.foe.item.FishingRodNbtObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.StringValue;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Pattern;

public class LoadingHandler extends Handler {
    private static LoadingHandler INSTANCE = new LoadingHandler();

    public static LoadingHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new LoadingHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private boolean isLoadingDone = false;
    private boolean isError = false;

    public boolean isLoadingDone() {
        return isLoadingDone;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public Pair<Boolean, CustomTextValue> getLoading(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(is_loading_done|is_error)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "is_loading_done" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(isLoadingDone())));
                    case "is_error" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(isError())));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        if(minecraftClient.player != null) {
            ItemStack firstSlot = minecraftClient.player.getInventory().getMainStacks().getFirst();

            if(this.checkFishingRodLoaded(firstSlot)) {
                isLoadingDone = this.scanFish();
            }

            if(isLoadingDone) {
                InventoryHandler.instance().snapshotInventory();
                EventHandler.instance().onJoin();
                LoggerHandler.info("Loading Done");
            }
        }
    }

    public void init() {
        isLoadingDone = false;
        LoggerHandler.info("Loading Started");
    }

    public void onLeave() {
        isLoadingDone = false;
    }

    private boolean checkFishingRodLoaded(ItemStack itemStack) {
        Pair<Boolean, @Nullable FishingRodNbtObject> validatedFishingRod = ValidateItem.isFishingRod(itemStack);

        if(validatedFishingRod.value1()) {
            InventoryHandler.instance().setCurrentFishingRod(validatedFishingRod.value2());
            return true;
        }
        return false;
    }

    private boolean scanFish() {
        return InventoryHandler.instance().trackAllFish();
    }
    //endregion

    //region Dev
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "isLoadingDone", Pair.of(Text.literal(Boolean.toString(isLoadingDone())), Text.empty())
        );
    }
    //endregion
}
