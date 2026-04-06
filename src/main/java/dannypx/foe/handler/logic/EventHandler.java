package dannypx.foe.handler.logic;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.Handler;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

public class EventHandler extends Handler {
    private static EventHandler INSTANCE = new EventHandler();

    public static EventHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new EventHandler();
        }
        return INSTANCE;
    }

    //region Fields

    //endregion

    //region Methods
    public void onJoin() {
        if(minecraftClient.player != null) {
            if(Configs.handlerConfig.openEventsOnJoin.get()) minecraftClient.player.networkHandler.sendChatCommand("events");
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
