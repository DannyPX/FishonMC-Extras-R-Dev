package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.type.Pair;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;

import java.util.List;
import java.util.Map;

public class _DebugFetch {
    public static List<String> _getHandlers() {
        return List.of(
                TabHandler.class.getName(),
                InventoryHandler.class.getName(),
                ClientPlayerHandler.class.getName(),
                ScoreboardHandler.class.getName(),
                BossBarHandler.class.getName()
        );
    }

    /// Handler, Map<Field, Pair<Value, Tooltip>>
    public static Map<String, Map<String, Pair<MutableText, Tooltip>>> _getFields() {
        return Map.of(
                TabHandler.class.getName(), TabHandler.instance()._getFields(),
                InventoryHandler.class.getName(), InventoryHandler.instance()._getFields(),
                ClientPlayerHandler.class.getName(), ClientPlayerHandler.instance()._getFields(),
                ScoreboardHandler.class.getName(), ScoreboardHandler.instance()._getFields(),
                BossBarHandler.class.getName(), BossBarHandler.instance()._getFields()
        );
    }
}
