package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.logic.ConnectionHandler;
import dannypx.foe.common.handler.logic.KeyBindHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Map;

public class _DebugStore {
    public static List<String> _getHandlers() {
        return List.of(
            ProfileDataHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<MutableText, Tooltip>>> _getFields() {
        return Map.of(
            ProfileDataHandler.class.getName(), ProfileDataHandler.instance()._getFields()
        );
    }
}
