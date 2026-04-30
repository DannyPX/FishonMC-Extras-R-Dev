package dannypx.foe.handler.logic;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.store.CustomEventTriggerDataHandler;
import dannypx.foe.type.event.EventTrigger;
import dannypx.foe.type.tuple.Pair;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

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
        if(minecraft.player != null) {
            if(Configs.handlerConfig.openEventsOnJoin.get()) minecraft.player.connection.sendCommand("events");

            this.checkEventTrigger(EventTrigger.ON_JOIN);
        }
    }

    public void onCatch() {
        this.checkEventTrigger(EventTrigger.ON_CATCH);
    }

    public void onCrewJoin() {
        this.checkEventTrigger(EventTrigger.ON_CREW_JOIN);
    }

    public void onCrewLeave() {
        this.checkEventTrigger(EventTrigger.ON_CREW_LEAVE);
    }

    private void checkEventTrigger(EventTrigger eventTrigger) {
        CustomEventTriggerDataHandler.instance().getCustomEventTriggerData().eventTriggerList.forEach((name, event) -> {
            if(event.useEventTrigger && event.event == eventTrigger) {
                NotifierHandler.instance().notifyOnTrigger(event.notificationToTrigger);
                ChatNotifierHandler.instance().notifyChatOnTrigger(event.chatNotificationToTrigger);
            }
        });
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "key", Pair.of(Component.literal("value"), Component.empty())
        );
    }
    //endregion
}
