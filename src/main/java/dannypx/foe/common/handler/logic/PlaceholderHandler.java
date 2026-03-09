package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.fetch.*;
import dannypx.foe.common.handler.store.ConstantDataHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.handler.store.QuestDataHandler;
import dannypx.foe.common.handler.store.StatsDataHandler;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.custom_text.CustomTextValue;
import dannypx.foe.common.type.custom_text.StringValue;
import dannypx.foe.common.type.custom_text.TextValue;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

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
    private static final Map<String, Function<String[], Pair<Boolean, CustomTextValue>>> placeholders = Map.ofEntries(
            Map.entry("boss_bar", params -> BossBarHandler.instance().getBossBar(params)),
            Map.entry("player", params -> ClientPlayerHandler.instance().getClientPlayer(params)),
            Map.entry("scoreboard", params -> ScoreboardHandler.instance().getScoreboard(params)),
            Map.entry("tab", params -> TabHandler.instance().getTab(params)),
            Map.entry("title", params -> TitleHandler.instance().getTitle(params)),
            Map.entry("connection", params -> ConnectionHandler.instance().getConnection(params)),
            Map.entry("inventory", params -> InventoryHandler.instance().getInventory(params)),
            Map.entry("key_bind", params -> KeyBindHandler.instance().getKeyBind(params)),
            Map.entry("loading", params -> LoadingHandler.instance().getLoading(params)),
            Map.entry("ray_cast", params -> RayCastHandler.instance().getRayCast(params)),
            Map.entry("constant_data", params -> ConstantDataHandler.instance().getConstantData(params)),
            Map.entry("profile_data", params -> ProfileDataHandler.instance().getProfileData(params)),
            Map.entry("quest_data", params -> QuestDataHandler.instance().getQuestData(params)),
            Map.entry("stats_data", params -> StatsDataHandler.instance().getStatsData(params))
    );
    //endregion

    //region Methods
    // Boolean = hasFullData
    public static Pair<Boolean, MutableText> parsePlaceholderFromString(String input) {
        Pattern placeholderPattern = Pattern.compile("(?<!\\\\)%([^%]+?)(?<!\\\\)%");
        Matcher matcher = placeholderPattern.matcher(input);
        boolean hasFullData = true;

        MutableText result = Text.empty();
        int lastEnd = 0;
        Style activeStyle = Style.EMPTY;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String before = input.substring(lastEnd, matcher.start());
                Pair<MutableText, Style> parsed = parseLegacyWithStyle(before, activeStyle);
                result.append(parsed.value1());
                activeStyle = parsed.value2();
            }

            String full = matcher.group(1);
            String[] parts = full.split("\\.");
            String identifier = parts[0];
            String[] parameters = Arrays.copyOfRange(parts, 1, parts.length);

            Function<String[], Pair<Boolean, CustomTextValue>> function = placeholders.get(identifier);

            if (function != null) {
                Pair<Boolean, CustomTextValue> functionResult = function.apply(parameters);
                if (functionResult.value1()) {
                    Pair<MutableText, Style> parsed;

                    switch (functionResult.value2()) {
                        case StringValue stringValue -> {
                            parsed = parseLegacyWithStyle(stringValue.value(), activeStyle);
                        }
                        case TextValue textValue -> {
                            parsed = Pair.of(textValue.value(), textValue.value().getStyle());
                        }
                    }

                    result.append(parsed.value1());
                    activeStyle = parsed.value2();
                } else {
                    result.append(Text.literal(matcher.group()).setStyle(activeStyle));
                    hasFullData = false;
                }
            } else {
                result.append(Text.literal(matcher.group()).setStyle(activeStyle));
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < input.length()) {
            String remaining = input.substring(lastEnd);
            Pair<MutableText, Style> parsed = parseLegacyWithStyle(remaining, activeStyle);
            result.append(parsed.value1());
        }

        return Pair.of(hasFullData, result);
    }

    private static Pair<MutableText, Style> parseLegacyWithStyle(String input, Style startingStyle) {
        MutableText text = Text.empty();
        Pattern pattern = Pattern.compile("(§#[0-9A-Fa-f]{6}|§[0-9A-FK-ORa-fk-or])");
        Matcher matcher = pattern.matcher(input);

        int lastEnd = 0;
        Style currentStyle = startingStyle;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                text.append(Text.literal(input.substring(lastEnd, matcher.start())).setStyle(currentStyle));
            }

            String code = matcher.group();
            if (code.equalsIgnoreCase("§r")) {
                currentStyle = Style.EMPTY;
            } else if (code.startsWith("§#")) {
                int rgb = Integer.parseInt(code.substring(2), 16);
                currentStyle = currentStyle.withColor(TextColor.fromRgb(rgb));
            } else {
                Formatting fmt = Formatting.byCode(code.charAt(1));
                currentStyle = currentStyle.withFormatting(fmt);
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < input.length()) {
            text.append(Text.literal(input.substring(lastEnd)).setStyle(currentStyle));
        }

        return Pair.of(text, currentStyle);
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
