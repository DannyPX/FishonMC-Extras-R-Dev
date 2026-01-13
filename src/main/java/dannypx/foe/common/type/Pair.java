package dannypx.foe.common.type;

import org.jetbrains.annotations.Nullable;

public record Pair<V1, V2>(V1 v1, V2 v2) {
    public static <V1, V2> Pair<V1, V2> of(V1 v1, V2 v2) {
        return new Pair<>(v1, v2);
    }

    public static <V2> Pair<Boolean, V2> nullableFalse() {
        return new Pair<>(Boolean.FALSE, null);
    }
}

