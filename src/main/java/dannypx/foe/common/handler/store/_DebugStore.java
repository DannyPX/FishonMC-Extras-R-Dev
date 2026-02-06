package dannypx.foe.common.handler.store;

import dannypx.foe.common.type.Pair;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Map;

public class _DebugStore {
    public static List<String> _getHandlers() {
        return List.of(
                ProfileDataHandler.class.getName(),
                StatsDataHandler.class.getName(),
                ConstantDataHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<MutableText, MutableText>>> _getFields() {
        return Map.of(
                ProfileDataHandler.class.getName(), ProfileDataHandler.instance()._getFields(),
                StatsDataHandler.class.getName(), StatsDataHandler.instance()._getFields(),
                ConstantDataHandler.class.getName(), ConstantDataHandler.instance()._getFields()
        );
    }
}
