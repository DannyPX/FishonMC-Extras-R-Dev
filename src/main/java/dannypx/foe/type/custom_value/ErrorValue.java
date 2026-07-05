package dannypx.foe.type.custom_value;

public record ErrorValue() implements TrackerValue<Object> {
    public static TrackerValue<Object> getDefault() {
        return new ErrorValue();
    }
}
