package dannypx.foe.placeholder.functions;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

public class PlaceholderValue {
    @Nullable
    private final String stringValue;
    @Nullable
    private final MutableComponent componentValue;
    @Nullable
    private final Number numberValue;

    private PlaceholderValue(String stringValue, MutableComponent componentValue, Number numberValue) {
        this.stringValue = stringValue;
        this.componentValue = componentValue;
        this.numberValue = numberValue;
    }

    public static PlaceholderValue text(String s) {
        return new PlaceholderValue(s, null, null);
    }

    public static PlaceholderValue component(MutableComponent c) {
        return new PlaceholderValue(null, c, null);
    }

    public static PlaceholderValue number(Number n) {
        return new PlaceholderValue(null, null, n);
    }

    public boolean isNull() {
        return stringValue == null && componentValue == null && numberValue == null;
    }

    public boolean isString() {
        return stringValue != null;
    }

    public boolean isComponent() {
        return componentValue != null;
    }

    public boolean isNumber() {
        return numberValue != null;
    }

    public boolean isEmpty() {
        return !isNull() && this.toString().isEmpty();
    }

    public MutableComponent toComponent() {
        if(this.isComponent()) return componentValue;
        return Component.literal(this.toString());
    }

    @Override
    public String toString() {
        if(this.isString()) return stringValue;
        if(this.isComponent()) return componentValue.getString();
        if(this.isNumber()) return PlaceholderValue.formatNumber(numberValue);
        return "";
    }

    public double toDouble() {
        if(this.isNumber()) return numberValue.doubleValue();
        try {
            return Double.parseDouble(this.toString());
        } catch (NumberFormatException ignored) {
            return 0.0d;
        }
    }

    private static String formatNumber(Number n) {
        double d = n.doubleValue();
        if(d == Math.floor(d) && !Double.isInfinite(d)) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }
}
