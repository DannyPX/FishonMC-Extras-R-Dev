package dannypx.foe.type.custom_text;

public record StringValue(String value) implements PlaceholderValue {
    @Override
    public String getString() {
        return value;
    }
}
