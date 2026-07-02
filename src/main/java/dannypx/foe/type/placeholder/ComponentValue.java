package dannypx.foe.type.placeholder;

import net.minecraft.network.chat.Component;

public record ComponentValue(Component value) implements PlaceholderValue {
    @Override
    public String getString() {
        return value.getString();
    }

    public static ComponentValue valueOf(String value) { return new ComponentValue(Component.literal(value)); }

    public static ComponentValue of(Component value) { return new ComponentValue(value); }
}
