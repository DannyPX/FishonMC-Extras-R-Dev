package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardHandler {
    private static ScoreboardHandler INSTANCE = new ScoreboardHandler();

    public static ScoreboardHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ScoreboardHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private List<Text> prevResult = new ArrayList<>();

    private String wallet = "";
    private String credits = "";

    private String catches = "";
    private String locationMin = "";
    private String locationMax = "";
    private String catchRate = "";

    private String crew = "";
    private boolean crewNearby = false;

    private boolean noScoreboard = true;

    public String getWallet() {
        return wallet;
    }

    public String getCredits() {
        return credits;
    }

    public String getCatches() {
        return catches;
    }

    public String getLocationMin() {
        return locationMin;
    }

    public String getLocationMax() {
        return locationMax;
    }

    public String getCatchRate() {
        return catchRate;
    }

    public String getCrew() {
        return crew;
    }

    public boolean isCrewNearby() {
        return crewNearby;
    }

    public boolean isNoScoreboard() {
        return noScoreboard;
    }
    //endregion

    //region Methods
    public void tick() {
        this.fetchFromScoreboard();
    }

    private void fetchFromScoreboard() {
        if(minecraftClient.player != null) {
            ScoreboardObjective objective = this.getObjective();
            if(objective == null) return;

            Pair<Boolean, List<Text>> extractedText = this.extractLines(objective);
            noScoreboard = extractedText.v2().isEmpty();

            if(!noScoreboard && extractedText.v1()) {
                this.extractData(extractedText.v2());
            }
        }
    }

    private void extractData(List<Text> texts) {
        texts.forEach(text -> {
            wallet = checkText(text, "ᴡᴀʟʟᴇᴛ")
                    ? getSubString(text, getIndexString(text, "$") + 1) : wallet;
            credits = checkText(text, "ᴄʀᴇᴅɪᴛꜱ")
                    ? getSubString(text, getIndexString(text, "\uF00C") + 1) : credits;
            catches = checkText(text, "ᴄᴀᴛᴄʜᴇꜱ")
                    ? getSubString(text, getIndexString(text, ":") + 2) : catches;
            locationMin = checkText(text, "┠ ʟᴏᴄᴀᴛɪᴏɴ") && !checkText(text, "---")
                    ? getSubString(text, getIndexString(text, ":") + 2,
                    getLastIndexString(text, "/")) : locationMin;
            locationMax = checkText(text, "┠ ʟᴏᴄᴀᴛɪᴏɴ") && !checkText(text, "---")
                    ? getSubString(text, getIndexString(text, "/") + 1).trim() : locationMax;
            catchRate = checkText(text, "ᴄᴀᴛᴄʜ ʀᴀᴛᴇ")
                    ? getSubString(text, getIndexString(text, ":") + 2) : catchRate;
            crew = checkText(text, "ᴄʀᴇᴡ:")
                    ? getSubString(text, getIndexString(text, "[") + 1,
                    getLastIndexString(text, "]")) : crew;
            crewNearby = checkText(text, "ᴄʀᴇᴡ ɴᴇᴀʀʙʏ") && checkText(text, "✔");
        });
    }

    private boolean checkText(Text text, String valueToMatch) {
        return text.getString().contains(valueToMatch);
    }

    private String getSubString(Text text, int beginIndex) {
        return text.getString().substring(beginIndex);
    }

    private String getSubString(Text text, int beginIndex, int lastIndex) {
        return text.getString().substring(beginIndex, lastIndex);
    }

    private int getIndexString(Text text, String valueToFind) {
        return text.getString().indexOf(valueToFind);
    }

    private int getLastIndexString(Text text, String valueToFind) {
        return text.getString().lastIndexOf(valueToFind);
    }

    private Pair<Boolean, List<Text>> extractLines(ScoreboardObjective objective) {
        Collection<Team> team = objective.getScoreboard().getTeams();
        List<Text> textList = team.stream()
                .map(Team::getPrefix)
                .filter(prefix -> !prefix.getString().isBlank())
                .toList();
        if(!Objects.equals(prevResult, textList)) {
            prevResult = textList;
            return Pair.of(true, prevResult);
        }
        return Pair.of(false, prevResult);
    }

    private ScoreboardObjective getObjective() {
        Scoreboard scoreboard = Objects.requireNonNull(minecraftClient.player).getScoreboard();
        return scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, Tooltip>> _getFields() {
        return Map.of(
                "wallet", Pair.of(Text.literal(wallet), null),
                "credits", Pair.of(Text.literal(credits), null),
                "catches", Pair.of(Text.literal(catches), null),
                "locationMin", Pair.of(Text.literal(locationMin), null),
                "locationMax", Pair.of(Text.literal(locationMax), null),
                "catchRate", Pair.of(Text.literal(catchRate), null),
                "crew", Pair.of(Text.literal(crew), null),
                "crewNearby", Pair.of(TextHelper.literal(crewNearby), null)
        );
    }
    //endregion
}
