package dannypx.foe.handler.debug;

import dannypx.foe.handler.fetch._DebugFetch;
import dannypx.foe.handler.logic._DebugLogic;
import dannypx.foe.handler.io._DebugIO;
import dannypx.foe.handler.store._DebugStore;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.tuple.Quartet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.chat.MutableComponent;

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
    private Pair<List<String>, Map<String, Map<String, Pair<MutableComponent, MutableComponent>>>> debugFetch() {
        return Pair.of(_DebugFetch._getHandlers(), _DebugFetch._getFields());
    }
    //debugLogic
    private Pair<List<String>, Map<String, Map<String, Pair<MutableComponent, MutableComponent>>>> debugLogic() {
        return Pair.of(_DebugLogic._getHandlers(), _DebugLogic._getFields());
    }
    //debugIO
    private Pair<List<String>, Map<String, Map<String, Pair<MutableComponent, MutableComponent>>>> debugIO() {
        return Pair.of(_DebugIO._getHandlers(), _DebugIO._getFields());
    }
    //debugStore
    private Pair<List<String>, Map<String, Map<String, Pair<MutableComponent, MutableComponent>>>> debugStore() {
        return Pair.of(_DebugStore._getHandlers(), _DebugStore._getFields());
    }
    //endregion

    //region Methods
    public List<String> _getHandlerNames() {
        return Stream.of(debugFetch().value1(), debugLogic().value1(), debugIO().value1(), debugStore().value1())
                .flatMap(Collection::stream).toList();
    }

    public List<String> _getFieldNames() {
        Stream<Map.Entry<String, Pair<MutableComponent, MutableComponent>>> test = _getFields().values().stream()
                .flatMap(m -> m.entrySet().stream());
        return  test.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).keySet().stream().toList();
    }

    public Map<String, Map<String, Pair<MutableComponent, MutableComponent>>> _getFields() {
        return Stream.of(debugFetch().value2(), debugLogic().value2(),
                        debugIO().value2(), debugStore().value2())
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Quartet<String, String, MutableComponent, MutableComponent> _getField(String handler, String field) {
        Map<String, Pair<MutableComponent, MutableComponent>> fetchedHandler = _getFields().getOrDefault(handler, null);
        if(fetchedHandler != null) {
            Pair<MutableComponent, MutableComponent> fetchedField = fetchedHandler.getOrDefault(field, null);
            if(fetchedField != null) {
                return Quartet.of(handler, field, fetchedField.value1(), fetchedField.value2());
            }
        }
        return null;
    }
    //endregion
}
