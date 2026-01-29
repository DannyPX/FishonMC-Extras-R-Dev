package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

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

    private MutableText date = Text.empty();
    private MutableText version = Text.empty();
    private MutableText level = Text.empty();
    private MutableText wallet = Text.empty();
    private MutableText credits = Text.empty();

    private MutableText catches = Text.empty();
    private MutableText locationMin = Text.empty();
    private MutableText locationMax = Text.empty();
    private MutableText catchRate = Text.empty();

    private MutableText crew = Text.empty();
    private MutableText crewNearby = Text.empty();

    private boolean noScoreboard = true;

    public MutableText getLevel() {
        return level;
    }

    public MutableText getWallet() {
        return wallet;
    }

    public MutableText getCredits() {
        return credits;
    }

    public MutableText getCatches() {
        return catches;
    }

    public MutableText getLocationMin() {
        return locationMin;
    }

    public MutableText getLocationMax() {
        return locationMax;
    }

    public MutableText getCatchRate() {
        return catchRate;
    }

    public MutableText getCrew() {
        return crew;
    }

    public MutableText isCrewNearby() {
        return crewNearby;
    }

    public boolean isNoScoreboard() {
        return noScoreboard;
    }

    public MutableText getVersion() {
        return version;
    }

    public MutableText getDate() {
        return date;
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
            date = checkText(text, "/") && !checkText(text, "┠ ʟᴏᴄᴀᴛɪᴏɴ")
                    ? text.getSiblings().get(0).copy() : date;
            version = checkText(text, "/") && !checkText(text, "┠ ʟᴏᴄᴀᴛɪᴏɴ")
                    ? text.getSiblings().get(1).copy() : version;
            level = checkText(text, "┏ ʟᴇᴠᴇʟ") && text.getSiblings().size() > 2
                    ? text.getSiblings().get(3).copy() : level;
            wallet = checkText(text, "ᴡᴀʟʟᴇᴛ")
                    ? text.getSiblings().get(2).copy() : wallet;
            credits = checkText(text, "ᴄʀᴇᴅɪᴛꜱ")
                    ? text.getSiblings().get(3).copy() : credits;
            catches = checkText(text, "ᴄᴀᴛᴄʜᴇꜱ")
                    ? text.getSiblings().get(2).copy() : catches;
            locationMin = checkText(text, "┠ ʟᴏᴄᴀᴛɪᴏɴ") && !checkText(text, "---")
                    ? text.getSiblings().get(2).copy() : locationMin;
            locationMax = checkText(text, "┠ ʟᴏᴄᴀᴛɪᴏɴ") && !checkText(text, "---")
                    ? text.getSiblings().get(4).copy() : locationMax;
            catchRate = checkText(text, "ᴄᴀᴛᴄʜ ʀᴀᴛᴇ")
                    ? text.getSiblings().get(2).copy() : catchRate;
            crew = checkText(text, "ᴄʀᴇᴡ:")
                    ? text.getSiblings().get(3).copy() : crew;
            crewNearby = checkText(text, "ᴄʀᴇᴡ ɴᴇᴀʀʙʏ")
                    ? text.getSiblings().get(2).copy() : crewNearby;
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
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.ofEntries(
                Map.entry("version", Pair.of(getVersion(), Text.empty())),
                Map.entry("date", Pair.of(getDate(), Text.empty())),
                Map.entry("level", Pair.of(getLevel(), Text.empty())),
                Map.entry("wallet", Pair.of(getWallet(), Text.empty())),
                Map.entry("credits", Pair.of(getCredits(), Text.empty())),
                Map.entry("catches", Pair.of(getCatches(), Text.empty())),
                Map.entry("locationMin", Pair.of(getLocationMin(), Text.empty())),
                Map.entry("locationMax", Pair.of(getLocationMax(), Text.empty())),
                Map.entry("catchRate", Pair.of(getCatchRate(), Text.empty())),
                Map.entry("crew", Pair.of(getCrew(), Text.empty())),
                Map.entry("crewNearby", Pair.of(isCrewNearby(), Text.empty()))
        );
    }
    //endregion
}
