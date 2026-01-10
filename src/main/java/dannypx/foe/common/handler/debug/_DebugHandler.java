package dannypx.foe.common.handler.debug;

import dannypx.foe.common.handler.fetch._DebugFetch;
import dannypx.foe.common.handler.logic._DebugLogic;
import dannypx.foe.common.handler.io._DebugIO;
import dannypx.foe.common.handler.store._DebugStore;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class _DebugHandler {
    private static _DebugHandler INSTANCE = new _DebugHandler();

    public static _DebugHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new _DebugHandler();
        }
        return INSTANCE;
    }

    //region Fields
    /// Pair(LIST_OF_HANDLER_NAMES, MAP(HANDLER_NAME, FIELD_AND_TOOLTIP))
    //debugFetch
    private Pair<List<String>, Map<String, Map<String, Pair<MutableText, Tooltip>>>> debugFetch() {
        return Pair.of(_DebugFetch._getHandlers(), _DebugFetch._getFields());
    }
    //debugLogic
    private Pair<List<String>, Map<String, Map<String, Pair<MutableText, Tooltip>>>> debugLogic() {
        return Pair.of(_DebugLogic._getHandlers(), _DebugLogic._getFields());
    }
    //debugIO
    private Pair<List<String>, Map<String, Map<String, Pair<MutableText, Tooltip>>>> debugIO() {
        return Pair.of(_DebugIO._getHandlers(), _DebugIO._getFields());
    }
    //debugStore
    private Pair<List<String>, Map<String, Map<String, Pair<MutableText, Tooltip>>>> debugStore() {
        return Pair.of(_DebugStore._getHandlers(), _DebugStore._getFields());
    }
    //endregion

    //region Methods
    public List<String> _getHandlers() {
        return Stream.of(debugFetch().v1(), debugLogic().v1(), debugIO().v1(), debugStore().v1())
                .flatMap(Collection::stream).toList();
    }

    public Map<String, Map<String, Pair<MutableText, Tooltip>>> _getFields() {
        return Stream.of(debugFetch().v2(), debugLogic().v2(),
                        debugIO().v2(), debugStore().v2())
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    //endregion
}
