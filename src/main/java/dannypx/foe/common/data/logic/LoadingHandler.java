package dannypx.foe.common.data.logic;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.constants.ServerItemId;
import dannypx.foe.common.item.ServerItem;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Map;

public class LoadingHandler {
    private static LoadingHandler INSTANCE = new LoadingHandler();
    public static LoadingHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new LoadingHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private boolean isLoadingDone = false;

    public boolean isLoadingDone() {
        return isLoadingDone;
    }
    //endregion

    //region Methods
    public void tick() {
        if(minecraftClient.player != null) {
            ItemStack firstSlot = minecraftClient.player.getInventory().main.getFirst();
            isLoadingDone = this.checkFishingRodLoaded(firstSlot);
            if(isLoadingDone) LoggerHandler.info("Loading Done");
        }
    }

    public void onJoin() {
        isLoadingDone = false;
        LoggerHandler.info("Loading Started");
    }

    public void onLeave() {
        isLoadingDone = false;
    }

    private boolean checkFishingRodLoaded(ItemStack itemStack) {
        return ServerItem.isServerItem(itemStack, ServerItemId.FISHINGROD);
    }
    //endregion

    //region Dev
    protected Map<String, Pair<Text, Tooltip>> _getFields() {
        return Map.of(
                "isLoadingDone", Pair.of(Text.literal(Boolean.toString(isLoadingDone())), null)
        );
    }
    //endregion
}
