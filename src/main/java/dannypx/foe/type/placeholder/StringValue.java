package dannypx.foe.type.placeholder;

public record StringValue(String value) implements PlaceholderValue {
    @Override
    public String getString() {
        return value;
    }
}
