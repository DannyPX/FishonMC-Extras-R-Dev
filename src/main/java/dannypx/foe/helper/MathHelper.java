package dannypx.foe.helper;

public class MathHelper {
    public static String percentToString(float percent, int decimals) {
        return TextHelper.floatToString(percent * 100f, decimals);
    }
}
