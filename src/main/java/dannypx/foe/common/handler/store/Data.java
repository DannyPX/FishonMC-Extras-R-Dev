package dannypx.foe.common.handler.store;

public record Data<Amount, CaughtOn>(Amount amount, CaughtOn caughtOn) {
    public static <Amount, CaughtOn> Data<Amount, CaughtOn> of(Amount amount, CaughtOn caughtOn) {
        return new Data<>(amount, caughtOn);
    }
}
