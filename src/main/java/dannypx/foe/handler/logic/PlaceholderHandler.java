package dannypx.foe.handler.logic;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.fetch.*;
import dannypx.foe.handler.store.*;
import dannypx.foe.helper.FunctionParser;
import dannypx.foe.helper.MathHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.item.NbtObject;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.StringValue;
import dannypx.foe.type.custom_text.TextValue;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderHandler extends Handler {
    private static PlaceholderHandler INSTANCE = new PlaceholderHandler();

    public static PlaceholderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new PlaceholderHandler();
        }
        return INSTANCE;
    }

    //region Fields
    static final Pattern placeholderPattern = Pattern.compile("(?<!\\\\)%([^%]+?)(?<!\\\\)%");

    private static final Map<String, Function<String[], Pair<Boolean, CustomTextValue>>> placeholders = Map.ofEntries(
            Map.entry("boss_bar", params -> BossBarHandler.instance().getBossBar(params)),
            Map.entry("player", params -> ClientPlayerHandler.instance().getClientPlayer(params)),
            Map.entry("network", params -> NetworkHandler.instance().getNetwork(params)),
            Map.entry("scoreboard", params -> ScoreboardHandler.instance().getScoreboard(params)),
            Map.entry("tab", params -> TabHandler.instance().getTab(params)),
            Map.entry("title", params -> TitleHandler.instance().getTitle(params)),
            Map.entry("connection", params -> ConnectionHandler.instance().getConnection(params)),
            Map.entry("inventory", params -> InventoryHandler.instance().getInventory(params)),
            Map.entry("key_bind", params -> KeyBindHandler.instance().getKeyBind(params)),
            Map.entry("loading", params -> LoadingHandler.instance().getLoading(params)),
            Map.entry("ray_cast", params -> RayCastHandler.instance().getRayCast(params)),
            Map.entry("crew", params -> CrewHandler.instance().getCrew(params)),
            Map.entry("chat", params -> ChatHandler.instance().getChat(params)),
            Map.entry("timer", params -> TimerHandler.instance().getTimer(params)),
            Map.entry("catch", params -> CatchingHandler.instance().getCatch(params)),
            Map.entry("constant_data", params -> ConstantDataHandler.instance().getConstantData(params)),
            Map.entry("profile_data", params -> ProfileDataHandler.instance().getProfileData(params)),
            Map.entry("quest_data", params -> QuestDataHandler.instance().getQuestData(params)),
            Map.entry("stats_data", params -> StatsDataHandler.instance().getStatsData(params)),
            Map.entry("crew_data", params -> CrewDataHandler.instance().getCrewData(params))
    );

    private static final Map<String, Function<FunctionParser.FunctionPlaceholder, Pair<Boolean, CustomTextValue>>> functionPlaceholders = Map.ofEntries(
            Map.entry("condition", PlaceholderHandler::parseConditionFromString)
    );
    //endregion

    //region Methods
    // Boolean = hasFullData
    public static Pair<Boolean, MutableText> parsePlaceholderFromString(String input) {
        Matcher placeholderMatcher = placeholderPattern.matcher(input);
        boolean hasFullData = true;

        MutableText result = Text.empty();
        int lastEnd = 0;
        Style activeStyle = Style.EMPTY;

        while (placeholderMatcher.find() && hasFullData) {
            if (placeholderMatcher.start() > lastEnd) {
                String before = input.substring(lastEnd, placeholderMatcher.start());
                Pair<MutableText, Style> parsed = TextHelper.parseLegacyWithStyle(before, activeStyle);
                result.append(parsed.value1());
                activeStyle = parsed.value2();
            }

            String full = placeholderMatcher.group(1);
            String[] parts = full.split("\\.");
            String identifier = parts[0];
            String[] parameters = Arrays.copyOfRange(parts, 1, parts.length);

            Pair<Boolean, CustomTextValue> functionResult = null;

            if(placeholders.containsKey(identifier)) {
                Function<String[], Pair<Boolean, CustomTextValue>> function = placeholders.get(identifier);

                if (function != null) {
                    functionResult = function.apply(parameters);
                } else {
                    result.append(Text.literal(placeholderMatcher.group()).setStyle(activeStyle));
                }
            } else if (functionPlaceholders.containsKey(identifier)) {
                functionResult = parseFunctionPlaceHolderFromString("%" + full + "%");
            }

            if (functionResult != null && functionResult.value1()) {
                Pair<MutableText, Style> parsed;

                switch (functionResult.value2()) {
                    case StringValue stringValue -> parsed = TextHelper.parseLegacyWithStyle(stringValue.value(), activeStyle);
                    case TextValue textValue -> parsed = Pair.of(textValue.value().copy(), textValue.value().getStyle());
                }

                result.append(parsed.value1());
                activeStyle = parsed.value2();
            } else {
                result.append(Text.literal(placeholderMatcher.group()).setStyle(activeStyle));
                hasFullData = false;
            }

            lastEnd = placeholderMatcher.end();
        }

        if (lastEnd < input.length()) {
            String remaining = input.substring(lastEnd);
            Pair<MutableText, Style> parsed = TextHelper.parseLegacyWithStyle(remaining, activeStyle);
            result.append(parsed.value1());
        }

        return Pair.of(hasFullData, result);
    }

    private static Pair<Boolean, CustomTextValue> parseFunctionPlaceHolderFromString(String placeholder) {
        FunctionParser.FunctionPlaceholder functionPlaceholder = FunctionParser.parse(placeholder);

        Function<FunctionParser.FunctionPlaceholder, Pair<Boolean, CustomTextValue>> function = functionPlaceholders.get(functionPlaceholder.function);

        if(function != null) {
            return function.apply(functionPlaceholder);
        } else {
            return noResult();
        }
    }

    private static Pair<Boolean, CustomTextValue> parseConditionFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator != null && placeholder.left != null && placeholder.right != null) {
            String leftField;
            String rightField;

            if(placeholder.leftBracketed)
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%").value2().getString();
            else {
                leftField = placeholder.left;
            }

            if(placeholder.rightBracketed)
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%").value2().getString();
            else {
                rightField = placeholder.right;
            }

            try {
                float leftFloat = Float.parseFloat(leftField);
                float rightFloat = Float.parseFloat(rightField);

                return Pair.of(MathHelper.checkOperation(placeholder.operator, leftFloat, rightFloat), new StringValue(""));
            } catch (NumberFormatException e) {
                return switch (placeholder.operator) {
                    case SHORT_EQUAL -> Pair.of(leftField.contains(rightField), new StringValue(""));
                    case EQUAL -> Pair.of(leftField.equals(rightField), new StringValue(""));
                    case NOT_EQUAL -> Pair.of(!leftField.equals(rightField), new StringValue(""));
                    default -> noResult();
                };
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> getTextValue(CustomTextValue customTextValue) {
        return getTextValue(customTextValue, false);
    }

    public static Pair<Boolean, CustomTextValue> getTextValue(CustomTextValue customTextValue, Boolean noHide) {
        switch (customTextValue) {
            case StringValue stringValue -> {
                if(!stringValue.value().isBlank()) return Pair.of(stringValue);
                return noHide ? Pair.of(stringValue) : Pair.ofFalse(stringValue);
            }
            case TextValue textValue -> {
                if(!textValue.value().getString().isBlank()) return Pair.of(textValue);
                return noHide ? Pair.of(textValue) : Pair.ofFalse(textValue);
            }
        }
    }

    public static Pair<Boolean, CustomTextValue> noResult() {
        return Pair.ofFalse(new StringValue(""));
    }

    public static Pair<Boolean, CustomTextValue> getNbtTextValue(NbtObject object, String field) {
        if(object.contains(field)) {
            NbtElement data = object.get(field);
            return switch (data.getType()) {
                case 1 -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(object.getBoolean(field))));
                case 3 -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(object.getInt(field))));
                case 5 -> PlaceholderHandler.getTextValue(new StringValue(TextHelper.floatToString(object.getFloat(field), 2)));
                case 8 -> PlaceholderHandler.getTextValue(new StringValue(object.getString(field)));
                default -> PlaceholderHandler.noResult();
            };
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
