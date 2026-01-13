package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.logic.LoggerHandler;
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
    protected Map<String, Pair<MutableText, Tooltip>> _getFields() {
        return Map.of(
                "level", Pair.of(level, null),
                "wallet", Pair.of(wallet, null),
                "credits", Pair.of(credits, null),
                "catches", Pair.of(catches, null),
                "locationMin", Pair.of(locationMin, null),
                "locationMax", Pair.of(locationMax, null),
                "catchRate", Pair.of(catchRate, null),
                "crew", Pair.of(crew, null),
                "crewNearby", Pair.of(crewNearby, null)
        );
    }
    //endregion
}
