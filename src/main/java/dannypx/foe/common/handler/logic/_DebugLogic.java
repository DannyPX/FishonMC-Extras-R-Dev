package dannypx.foe.common.handler.logic;

import dannypx.foe.common.type.Pair;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Map;

public class _DebugLogic {
    public static List<String> _getHandlers() {
        return List.of(
                ConnectionHandler.class.getName(),
                LoadingHandler.class.getName(),
                KeyBindHandler.class.getName(),
                InventoryHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<MutableText, MutableText>>> _getFields() {
        return Map.of(
                ConnectionHandler.class.getName(), ConnectionHandler.instance()._getFields(),
                LoadingHandler.class.getName(), LoadingHandler.instance()._getFields(),
                KeyBindHandler.class.getName(), KeyBindHandler.instance()._getFields(),
                InventoryHandler.class.getName(), InventoryHandler.instance()._getFields()
        );
    }
}
