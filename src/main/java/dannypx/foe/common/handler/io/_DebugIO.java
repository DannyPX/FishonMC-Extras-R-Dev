package dannypx.foe.common.handler.io;

import dannypx.foe.common.type.Pair;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Map;

public class _DebugIO {
    public static List<String> _getHandlers() {
        return List.of(
                DataFileHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<MutableText, Tooltip>>> _getFields() {
        return Map.of(
                DataFileHandler.class.getName(), DataFileHandler.instance()._getFields()
        );
    }
}
