package dannypx.foe.common.minecraft;

import com.google.gson.Gson;
import dannypx.foe.common.io.DataModels;
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
}
