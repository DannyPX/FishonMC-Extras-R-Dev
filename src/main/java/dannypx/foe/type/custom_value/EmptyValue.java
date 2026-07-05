package dannypx.foe.type.custom_value;

public record EmptyValue() implements TrackerValue<Object> {
    public static TrackerValue<Object> getDefault() {
        return new EmptyValue();
    }
}
