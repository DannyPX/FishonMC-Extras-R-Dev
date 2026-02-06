package dannypx.foe.common.handler.store;

public record Stat<Amount, CaughtOn>(Amount amount, CaughtOn caughtOn) {
    public static <Amount, CaughtOn> Stat<Amount, CaughtOn> of(Amount amount, CaughtOn caughtOn) {
        return new Stat<>(amount, caughtOn);
    }
}
