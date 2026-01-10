package dannypx.foe.common.handler.debug;

import dannypx.foe.common.handler.fetch._DebugFetch;
import dannypx.foe.common.handler.logic._DebugLogic;
import dannypx.foe.common.handler.io._DebugIO;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
    private final Pair<List<String>, Map<String, Map<String, Pair<MutableText, Tooltip>>>> debugFetch =
            Pair.of(_DebugFetch._getHandlers(), _DebugFetch._getFields());
    //debugLogic
    private final Pair<List<String>, Map<String, Map<String, Pair<MutableText, Tooltip>>>> debugLogic =
            Pair.of(_DebugLogic._getHandlers(), _DebugLogic._getFields());
    //debugIO
    private final Pair<List<String>, Map<String, Map<String, Pair<MutableText, Tooltip>>>> debugIO =
            Pair.of(_DebugIO._getHandlers(), _DebugIO._getFields());

    //handlers
    List<String> _handlers = Stream.of(debugFetch.v1(), debugLogic.v1(), debugIO.v1())
            .flatMap(Collection::stream).toList();

    //fields
    Map<String, Map<String, Pair<MutableText, Tooltip>>> _fields = Stream.of(debugFetch.v2(), debugLogic.v2(),  debugIO.v2())
            .flatMap(m -> m.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public List<String> _getHandlers() {
        return _handlers;
    }

    public Map<String, Map<String, Pair<MutableText, Tooltip>>> _getFields() {
        return _fields;
    }
    //endregion

    //region Methods
    //endregion
}
