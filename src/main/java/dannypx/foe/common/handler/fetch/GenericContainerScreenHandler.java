package dannypx.foe.common.handler.fetch;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.FishOnMCExtrasClient;
import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.renderer.AuctionHouseScreenRenderHandler;
import dannypx.foe.common.handler.renderer.ChestScreenRenderHandler;
import dannypx.foe.common.handler.renderer.PersonalVaultScreenRenderHandler;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Objects;

public class GenericContainerScreenHandler extends Handler {
    private static GenericContainerScreenHandler INSTANCE = new GenericContainerScreenHandler();

    public static GenericContainerScreenHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new GenericContainerScreenHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private String lastContainerScreen = "";

    public String getLastContainerScreen() {
        return lastContainerScreen;
    }
    //endregion

    //region Methods
    public void init(GenericContainerScreen genericContainerScreen) {
        if(!Configs.handlerConfig.genericContainerScreenHandler.get()) {
            return;
        }

        this.checkIsOfTitle(genericContainerScreen);
    }

    private void checkIsOfTitle(GenericContainerScreen genericContainerScreen) {
        this.lastContainerScreen = genericContainerScreen.getTitle().getString();

        if(Objects.equals(genericContainerScreen.getTitle().getString(), FishOnMCExtras.QUEST_SCREEN)) {
            QuestScreenHandler.instance().checkQuests(genericContainerScreen.getScreenHandler());
        } else if (Objects.equals(genericContainerScreen.getTitle().getString(), FishOnMCExtras.STATS_SCREEN)) {
            StatsScreenHandler.instance().checkStats(genericContainerScreen.getScreenHandler());
        } else if (Objects.equals(genericContainerScreen.getTitle().getString(), FishOnMCExtras.AUCTION_HOUSE_SCREEN)) {
            AuctionHouseScreenRenderHandler.instance().init(genericContainerScreen);
        } else if (genericContainerScreen.getTitle().getString().startsWith(FishOnMCExtras.PERSONAL_VAULT_SCREEN)) {
            PersonalVaultScreenRenderHandler.instance().init(genericContainerScreen);
        } else if (Objects.equals(genericContainerScreen.getTitle().getString(), FishOnMCExtras.STORAGE_SCREEN)) {
            ChestScreenRenderHandler.instance().init(genericContainerScreen);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "lastContainerScreen", Pair.of(Text.literal(getLastContainerScreen()), Text.empty())
        );
    }
    //endregion
}
