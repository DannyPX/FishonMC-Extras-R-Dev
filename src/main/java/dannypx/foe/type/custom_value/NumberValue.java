package dannypx.foe.type.custom_value;

public record NumberValue(Float value) implements TrackerValue<Float> {
    @Override
    public TrackerValue<Float> setValue(Float value) {
        return new NumberValue(value);
    }

    @Override
    public TrackerValue<Float> addValue(Float value) {
        return new NumberValue(this.value + value);
    }

    @Override
    public TrackerValue<Float> subtractValue(Float value) {
        return new NumberValue(this.value - value);
    }

    public static TrackerValue<Float> getDefault() {
        return new NumberValue(0f);
    }

    public static TrackerValue<Float> of(Float value) {
        return new NumberValue(value);
    }
}
