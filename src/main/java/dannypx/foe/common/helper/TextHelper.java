package dannypx.foe.common.helper;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.type.type_adapter.ItemStackAdapter;
import dannypx.foe.common.type.type_adapter.TextAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TextHelper {
    private static final GsonBuilder gson = new GsonBuilder();

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
        return Text.literal(gson
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeAdapter(Text.class, new TextAdapter())
                .setPrettyPrinting()
                .create().toJson(dataModel));
    }

    public static MutableText literal(int i) {
        return Text.literal(Integer.toString(i));
    }

    public static MutableText literal(char c) {
        return Text.literal(String.valueOf(c));
    }

    public static MutableText literal(float f) {
        return Text.literal(Float.toString(f));
    }

    public static MutableText literal(Text text) {
        return concat(text);
    }

    @SuppressWarnings("unchecked")
    public static <T> MutableText literal(List<T> list) {
        if(!list.isEmpty()) {
            Object first = list.getFirst();
            if(first instanceof ItemStack) return Text.empty().append(ItemStackHelper.itemStackListToJson((List<ItemStack>) list));
            return Text.literal(
                    gson.setPrettyPrinting()
                            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                            .registerTypeAdapter(Text.class, new TextAdapter())
                            .create()
                            .toJson(list)
            );
        }
        return Text.empty();
    }

    public static MutableText literal(String s) {
        return Text.empty().append(Text.literal(s));
    }

    public static MutableText literal(ItemStack i) {
        return Text.empty().append(ItemStackHelper.itemStackToJson(i));
    }

    public static MutableText literal(NbtObject currentHeldItem) {
        return TextHelper.concat(
                Text.literal("name: "), currentHeldItem.getName(), Text.literal("\n"),
                Text.literal("rarity: "), Text.literal(currentHeldItem.getRarity()), Text.literal("\n"),
                Text.literal("type: "), Text.literal(currentHeldItem.getType())
        );
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

    public static char smallChar(char c) {
        if(isNumber(c)) {
            return smallNumber(c);
        } else if(isLetter(c)) {
            return smallLetter(c);
        } else {
            return c;
        }
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

    public static boolean isCustomFont(char c) {
        return (c >= '\uF000' && c <= '\uF999');
    }

    public static String shortenNumber(float d, int decimals) {
        if(d >= 1000 && d < 1000000) {
            String s = String.format("%." + decimals + "f", d / 1000);
            return (s.contains(".") ? s.replaceAll("0*$","").replaceAll("\\.$","") : s) + "K";
        } else if (d >= 1000000 && d < 1000000000 ){
            String s = String.format("%." + decimals + "f", d / 1000000);
            return (s.contains(".") ? s.replaceAll("0*$","").replaceAll("\\.$","") : s) + "M";
        } else if (d >= 1000000000) {
            String s = String.format("%." + decimals + "f", d / 1000000000);
            return (s.contains(".") ? s.replaceAll("0*$","").replaceAll("\\.$","") : s) + "B";
        } else {
            String s = String.format("%.0f", d);
            return s.contains(".") ? s.replaceAll("0*$","").replaceAll("\\.$","") : s;
        }
    }

    public static int toIntFromString(String value) {
        value = value.trim();
        if(value.contains("K")) {
            return (int) (Float.parseFloat(value.substring(0, value.indexOf("K"))) * 1000f);
        } else if(value.contains("M")) {
            return (int) (Float.parseFloat(value.substring(0, value.indexOf("M"))) * 1000000f);
        } else {
            return Integer.parseInt(value);
        }
    }

    public static String shortenNumber(int i, int decimals) {
        return shortenNumber((float) i, decimals);
    }

    public static String floatToString(float f, int decimals) {
        return String.format("%." + decimals + "f", f);
    }

    public static float lbToKg(float f) {
        return f * 0.4535924f;
    }

    public static float inchToCm(float f) {
        return f * 2.54f;
    }

    public static String capitalize(String s) {
        return StringUtils.capitalize(s);
    }

    public static String splitTitleCase(String s) {
        return String.join(" ", s.split("(?<!^)(?=[A-Z])"));
    }

    public static String convertField(String s) {
        return splitTitleCase(capitalize(s));
    }

    public static String textToJson(Text text) {
        return gson.create().toJson(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow());
    }

    public static String textToJsonPretty(Text text) {
        return gson.setPrettyPrinting().create().toJson(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow());
    }

    public static Text jsonToText(String json) {
        return TextCodecs.CODEC
                .decode(JsonOps.INSTANCE, gson.create().fromJson(json, JsonElement.class))
                .mapOrElse((Pair::getFirst), (pairError -> Text.empty()));

    }
}
