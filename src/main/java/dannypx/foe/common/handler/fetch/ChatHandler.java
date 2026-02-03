package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.type.Pair;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

public class ChatHandler {
    private static ChatHandler INSTANCE = new ChatHandler();

    public static ChatHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatHandler();
        }
        return INSTANCE;
    }

    //region Fields
    //endregion

    //region Methods
    public void onReceiveMessage(Text text) {
        this.checkPet(text);
    }

    //[System] [CHAT] PETS » Equipped your Eagle Pet
    //[System] [CHAT] PETS » Pet unequipped!
    private void checkPet(Text text) {
        if(text.getString().startsWith("PETS » Equipped your")) {
            ProfileDataHandler.instance().updatePet(true);
        } else if (text.getString().startsWith("PETS » Pet unequipped!")) {
            ProfileDataHandler.instance().updatePet(false);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), null)
        );
    }
    //endregion
}
