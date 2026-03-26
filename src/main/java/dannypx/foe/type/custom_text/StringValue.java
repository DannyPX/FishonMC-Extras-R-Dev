package dannypx.foe.type.custom_text;

public record StringValue(String value) implements CustomTextValue {
    @Override
    public String getString() {
        return value;
    }
}
