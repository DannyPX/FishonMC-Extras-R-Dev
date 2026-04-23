package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.NotifierHandler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.handler.store.ProfileDataHandler;
import dannypx.foe.helper.ComponentHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import dannypx.foe.type.placeholder.ComponentValue;
import java.util.*;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardHandler extends Handler {
    private static ScoreboardHandler INSTANCE = new ScoreboardHandler();

    public static ScoreboardHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ScoreboardHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private List<Component> prevResult = new ArrayList<>();

    private MutableComponent date = Component.empty();
    private MutableComponent version = Component.empty();
    private MutableComponent level = Component.empty();
    private MutableComponent wallet = Component.empty();
    private MutableComponent credits = Component.empty();

    private MutableComponent catches = Component.empty();
    private MutableComponent locationMin = Component.empty();
    private MutableComponent locationMax = Component.empty();
    private MutableComponent catchRate = Component.empty();

    private MutableComponent crew = Component.empty();
    private MutableComponent crewNearby = Component.empty();

    private boolean noScoreboard = true;
    private boolean hasSentNotification = false;

    public MutableComponent getLevel() {
        return level;
    }

    public MutableComponent getWallet() {
        return wallet;
    }

    public MutableComponent getCredits() {
        return credits;
    }

    public MutableComponent getCatches() {
        return catches;
    }

    public MutableComponent getLocationMin() {
        return locationMin;
    }

    public MutableComponent getLocationMax() {
        return locationMax;
    }

    public MutableComponent getCatchRate() {
        return catchRate;
    }

    public MutableComponent getCrew() {
        return crew;
    }

    public MutableComponent isCrewNearby() {
        return crewNearby;
    }

    public boolean isNoScoreboard() {
        return noScoreboard;
    }

    public MutableComponent getVersion() {
        return version;
    }

    public MutableComponent getDate() {
        return date;
    }

    public Pair<Boolean, PlaceholderValue> getScoreboard(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(level|wallet|credits|catches|location_min|location_max|catch_rate|crew|crew_nearby|version|date)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "level" -> PlaceholderHandler.getPlaceholderValue(new ComponentValue(getLevel()));
                    case "wallet" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getWallet().getString()));
                    case "credits" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getCredits().getString()));
                    case "catches" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getCatches().getString()));
                    case "location_min" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getLocationMin().getString()));
                    case "location_max" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getLocationMax().getString()));
                    case "catch_rate" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getCatchRate().getString()));
                    case "crew" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getCrew().getString()));
                    case "crew_nearby" -> PlaceholderHandler.getPlaceholderValue(new ComponentValue(isCrewNearby()));
                    case "version" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getVersion().getString()));
                    case "date" -> PlaceholderHandler.getPlaceholderValue(new StringValue(getDate().getString()));
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
        this.reset();
    }

    private void reset() {
        prevResult.clear();

        date = Component.empty();
        version = Component.empty();
        level = Component.empty();
        wallet = Component.empty();
        credits = Component.empty();

        catches = Component.empty();
        locationMin = Component.empty();
        locationMax = Component.empty();
        catchRate = Component.empty();

        crew = Component.empty();
        crewNearby = Component.empty();

        noScoreboard = true;
    }

    private void fetchFromScoreboard() {
        if(minecraft.player != null) {
            Objective objective = this.getObjective();
            if(objective == null) return;

            Pair<Boolean, List<Component>> extractedComponent = this.extractLines(objective);
            noScoreboard = extractedComponent.value2().isEmpty();

            if(!noScoreboard && extractedComponent.value1()) {
                this.extractData(extractedComponent.value2());
            }
        }
    }

    private void extractData(List<Component> components) {
        components.forEach(component -> {
            date = checkComponent(component, "/") && !checkComponent(component, "┠ ʟᴏᴄᴀᴛɪᴏɴ")
                    ? component.getSiblings().get(0).copy() : date;
            version = checkComponent(component, "/") && !checkComponent(component, "┠ ʟᴏᴄᴀᴛɪᴏɴ")
                    ? component.getSiblings().get(1).copy() : version;
            level = checkComponent(component, "┏ ʟᴇᴠᴇʟ") && component.getSiblings().size() > 2
                    ? component.getSiblings().get(3).copy() : level;
            wallet = checkComponent(component, "ᴡᴀʟʟᴇᴛ")
                    ? component.getSiblings().get(2).copy() : wallet;
            credits = checkComponent(component, "ᴄʀᴇᴅɪᴛꜱ")
                    ? component.getSiblings().get(3).copy() : credits;
            catches = checkComponent(component, "ᴄᴀᴛᴄʜᴇꜱ")
                    ? component.getSiblings().get(2).copy() : catches;
            locationMin = checkComponent(component, "┠ ʟᴏᴄᴀᴛɪᴏɴ") && !checkComponent(component, "---")
                    ? component.getSiblings().get(2).copy() : locationMin;
            locationMax = checkComponent(component, "┠ ʟᴏᴄᴀᴛɪᴏɴ") && !checkComponent(component, "---")
                    ? component.getSiblings().get(4).copy() : locationMax;
            catchRate = checkComponent(component, "ᴄᴀᴛᴄʜ ʀᴀᴛᴇ")
                    ? component.getSiblings().get(2).copy() : catchRate;
            crew = checkComponent(component, "ᴄʀᴇᴡ:")
                    ? component.getSiblings().get(3).copy() : crew;
            crewNearby = checkComponent(component, "ᴄʀᴇᴡ ɴᴇᴀʀʙʏ")
                    ? component.getSiblings().get(2).copy() : crewNearby;
        });
    }

    private boolean checkComponent(Component component, String valueToMatch) {
        return component.getString().contains(valueToMatch);
    }

    private Pair<Boolean, List<Component>> extractLines(Objective objective) {
        Collection<PlayerTeam> team = objective.getScoreboard().getPlayerTeams();
        List<Component> componentList = team.stream()
                .map(PlayerTeam::getPlayerPrefix)
                .filter(prefix -> !prefix.getString().isBlank())
                .toList();
        if(!Objects.equals(prevResult, componentList)) {
            prevResult = new ArrayList<>(componentList);
            return Pair.of(prevResult);
        }
        return Pair.ofFalse(prevResult);
    }

    private Objective getObjective() {
        if(minecraft.player != null
                && minecraft.player.getTeam() != null
        ) {
            Scoreboard scoreboard = minecraft.player.getTeam().getScoreboard();
            return scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        }
        return null;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.ofEntries(
                Map.entry("version", Pair.of(getVersion(), Component.empty())),
                Map.entry("date", Pair.of(getDate(), Component.empty())),
                Map.entry("level", Pair.of(getLevel(), Component.empty())),
                Map.entry("wallet", Pair.of(getWallet(), Component.empty())),
                Map.entry("credits", Pair.of(getCredits(), Component.empty())),
                Map.entry("catches", Pair.of(getCatches(), Component.empty())),
                Map.entry("locationMin", Pair.of(getLocationMin(), Component.empty())),
                Map.entry("locationMax", Pair.of(getLocationMax(), Component.empty())),
                Map.entry("catchRate", Pair.of(getCatchRate(), Component.empty())),
                Map.entry("crew", Pair.of(getCrew(), Component.empty())),
                Map.entry("crewNearby", Pair.of(isCrewNearby(), Component.empty())),
                Map.entry("noScoreboard", Pair.of(ComponentHelper.literal(isNoScoreboard()), Component.empty()))
        );
    }
    //endregion
}
