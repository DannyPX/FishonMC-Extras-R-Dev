package dannypx.foe.type.custom_value;

public record PlaceholderStringValue(String value) implements TrackerValue<String> {
    @Override
    public TrackerValue<String> setValue(String value) {
        return new PlaceholderStringValue(value);
    }

    public static TrackerValue<String> of(String value) {
        return new PlaceholderStringValue(value);
    }
}
