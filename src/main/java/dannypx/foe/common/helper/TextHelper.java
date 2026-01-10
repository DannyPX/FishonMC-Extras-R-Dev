package dannypx.foe.common.helper;

import com.google.gson.Gson;
import dannypx.foe.common.handler.io.DataModels;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextHelper {
    private static final Gson gson = new Gson();

    public static MutableText concat(Text... texts) {
        MutableText text = Text.empty();
        for (Text t : texts) {
            text.append(t);
        }
        return text;
    }

    public static MutableText literal(boolean b) {
        return Text.literal(Boolean.toString(b));
    }

    public static MutableText literal(DataModels.DataModel dataModel) {
        return Text.literal(gson.toJson(dataModel));
    }

    public static MutableText literal(int i) {
        return Text.literal(Integer.toString(i));
    }

    public static MutableText literal(float f) {
        return Text.literal(Float.toString(f));
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }
}
