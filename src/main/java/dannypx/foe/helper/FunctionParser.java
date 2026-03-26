package dannypx.foe.helper;

import dannypx.foe.type.search.Operator;

public class FunctionParser {
    public static class FunctionPlaceholder {
        public String function;

        public Operator operator;

        public String left;
        public boolean leftBracketed;

        public String right;
        public boolean rightBracketed;

        public boolean isCorrect = true;

        public static FunctionPlaceholder getFalse() {
            FunctionPlaceholder result = new FunctionPlaceholder();
            result.isCorrect = false;
            return result;
        }
    }

    public static FunctionPlaceholder parse(String input) {
        input = input.trim();

        if (!input.startsWith("%") || !input.endsWith("%")) {
            return FunctionPlaceholder.getFalse();
        }

        String inner = input.substring(1, input.length() - 1);

        int dotIndex = inner.indexOf(".(");
        if (dotIndex == -1) {
            return FunctionPlaceholder.getFalse();
        }

        String function = inner.substring(0, dotIndex);
        String expr = inner.substring(dotIndex + 2, inner.length() - 1).trim();

        FunctionPlaceholder result = new FunctionPlaceholder();
        result.function = function;

        parseOperator(expr, result);

        return result;
    }

    private static void parseOperator(String expression, FunctionPlaceholder result) {
        int angleDepth = 0;
        int parenDepth = 0;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '<') {
                angleDepth++;
                continue;
            } else if (c == '>') {
                angleDepth--;
                continue;
            } else if (c == '(') {
                parenDepth++;
                continue;
            } else if (c == ')') {
                parenDepth--;
                continue;
            }

            if (angleDepth == 0 && parenDepth == 0) {
                Operator op = matchOperator(expression, i);
                if (op != null) {
                    String left = expression.substring(0, i).trim();
                    String right = expression.substring(i + op.symbol.length()).trim();

                    result.operator = op;

                    if (isBracketed(left)) {
                        result.leftBracketed = true;
                        result.left = stripBrackets(left);
                    } else {
                        result.leftBracketed = false;
                        result.left = left;
                    }

                    if (isBracketed(right)) {
                        result.rightBracketed = true;
                        result.right = stripBrackets(right);
                    } else {
                        result.rightBracketed = false;
                        result.right = right;
                    }

                    return;
                }
            }
        }

        if(result.operator == null) {
            handleSingleValue(expression, result);
        }
    }

    private static void handleSingleValue(String expr, FunctionPlaceholder result) {
        expr = expr.trim();

        if (expr.isEmpty()) {
            result.left = "";
            result.leftBracketed = false;
            result.operator = null;
            result.right = null;
            result.rightBracketed = false;
            return;
        }

        if (isBracketed(expr)) {
            result.leftBracketed = true;
            result.left = stripBrackets(expr);
        } else {
            result.leftBracketed = false;
            result.left = expr;
        }

        result.operator = null;
        result.right = null;
        result.rightBracketed = false;
    }

    private static boolean isBracketed(String s) {
        return s.startsWith("<") && s.endsWith(">");
    }

    private static String stripBrackets(String s) {
        return s.substring(1, s.length() - 1);
    }

    private static Operator matchOperator(String s, int i) {
        Operator[] ops = Operator.values();

        for (Operator op : ops) {
            if (s.startsWith(op.symbol, i)) {
                return op;
            }
        }
        return null;
    }
}
