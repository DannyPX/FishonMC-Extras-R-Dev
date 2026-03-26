package dannypx.foe.handler.fetch;

import dannypx.foe.type.tuple.Pair;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Map;

public class _DebugFetch {
    public static List<String> _getHandlers() {
        return List.of(
                TabHandler.class.getName(),
                ClientPlayerHandler.class.getName(),
                ScoreboardHandler.class.getName(),
                BossBarHandler.class.getName(),
                TitleHandler.class.getName(),
                StatsScreenHandler.class.getName(),
                GenericContainerScreenHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<MutableText, MutableText>>> _getFields() {
        return Map.of(
                TabHandler.class.getName(), TabHandler.instance()._getFields(),
                ClientPlayerHandler.class.getName(), ClientPlayerHandler.instance()._getFields(),
                ScoreboardHandler.class.getName(), ScoreboardHandler.instance()._getFields(),
                BossBarHandler.class.getName(), BossBarHandler.instance()._getFields(),
                TitleHandler.class.getName(), TitleHandler.instance()._getFields(),
                StatsScreenHandler.class.getName(), StatsScreenHandler.instance()._getFields(),
                GenericContainerScreenHandler.class.getName(), GenericContainerScreenHandler.instance()._getFields()
        );
    }
}
