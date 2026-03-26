package dannypx.foe.type.custom_text;

import net.minecraft.text.Text;

public record TextValue(Text value) implements CustomTextValue {
    @Override
    public String getString() {
        return value.getString();
    }
}
