package dannypx.foe.type.placeholder;

import net.minecraft.network.chat.Component;

public record ComponentValue(Component value) implements PlaceholderValue {
    @Override
    public String getString() {
        return value.getString();
    }
}
