package dannypx.foe.type.custom_value;

public sealed interface TrackerValue<T> permits BooleanValue, EmptyValue, ErrorValue, NumberValue, PlaceholderStringValue {
    default TrackerValue<T> setValue(T value) { return null; }
    default TrackerValue<T> toggleValue() { return null; }

    default TrackerValue<T> addValue(T value) { return null; }
    default TrackerValue<T> subtractValue(T value) { return null; }

}
