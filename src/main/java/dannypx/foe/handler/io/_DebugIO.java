package dannypx.foe.handler.io;

import dannypx.foe.type.tuple.Pair;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.MutableComponent;

public class _DebugIO {
    public static List<String> _getHandlers() {
        return List.of(
                DataFileHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<MutableComponent, MutableComponent>>> _getFields() {
        return Map.of(
                DataFileHandler.class.getName(), DataFileHandler.instance()._getFields()
        );
    }
}
