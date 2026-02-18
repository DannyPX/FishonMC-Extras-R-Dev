package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.item.FishingRodNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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
    //endregion

    //region Methods
    public void tick() {
        if(minecraftClient.player != null) {
            ItemStack firstSlot = minecraftClient.player.getInventory().main.getFirst();

            if(this.checkFishingRodLoaded(firstSlot)) {
                isLoadingDone = this.scanFish();
            }

            if(isLoadingDone) LoggerHandler.info("Loading Done");
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

        if(validatedFishingRod.v1()) {
            InventoryHandler.instance().setCurrentFishingRod(validatedFishingRod.v2());
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
