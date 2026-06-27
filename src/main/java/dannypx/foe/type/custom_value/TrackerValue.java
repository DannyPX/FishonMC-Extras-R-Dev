package dannypx.foe.type.custom_value;

public sealed interface TrackerValue permits NumberValue, BooleanValue, PlaceholderStringValue, EmptyValue, ErrorValue {
    default TrackerValue setValue(boolean value) { return null; }
    default TrackerValue toggleValue() { return null; }

    default TrackerValue setValue(float value) { return null; }
    default TrackerValue addValue(float value) { return null; }
    default TrackerValue subtractValue(float value) { return null; }

    default TrackerValue setValue(String value) {return null; }
}
