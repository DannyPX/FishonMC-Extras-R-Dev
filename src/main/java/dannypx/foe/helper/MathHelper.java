package dannypx.foe.helper;

import dannypx.foe.type.search.FloatValue;
import dannypx.foe.type.search.Operator;
import dannypx.foe.type.search.SearchFilter;

public class MathHelper {
    public static String percentToString(float percent, int decimals) {
        return TextHelper.floatToString(percent * 100f, decimals);
    }

    public static boolean checkOperation(SearchFilter searchFilter, FloatValue floatValue, float fetchedValue) {
        return checkOperation(searchFilter.operator, fetchedValue, floatValue.value());
    }

    public static boolean checkOperation(Operator operator, float leftValue, float rightValue) {
        return switch (operator) {
            case GREATER -> leftValue > rightValue;
            case LESS -> leftValue < rightValue;
            case EQUAL, SHORT_EQUAL -> leftValue == rightValue;
            case NOT_EQUAL -> leftValue != rightValue;
            case GREATER_EQUAL -> leftValue >= rightValue;
            case LESS_EQUAL -> leftValue <= rightValue;
            default -> false;
        };
    }

    public static float checkExpression(Operator operator, float leftValue, float rightValue) {
        return switch (operator) {
            case ADDITION -> leftValue + rightValue;
            case SUBTRACTION -> leftValue - rightValue;
            case MULTIPLICATION -> leftValue * rightValue;
            case DIVISION -> leftValue / rightValue;
            case MODULO -> leftValue % rightValue;
            case POWER -> (float) Math.pow(leftValue, rightValue);
            default -> Float.MIN_VALUE;
        };
    }
}
