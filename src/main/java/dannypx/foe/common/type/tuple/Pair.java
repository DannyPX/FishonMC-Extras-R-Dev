package dannypx.foe.common.type.tuple;

import org.jetbrains.annotations.NotNull;

public record Pair<Type1, Type2>(@NotNull Type1 value1, @NotNull Type2 value2) {
    public <Type3> Triplet<Type1, Type2, Type3> add(@NotNull Type3 value3) {
        return Triplet.of(this.value1, this.value2, value3);
    }

    public static <Type1, Type2> Pair<Type1, Type2> of(@NotNull Type1 value1, @NotNull Type2 value2) {
        return new Pair<>(value1, value2);
    }

    public static <Type2> Pair<Boolean, Type2> of(@NotNull Type2 value2) {
        return new Pair<>(Boolean.TRUE, value2);
    }

    public static <Type> Pair<Boolean, Type> ofFalse(@NotNull Type value) {
        return Pair.of(Boolean.FALSE, value);
    }
}

