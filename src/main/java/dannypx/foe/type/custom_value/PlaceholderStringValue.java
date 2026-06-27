package dannypx.foe.type.custom_value;

public record PlaceholderStringValue(String value) implements TrackerValue {
    @Override
    public TrackerValue setValue(String value) {
        return new PlaceholderStringValue(value);
    }

    public static TrackerValue of(String value) {
        return new PlaceholderStringValue(value);
    }
}
