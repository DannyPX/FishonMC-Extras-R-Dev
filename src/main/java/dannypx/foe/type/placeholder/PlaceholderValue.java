package dannypx.foe.type.placeholder;

public sealed interface PlaceholderValue permits ComponentValue, StringValue {
    String getString();
}
