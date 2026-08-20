package dannypx.foe.placeholder.evaluator;

import dannypx.foe.placeholder.functions.PlaceholderValue;
import dannypx.foe.placeholder.parser.ast.*;
import dannypx.foe.placeholder.registry.PlaceholderTreeNode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderEvaluator {
    public PlaceholderResult eval(Group group) {
        boolean[] success = { true };
        List<String> errors = new ArrayList<>();
        MutableComponent combined = Component.empty();
        PlaceholderColorCodes.Tracker colorCodesTracker = new PlaceholderColorCodes.Tracker();

        for (Node child : group.children()) {
            if(child instanceof Literal literal) {
                combined.append(colorCodesTracker.consumeLiteral(literal.text()));
            } else {
                MutableComponent resolved;
                try {
                    resolved = this.evalNode(child, success, errors).toComponent();
                } catch (RuntimeException e) {
                    success[0] = false;
                    String msg = "Resolved error: " + e;
                    errors.add(msg);
                    resolved = Component.literal(msg).withStyle(ChatFormatting.RED);
                }
                combined.append(colorCodesTracker.applyActiveStyle(resolved));
            }
        }
        return new PlaceholderResult(combined, success[0], errors);
    }

    public String evalToPlainText(Group group) {
        return this.eval(group).text().getString();
    }

    private PlaceholderValue evalNode(Node node, boolean[] successAcc, List<String> errors) {
        return switch (node) {
            case Literal l -> PlaceholderValue.text(l.text());
            case AstError e -> {
                successAcc[0] = false;
                errors.add(e.message());
                yield PlaceholderValue.component(Component.literal(e.message()).withStyle(ChatFormatting.RED));
            }
            case PlaceholderReference p -> {
                PlaceholderTreeNode treeNode = p.resolved();
                PlaceholderValue result = treeNode.resolveValue(p.indices());
                if(!this.isSuccess(treeNode, result)) {
                    successAcc[0] = false;
                }
                yield result.isNull() ? PlaceholderValue.text("") : result;
            }
            case FunctionCall f -> {
                PlaceholderTreeNode treeNode = f.resolved();
                List<PlaceholderValue> evaluatedArgs = new ArrayList<>(f.args().size());

                for(Node argNode : f.args()) {
                    evaluatedArgs.add(this.evalNode(argNode, successAcc, errors));
                }

                PlaceholderValue result = treeNode.resolveEval(evaluatedArgs);

                if(!isSuccess(treeNode, result)) {
                    successAcc[0] = false;
                }
                yield result.isNull() ? PlaceholderValue.text("") : result;
            }
            case BinaryOp b -> {
                PlaceholderValue left = this.evalNode(b.left(), successAcc, errors);
                PlaceholderValue right = this.evalNode(b.right(), successAcc, errors);
                yield this.applyBinary(b.op(), left, right);
            }
            case UnaryOp u -> {
                PlaceholderValue operand = this.evalNode(u.operand(), successAcc, errors);
                yield this.applyUnary(u.op(), operand);
            }
            case Group g -> {
                MutableComponent combined = Component.empty();

                for(Node c : g.children()) {
                    combined.append(this.evalNode(c, successAcc, errors).toComponent());
                }
                yield PlaceholderValue.component(combined);
            }
        };
    }

    private boolean isSuccess(PlaceholderTreeNode node, PlaceholderValue result) {
        if(result.isNull()) return false;
        return !result.isEmpty() || node.allowsEmpty();
    }

    /// Binary/Unary evaluation

    private PlaceholderValue applyBinary(String op, PlaceholderValue leftValue, PlaceholderValue rightValue) {
        double left = leftValue.toDouble();
        double right = rightValue.toDouble();
        return switch (op) {
            case "<" -> PlaceholderValue.text(String.valueOf(left < right));
            case ">" -> PlaceholderValue.text(String.valueOf(left > right));
            case "<=" -> PlaceholderValue.text(String.valueOf(left <= right));
            case ">=" -> PlaceholderValue.text(String.valueOf(left >= right));
            case "==" -> PlaceholderValue.text(String.valueOf(left == right));
            case "!=" -> PlaceholderValue.text(String.valueOf(left != right));
            case "+" -> PlaceholderValue.number(left + right);
            case "-" -> PlaceholderValue.number(left - right);
            case "*" -> PlaceholderValue.number(left * right);
            case "/" -> PlaceholderValue.number(left / right);
            default -> throw new IllegalStateException(
                    "Unknown binary operator: " + op
            );
        };
    }

    private PlaceholderValue applyUnary(String op, PlaceholderValue operand) {
        return switch (op) {
            case "-" -> PlaceholderValue.number(-operand.toDouble());
            default -> throw new IllegalStateException(
                    "Unknown unary operator: " + op
            );
        };
    }
}
