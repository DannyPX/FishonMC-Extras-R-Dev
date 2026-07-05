package dannypx.foe.type.custom_value;

public record BooleanValue(Boolean value) implements TrackerValue<Boolean> {
    @Override
    public TrackerValue<Boolean> setValue(Boolean value) {
        return new BooleanValue(value);
    }

    @Override
    public TrackerValue<Boolean> toggleValue() {
        return new BooleanValue(!(boolean) this.value);
    }

    public static TrackerValue<Boolean> getFalse() {
        return new BooleanValue(false);
    }

    public static TrackerValue<Boolean> getTrue() {
        return new BooleanValue(true);
    }

    public static TrackerValue<Boolean> of(boolean value) {
        return new BooleanValue(value);
    }
}
