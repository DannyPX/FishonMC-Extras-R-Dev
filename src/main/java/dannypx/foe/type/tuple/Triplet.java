package dannypx.foe.type.tuple;

import org.jetbrains.annotations.NotNull;

public record Triplet<Type1, Type2, Type3>(@NotNull Type1 value1, @NotNull Type2 value2, @NotNull Type3 value3) {
    public <Type4> Quartet<Type1, Type2, Type3, Type4> add(@NotNull Type4 value4) {
        return Quartet.of(this.value1, this.value2, this.value3, value4);
    }

    public static <Type1, Type2, Type3> Triplet<Type1, Type2, Type3> of(@NotNull Type1 value1, @NotNull Type2 value2, @NotNull Type3 value3) {
        return new Triplet<>(value1, value2, value3);
    }

    public static <Type2, Type3> Triplet<Boolean, Type2, Type3> ofTrue(@NotNull Type2 value2, @NotNull Type3 value3) {
        return new Triplet<>(Boolean.TRUE, value2, value3);
    }

    public static <Type2, Type3> Triplet<Boolean, Type2, Type3> ofFalse(@NotNull Type2 value2, @NotNull Type3 value3) {
        return Triplet.of(Boolean.FALSE, value2, value3);
    }
}
