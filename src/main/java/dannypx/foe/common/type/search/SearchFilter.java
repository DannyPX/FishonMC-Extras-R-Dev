package dannypx.foe.common.type.search;

import org.jetbrains.annotations.Nullable;

public class SearchFilter {
    public final String key;
    public final Operator operator;
    public final @Nullable FilterValue value;

    public SearchFilter(String key, Operator operator, @Nullable FilterValue value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }
}
