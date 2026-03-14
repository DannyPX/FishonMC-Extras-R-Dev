package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.logic.NotifierHandler;
import dannypx.foe.common.handler.logic.PlaceholderHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.custom_text.CustomTextValue;
import dannypx.foe.common.type.custom_text.StringValue;
import dannypx.foe.common.type.custom_text.TextValue;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Pattern;

public class ScoreboardHandler extends Handler {
    private static ScoreboardHandler INSTANCE = new ScoreboardHandler();

    public static ScoreboardHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ScoreboardHandler();
        }
        return INSTANCE;
    }

    //region Fields
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
    private boolean hasSentNotification = false;

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

    public Pair<Boolean, CustomTextValue> getScoreboard(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(level|wallet|credits|catches|location_min|location_max|catch_rate|crew|crew_nearby|version|date)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "level" -> PlaceholderHandler.getTextValue(new TextValue(getLevel()));
                    case "wallet" -> PlaceholderHandler.getTextValue(new StringValue(getWallet().getString()));
                    case "credits" -> PlaceholderHandler.getTextValue(new StringValue(getCredits().getString()));
                    case "catches" -> PlaceholderHandler.getTextValue(new StringValue(getCatches().getString()));
                    case "location_min" -> PlaceholderHandler.getTextValue(new StringValue(getLocationMin().getString()));
                    case "location_max" -> PlaceholderHandler.getTextValue(new StringValue(getLocationMax().getString()));
                    case "catch_rate" -> PlaceholderHandler.getTextValue(new StringValue(getCatchRate().getString()));
                    case "crew" -> PlaceholderHandler.getTextValue(new StringValue(getCrew().getString()));
                    case "crew_nearby" -> PlaceholderHandler.getTextValue(new TextValue(isCrewNearby()));
                    case "version" -> PlaceholderHandler.getTextValue(new StringValue(getVersion().getString()));
                    case "date" -> PlaceholderHandler.getTextValue(new StringValue(getDate().getString()));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        this.fetchFromScoreboard();



        if(!this.hasSentNotification
                && !ProfileDataHandler.instance().getProfileData().hasImportedCrew
                && !this.getCrew().getString().isBlank()
        ) {
            this.hasSentNotification = true;
            NotifierHandler.instance().notifyImportCrew();
        }
    }

    public void init() {
        prevResult.clear();
        noScoreboard = true;
    }

    private void fetchFromScoreboard() {
        if(minecraftClient.player != null) {
            ScoreboardObjective objective = this.getObjective();
            if(objective == null) return;

            Pair<Boolean, List<Text>> extractedText = this.extractLines(objective);
            noScoreboard = extractedText.value2().isEmpty();

            if(!noScoreboard && extractedText.value1()) {
                this.extractData(extractedText.value2());
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

    private Pair<Boolean, List<Text>> extractLines(ScoreboardObjective objective) {
        Collection<Team> team = objective.getScoreboard().getTeams();
        List<Text> textList = team.stream()
                .map(Team::getPrefix)
                .filter(prefix -> !prefix.getString().isBlank())
                .toList();
        if(!Objects.equals(prevResult, textList)) {
            prevResult = textList;
            return Pair.of(prevResult);
        }
        return Pair.ofFalse(prevResult);
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
                Map.entry("crewNearby", Pair.of(isCrewNearby(), Text.empty())),
                Map.entry("noScoreboard", Pair.of(TextHelper.literal(isNoScoreboard()), Text.empty()))
        );
    }
    //endregion
}
