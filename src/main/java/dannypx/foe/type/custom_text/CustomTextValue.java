package dannypx.foe.type.custom_text;

public sealed interface CustomTextValue permits TextValue, StringValue {
    String getString();
}
