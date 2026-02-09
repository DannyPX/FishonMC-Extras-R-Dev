package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.handler.logic.QuestHandler;
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
    //endregion

    //region Methods
    public void onInit(GenericContainerScreen genericContainerScreen) {
        this.checkIsOfTitle(genericContainerScreen);
    }

    private void checkIsOfTitle(GenericContainerScreen genericContainerScreen) {
        if(Objects.equals(genericContainerScreen.getTitle().getString(), "\uEEE4\uD539")) {
            QuestHandler.instance().checkQuests(genericContainerScreen.getScreenHandler());
        }
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
