package dannypx.foe.type.custom_value;

public record NumberValue(float value) implements TrackerValue {
    @Override
    public TrackerValue setValue(float value) {
        return new NumberValue(value);
    }

    @Override
    public TrackerValue addValue(float value) {
        return new NumberValue(this.value + value);
    }

    @Override
    public TrackerValue subtractValue(float value) {
        return new NumberValue(this.value - value);
    }

    public static TrackerValue getDefault() {
        return new NumberValue(0);
    }

    public static TrackerValue of(float value) {
        return new NumberValue(value);
    }
}
