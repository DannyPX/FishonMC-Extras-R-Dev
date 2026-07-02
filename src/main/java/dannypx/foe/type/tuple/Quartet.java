package dannypx.foe.type.tuple;

import org.jetbrains.annotations.NotNull;

public record Quartet<Type1, Type2, Type3, Type4>(@NotNull Type1 value1, @NotNull Type2 value2, @NotNull Type3 value3, @NotNull Type4 value4) {
    public static <Type1, Type2, Type3, Type4> Quartet<Type1, Type2, Type3, Type4> of(@NotNull Type1 value1, @NotNull Type2 value2, @NotNull Type3 value3, @NotNull Type4 value4) {
        return new Quartet<>(value1, value2, value3, value4);
    }

    public static <Type2, Type3, Type4> Quartet<Boolean, Type2, Type3, Type4> ofTrue(@NotNull Type2 value2, @NotNull Type3 value3, @NotNull Type4 value4) {
        return new Quartet<>(Boolean.TRUE, value2, value3, value4);
    }

    public static <Type2, Type3, Type4> Quartet<Boolean, Type2, Type3, Type4> ofFalse(@NotNull Type2 value2, @NotNull Type3 value3, @NotNull Type4 value4) {
        return Quartet.of(Boolean.FALSE, value2, value3, value4);
    }
}
