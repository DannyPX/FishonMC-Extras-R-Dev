package dannypx.foe.handler.store;

import dannypx.foe.type.tuple.Pair;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.MutableComponent;

public class _DebugStore {
    public static List<String> _getHandlers() {
        return List.of(
                ProfileDataHandler.class.getName(),
                StatsDataHandler.class.getName(),
                ConstantDataHandler.class.getName(),
                QuestDataHandler.class.getName(),
                CrewDataHandler.class.getName(),
                CustomHudDataHandler.class.getName(),
                CustomButtonDataHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<MutableComponent, MutableComponent>>> _getFields() {
        return Map.of(
                ProfileDataHandler.class.getName(), ProfileDataHandler.instance()._getFields(),
                StatsDataHandler.class.getName(), StatsDataHandler.instance()._getFields(),
                ConstantDataHandler.class.getName(), ConstantDataHandler.instance()._getFields(),
                QuestDataHandler.class.getName(), QuestDataHandler.instance()._getFields(),
                CrewDataHandler.class.getName(), CrewDataHandler.instance()._getFields(),
                CustomHudDataHandler.class.getName(), CustomHudDataHandler.instance()._getFields(),
                CustomButtonDataHandler.class.getName(), CustomButtonDataHandler.instance()._getFields()
        );
    }
}
