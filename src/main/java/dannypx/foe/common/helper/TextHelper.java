package dannypx.foe.common.helper;

import com.google.gson.Gson;
import com.mojang.serialization.JsonOps;
import dannypx.foe.common.handler.io.DataModels;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

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

    public static MutableText literal(Text text) {
        return concat(text);
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public static String smallText(String string) {
        return smallLetter(smallNumber(string));
    }

    public static String smallNumber(String string) {
        // based on numeric ping
        char[] characters = new char[string.length()];

        for (int index = 0; index < string.length(); index++) {
            characters[index] = string.charAt(index);

            if (isNumber(characters[index]))
                characters[index] = smallNumber(characters[index]);
        }

        return String.valueOf(characters);
    }

    public static char smallNumber(char c) {
        return (char) (c + 8272);
    }

    public static String smallLetter(String string) {
        // based on numeric ping
        char[] characters = new char[string.length()];

        for (int index = 0; index < string.length(); index++) {
            characters[index] = string.charAt(index);

            characters[index] = smallLetter(characters[index]);
        }

        return String.valueOf(characters);
    }

    public static char smallLetter(char c) {
        return switch (c) {
            case 'A', 'a' -> 'ᴀ';
            case 'B', 'b' -> 'ʙ';
            case 'C', 'c' -> 'ᴄ';
            case 'D', 'd' -> 'ᴅ';
            case 'E', 'e' -> 'ᴇ';
            case 'F', 'f' -> 'ꜰ';
            case 'G', 'g' -> 'ɢ';
            case 'H', 'h' -> 'ʜ';
            case 'I', 'i' -> 'ɪ';
            case 'J', 'j' -> 'ᴊ';
            case 'K', 'k' -> 'ᴋ';
            case 'L', 'l' -> 'ʟ';
            case 'M', 'm' -> 'ᴍ';
            case 'N', 'n' -> 'ɴ';
            case 'O', 'o' -> 'ᴏ';
            case 'P', 'p' -> 'ᴘ';
            case 'Q', 'q' -> 'ꞯ';
            case 'R', 'r' -> 'ʀ';
            case 'S', 's' -> 'ꜱ';
            case 'T', 't' -> 'ᴛ';
            case 'U', 'u' -> 'ᴜ';
            case 'V', 'v' -> 'ᴠ';
            case 'W', 'w' -> 'ᴡ';
            case 'X', 'x' -> 'x';
            case 'Y', 'y' -> 'ʏ';
            case 'Z', 'z' -> 'ᴢ';
            default -> c;
        };
    }

    public static boolean isNumber(char c) {
        return (c >= '0' && c <= '9');
    }

    public static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    public static boolean isSmallNumber(char c) {
        return (c >= '₀' && c <= '₉');
    }

    public static boolean isSmallLetter(char c) {
        return switch (c) {
            case 'ᴀ', 'ʙ', 'ᴄ', 'ᴅ', 'ᴇ', 'ꜰ', 'ɢ', 'ʜ', 'ɪ', 'ᴊ', 'ᴋ', 'ʟ', 'ᴍ', 'ɴ', 'ᴏ', 'ᴘ', 'ꞯ', 'ʀ', 'ꜱ', 'ᴛ',
                 'ᴜ', 'ᴠ', 'ᴡ', 'x', 'ʏ', 'ᴢ', '.', ',', ':', ';' -> true;
            default -> false;
        };
    }

    public static String textToJson(Text text) {
        return gson.toJson(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow());
    }
}
