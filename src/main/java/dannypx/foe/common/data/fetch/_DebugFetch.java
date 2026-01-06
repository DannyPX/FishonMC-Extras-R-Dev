package dannypx.foe.common.data.fetch;

import dannypx.foe.common.type.Pair;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public class _DebugFetch {
    public static List<String> _getHandlers() {
        return List.of(
                TabHandler.class.getName(),
                InventoryHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<Text, Tooltip>>> _getFields() {
        return Map.of(
                TabHandler.class.getName(), TabHandler.instance()._getFields(),
                InventoryHandler.class.getName(), InventoryHandler.instance()._getFields()
        );
    }
}
