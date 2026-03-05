package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ChatHandler extends Handler {
    private static ChatHandler INSTANCE = new ChatHandler();

    public static ChatHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatHandler();
        }
        return INSTANCE;
    }

    //region Fields
    //endregion

    //region Methods
    public void onReceiveMessage(Text text) {
        this.checkPet(text);
    }

    private void checkPet(Text text) {
        if(text.getString().startsWith("PETS » Equipped your")) {
            ProfileDataHandler.instance().updatePet(true);
        } else if (text.getString().startsWith("PETS » Pet unequipped!")) {
            ProfileDataHandler.instance().updatePet(false);
        } else if(text.getString().startsWith("CREWS » Crew Chat has been enabled")) {
            ProfileDataHandler.instance().updateCrewChat(true);
        } else if(text.getString().startsWith("CREWS » Crew Chat has been disabled")) {
            ProfileDataHandler.instance().updateCrewChat(false);
        }
    }

    public Text onModifyMessage(Text text) {
        text = this.modifyPetMessageWithPercentage(text);
        return text;
    }

    private Text modifyPetMessageWithPercentage(Text text) {

        String json = TextHelper.textToJson(text);
        if (json.contains("ᴘᴇᴛ ʀᴀᴛɪɴɢ")) {
            String petStr = json.substring(json.indexOf(" Pet\\n"), json.indexOf("ʀɪɢʜᴛ ᴄʟɪᴄᴋ ᴛᴏ ᴏᴘᴇɴ ᴘᴇᴛ ᴍᴇɴᴜ"));
            Pattern statNumber = Pattern.compile("(?<=\\+)(.*?)(?=\")");
            Matcher statNumberMatcher = statNumber.matcher(petStr);

            if(statNumberMatcher.find()) {
                List<String> matches = statNumberMatcher.results().map(MatchResult::group).toList();

                String petClimateLuck = matches.get(matches.size() - 7);
                String petClimateScale = matches.get(matches.size() - 5);
                String petLocationLuck = matches.get(matches.size() - 3);
                String petLocationScale = matches.getLast();

                float multiplier = findMultiplier(petStr);
                float total = Stream.of(petClimateLuck, petClimateScale, petLocationLuck, petLocationScale).mapToInt(Integer::parseInt).sum();

                StringBuilder builder = new StringBuilder(petStr);
                String petStrNew = petStr;

                petStrNew = builder.insert(StringUtils.ordinalIndexOf(petStrNew, "\\n", 9), " (" + TextHelper.floatToString((Float.parseFloat(petClimateLuck) * 4 / multiplier), 0) + "%)").toString();
                petStrNew = builder.insert(StringUtils.ordinalIndexOf(petStrNew, "\\n", 10), " (" + TextHelper.floatToString((Float.parseFloat(petClimateScale) * 4 / multiplier), 0) + "%)").toString();
                petStrNew = builder.insert(StringUtils.ordinalIndexOf(petStrNew, "\\n", 13), " (" + TextHelper.floatToString((Float.parseFloat(petLocationLuck) * 4 / multiplier), 0) + "%)").toString();
                petStrNew = builder.insert(StringUtils.ordinalIndexOf(petStrNew, "\\n", 14), " (" + TextHelper.floatToString((Float.parseFloat(petLocationScale) * 4 / multiplier), 0) + "%)").toString();
                petStrNew = builder.insert(StringUtils.ordinalIndexOf(petStrNew, "\\n", 16), " (" + TextHelper.floatToString((total / multiplier), 0) + "%)").toString();

                return TextHelper.jsonToText(json.replace(petStr, petStrNew));
            }
        }
        return text;
    }

    private static float findMultiplier(String petStr) {
        if (petStr.indexOf('\uf033') != -1) return 1f;
        else if (petStr.indexOf('\uf034') != -1) return 2f;
        else if (petStr.indexOf('\uf035') != -1) return 3f;
        else if (petStr.indexOf('\uf036') != -1) return 5f;
        else if (petStr.indexOf('\uf037') != -1) return 7.5f;
        return 1;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
        );
    }
    //endregion
}
