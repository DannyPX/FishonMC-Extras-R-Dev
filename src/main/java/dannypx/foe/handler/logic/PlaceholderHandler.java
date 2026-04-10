package dannypx.foe.handler.logic;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.fetch.*;
import dannypx.foe.handler.store.*;
import dannypx.foe.helper.FunctionParser;
import dannypx.foe.helper.MathHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.item.NbtObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.search.Operator;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.StringValue;
import dannypx.foe.type.custom_text.TextValue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

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
            // Boolean
            Map.entry("condition", PlaceholderHandler::parseConditionFromString),
            Map.entry("is_blank", param -> parseIsBlankFromString(param, true)),
            Map.entry("is_not_blank", param -> parseIsBlankFromString(param, false)),
            Map.entry("or", PlaceholderHandler::parseOrFromString),
            Map.entry("and", PlaceholderHandler::parseAndFromString),
            Map.entry("not", PlaceholderHandler::parseNotFromString),
            Map.entry("xot", PlaceholderHandler::parseXorFromString),
            // String
            Map.entry("substring_front", param -> parseSubStringFromString(param, true)),
            Map.entry("substring_back", param -> parseSubStringFromString(param, false)),
            Map.entry("index_of", PlaceholderHandler::parseIndexOfFromString),
            // Math
            Map.entry("expression", PlaceholderHandler::parseExpressionFromString),
            Map.entry("max", PlaceholderHandler::parseMaxFromString),
            Map.entry("min", PlaceholderHandler::parseMinFromString),
            Map.entry("abs", PlaceholderHandler::parseAbsoluteFromString),
            Map.entry("ceil", PlaceholderHandler::parseCeilingFromString),
            Map.entry("floor", PlaceholderHandler::parseFloorFromString),
            Map.entry("round", PlaceholderHandler::parseRoundingFromString)
    );
    //endregion

    //region Methods
    // Boolean = hasFullData
    public static Pair<Boolean, MutableText> parsePlaceholderFromString(String input) {
        boolean hasFullData = true;

        MutableText result = Text.empty();
        int lastEnd = 0;
        Style activeStyle = Style.EMPTY;

        while (hasFullData) {
            int startPlaceholderPos = -1;
            int endPlaceHolderPos = -1;

            for (int i = lastEnd; i < input.length(); i++) {
                if (input.charAt(i) == '%' && (i == 0 || input.charAt(i - 1) != '\\')) {
                    if (startPlaceholderPos == -1) {
                        startPlaceholderPos = i;
                    } else {
                        endPlaceHolderPos = i;
                        break;
                    }
                }
            }

            if (startPlaceholderPos == -1 || endPlaceHolderPos == -1) {
                break;
            }

            if (startPlaceholderPos > lastEnd) {
                String before = input.substring(lastEnd, startPlaceholderPos);
                Pair<MutableText, Style> parsed =
                        TextHelper.parseLegacyWithStyle(before, activeStyle);

                result.append(parsed.value1());
                activeStyle = parsed.value2();
            }

            String full = input.substring(startPlaceholderPos + 1, endPlaceHolderPos);

            String[] parts = full.split("\\.");
            String identifier = parts[0];
            String[] parameters = Arrays.copyOfRange(parts, 1, parts.length);

            Pair<Boolean, CustomTextValue> functionResult = null;

            if(placeholders.containsKey(identifier)) {
                Function<String[], Pair<Boolean, CustomTextValue>> function = placeholders.get(identifier);

                if (function != null) {
                    functionResult = function.apply(parameters);
                } else {
                    result.append(Text.literal(input.substring(startPlaceholderPos, endPlaceHolderPos + 1)).setStyle(activeStyle));
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
                result.append(Text.literal(input.substring(startPlaceholderPos, endPlaceHolderPos + 1)).setStyle(activeStyle));
                hasFullData = false;
            }

            lastEnd = endPlaceHolderPos + 1;
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

    public static Pair<Boolean, CustomTextValue> parseIsBlankFromString(FunctionParser.FunctionPlaceholder placeholder, boolean isBlank) {
        if(placeholder.operator == null && placeholder.left != null && placeholder.right == null) {
            String leftField;

            if(placeholder.leftBracketed) {
                Pair<Boolean, MutableText> parsedString = parsePlaceholderFromString("%" + placeholder.left + "%");
                if(parsedString.value1()) {
                    leftField = parsePlaceholderFromString("%" + placeholder.left + "%").value2().getString();
                } else {
                    leftField = "";
                }
            } else {
                leftField = placeholder.left;
            }

            return Pair.of(leftField.isBlank() == isBlank, new StringValue(""));
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseSubStringFromString(FunctionParser.FunctionPlaceholder placeholder, boolean isFront) {
        if(placeholder.operator == Operator.SEPARATOR && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            int rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            try {
                if(leftField.value1()) {
                    if(placeholder.rightBracketed) {
                        rightField = (int) Float.parseFloat(parsePlaceholderFromString("%" + placeholder.right + "%").value2().getString());
                    } else {
                        rightField = (int) Float.parseFloat(placeholder.right);
                    }

                    if(isFront) {
                        return Pair.of(true, new TextValue(TextHelper.substring(leftField.value2(), 0, rightField)));
                    } else {
                        return Pair.of(true, new TextValue(TextHelper.substring(leftField.value2(), rightField, leftField.value2().getString().length())));
                    }
                } else {
                    return noResult();
                }
            } catch (NumberFormatException e) {
                return noResult();
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseIndexOfFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == Operator.SEPARATOR && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            Pair<Boolean, MutableText> rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            if(placeholder.rightBracketed) {
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%");
            } else {
                rightField = Pair.of(true, Text.literal(placeholder.right));
            }

            if(leftField.value1() && rightField.value1()) {
                int index = leftField.value2().getString().indexOf(rightField.value2().getString());

                if(index == -1) {
                    return noResult();
                } else {
                    return Pair.of(true, new StringValue(String.valueOf(index)));
                }
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseOrFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == Operator.SEPARATOR && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            Pair<Boolean, MutableText> rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(Boolean.parseBoolean(placeholder.left), Text.empty());
            }

            if(placeholder.rightBracketed) {
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%");
            } else {
                rightField = Pair.of(Boolean.parseBoolean(placeholder.right), Text.empty());
            }

            if(leftField.value1() || rightField.value1()) {
                return Pair.of(true, new StringValue(""));
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseXorFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == Operator.SEPARATOR && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            Pair<Boolean, MutableText> rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(Boolean.parseBoolean(placeholder.left), Text.empty());
            }

            if(placeholder.rightBracketed) {
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%");
            } else {
                rightField = Pair.of(Boolean.parseBoolean(placeholder.right), Text.empty());
            }

            if((leftField.value1() || rightField.value1()) && (leftField.value1() != rightField.value1())) {
                return Pair.of(true, new StringValue(""));
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseAndFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == Operator.SEPARATOR && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            Pair<Boolean, MutableText> rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(Boolean.parseBoolean(placeholder.left), Text.empty());
            }

            if(placeholder.rightBracketed) {
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%");
            } else {
                rightField = Pair.of(Boolean.parseBoolean(placeholder.right), Text.empty());
            }

            if(leftField.value1() && rightField.value1()) {
                return Pair.of(true, new StringValue(""));
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseNotFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == null && placeholder.left != null && placeholder.right == null) {
            Pair<Boolean, MutableText> leftField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(Boolean.parseBoolean(placeholder.left), Text.empty());
            }

            if(leftField.value1()) {
                return Pair.of(false, new StringValue(""));
            } else {
                return Pair.of(true, new StringValue(""));
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseExpressionFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator != null && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            Pair<Boolean, MutableText> rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            if(placeholder.rightBracketed) {
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%");
            } else {
                rightField = Pair.of(true, Text.literal(placeholder.right));
            }

            try {
                float leftNumber = Float.parseFloat(leftField.value2().getString());
                float rightNumber = Float.parseFloat(rightField.value2().getString());

                float result = MathHelper.checkExpression(placeholder.operator, leftNumber, rightNumber);

                if(result != Float.MIN_VALUE) {
                    return Pair.of(true, new StringValue(String.format(Locale.US, "%f", result)));
                }
            } catch (NumberFormatException e) {
                return noResult();
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseMaxFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == Operator.SEPARATOR && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            Pair<Boolean, MutableText> rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            if(placeholder.rightBracketed) {
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%");
            } else {
                rightField = Pair.of(true, Text.literal(placeholder.right));
            }

            try {
                float leftNumber = Float.parseFloat(leftField.value2().getString());
                float rightNumber = Float.parseFloat(rightField.value2().getString());

                float result = Math.max(leftNumber, rightNumber);

                return Pair.of(true, new StringValue(String.format(Locale.US, "%f", result)));
            } catch (NumberFormatException e) {
                return noResult();
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseMinFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == Operator.SEPARATOR && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            Pair<Boolean, MutableText> rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            if(placeholder.rightBracketed) {
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%");
            } else {
                rightField = Pair.of(true, Text.literal(placeholder.right));
            }

            try {
                float leftNumber = Float.parseFloat(leftField.value2().getString());
                float rightNumber = Float.parseFloat(rightField.value2().getString());

                float result = Math.min(leftNumber, rightNumber);

                return Pair.of(true, new StringValue(String.format(Locale.US, "%f", result)));
            } catch (NumberFormatException e) {
                return noResult();
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseAbsoluteFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == null && placeholder.left != null && placeholder.right == null) {
            Pair<Boolean, MutableText> leftField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            try {
                float leftNumber = Float.parseFloat(leftField.value2().getString());

                float result = Math.abs(leftNumber);

                return Pair.of(true, new StringValue(String.format(Locale.US, "%f", result)));
            } catch (NumberFormatException e) {
                return noResult();
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseCeilingFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == null && placeholder.left != null && placeholder.right == null) {
            Pair<Boolean, MutableText> leftField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            try {
                float leftNumber = Float.parseFloat(leftField.value2().getString());

                float result = (float) Math.ceil(leftNumber);

                return Pair.of(true, new StringValue(String.format(Locale.US, "%f", result)));
            } catch (NumberFormatException e) {
                return noResult();
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseRoundingFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == Operator.SEPARATOR && placeholder.left != null && placeholder.right != null) {
            Pair<Boolean, MutableText> leftField;
            Pair<Boolean, MutableText> rightField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            if(placeholder.rightBracketed) {
                rightField = parsePlaceholderFromString("%" + placeholder.right + "%");
            } else {
                rightField = Pair.of(true, Text.literal(placeholder.right));
            }

            try {
                float leftNumber = Float.parseFloat(leftField.value2().getString());
                int rightNumber = Integer.parseInt(rightField.value2().getString());

                if (rightNumber < 0) return noResult();

                BigDecimal bd = new BigDecimal(Double.toString(leftNumber));
                bd = bd.setScale(rightNumber, RoundingMode.HALF_UP);
                float result = (float) bd.doubleValue();

                return Pair.of(true, new StringValue(TextHelper.floatToString(result, rightNumber)));
            } catch (NumberFormatException e) {
                return noResult();
            }
        }
        return noResult();
    }

    public static Pair<Boolean, CustomTextValue> parseFloorFromString(FunctionParser.FunctionPlaceholder placeholder) {
        if(placeholder.operator == null && placeholder.left != null && placeholder.right == null) {
            Pair<Boolean, MutableText> leftField;

            if(placeholder.leftBracketed) {
                leftField = parsePlaceholderFromString("%" + placeholder.left + "%");
            } else {
                leftField = Pair.of(true, Text.literal(placeholder.left));
            }

            try {
                float leftNumber = Float.parseFloat(leftField.value2().getString());

                float result = (float) Math.floor(leftNumber);

                return Pair.of(true, new StringValue(String.format(Locale.US, "%f", result)));
            } catch (NumberFormatException e) {
                return noResult();
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

    public static Pair<Boolean, CustomTextValue> getNbtTextValue(ItemStack itemStack, String field) {
        Pair<Boolean, NbtObject> item = ValidateItem.isServerItem(itemStack);
        return getNbtTextValue(item.value2(), field);
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
