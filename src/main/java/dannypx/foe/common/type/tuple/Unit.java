package dannypx.foe.common.type.tuple;

import org.jetbrains.annotations.NotNull;

public record Unit<Type1>(@NotNull Type1 value1) {
    public <Type2> Pair<Type1, Type2> add(@NotNull Type2 value2) {
        return Pair.of(this.value1, value2);
    }

    public static <Type1> Unit<Type1> of(@NotNull Type1 value1) {
        return new Unit<>(value1);
    }

    public static Unit<Boolean> ofFalse() {
        return Unit.of(Boolean.FALSE);
    }
}
