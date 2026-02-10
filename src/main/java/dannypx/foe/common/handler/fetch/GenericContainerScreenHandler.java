package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.type.Pair;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Objects;

public class GenericContainerScreenHandler {
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
    public void onInit(GenericContainerScreen genericContainerScreen) {
        this.checkIsOfTitle(genericContainerScreen);
    }

    private void checkIsOfTitle(GenericContainerScreen genericContainerScreen) {
        this.lastContainerScreen = genericContainerScreen.getTitle().getString();

        if(Objects.equals(genericContainerScreen.getTitle().getString(), "\uEEE4\uD539")) {
            QuestScreenHandler.instance().checkQuests(genericContainerScreen.getScreenHandler());
        } else if (Objects.equals(genericContainerScreen.getTitle().getString(), "\uEEE4\uD532")) {
            StatsScreenHandler.instance().checkStats(genericContainerScreen.getScreenHandler());
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
