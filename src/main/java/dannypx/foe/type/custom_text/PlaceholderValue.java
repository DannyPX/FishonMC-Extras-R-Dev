package dannypx.foe.type.custom_text;

public sealed interface PlaceholderValue permits ComponentValue, StringValue {
    String getString();
}
