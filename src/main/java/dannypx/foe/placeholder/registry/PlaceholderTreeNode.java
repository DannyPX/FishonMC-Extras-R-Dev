package dannypx.foe.placeholder.registry;

import dannypx.foe.placeholder.functions.*;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlaceholderTreeNode {
    private enum WildcardType { NONE, INDEX, STRING }

    private final String key;
    private final WildcardType wildcardType;
    private final Map<String, PlaceholderTreeNode> children = new HashMap<>();

    private PlaceholderTreeNode indexChild;
    private PlaceholderTreeNode stringChild;

    private Function<List<String>, PlaceholderValue> resolver;
    private Function<List<PlaceholderValue>, PlaceholderValue> evalFunction;

    private boolean allowEmpty = false;

    private PlaceholderTreeNode(@Nullable String key, WildcardType wildcardType) {
        this.key = key;
        this.wildcardType = wildcardType;
    }

    /// Tree builder

    public static PlaceholderTreeNode node(String key) {
        return new PlaceholderTreeNode(key, WildcardType.NONE);
    }

    public static PlaceholderTreeNode nodeIndex() {
        return new PlaceholderTreeNode(null, WildcardType.INDEX);
    }

    public static PlaceholderTreeNode nodeString() {
        return new PlaceholderTreeNode(null, WildcardType.STRING);
    }

    public PlaceholderTreeNode branch(PlaceholderTreeNode child) {
        switch (child.wildcardType) {
            case NONE -> children.put(child.key, child);
            case INDEX -> this.indexChild = child;
            case STRING -> this.stringChild = child;
        }
        return this;
    }

    /// .value resolvers

    public PlaceholderTreeNode valueString(Supplier<String> supplier) {
        this.resolver = args -> PlaceholderValue.text(supplier.get());
        return this;
    }

    public PlaceholderTreeNode valueString(PlaceholderStringFunction function) {
        this.resolver = args -> PlaceholderValue.text(function.resolve(args));
        return this;
    }

    public PlaceholderTreeNode valueComponent(Supplier<MutableComponent> supplier) {
        this.resolver = args -> PlaceholderValue.component(supplier.get());
        return this;
    }

    public PlaceholderTreeNode valueComponent(PlaceholderComponentFunction function) {
        this.resolver = args -> PlaceholderValue.component(function.resolve(args));
        return this;
    }

    public PlaceholderTreeNode valueNumber(Supplier<Number> supplier) {
        this.resolver = args -> PlaceholderValue.number(supplier.get());
        return this;
    }

    public PlaceholderTreeNode valueNumber(PlaceholderNumberFunction function) {
        this.resolver = args -> PlaceholderValue.number(function.resolve(args));
        return this;
    }

    public PlaceholderTreeNode evalString(PlaceholderEvalStringFunction function) {
        this.evalFunction = args -> PlaceholderValue.text(function.resolve(args));
        return this;
    }

    public PlaceholderTreeNode evalComponent(PlaceholderEvalComponentFunction function) {
        this.evalFunction = args -> PlaceholderValue.component(function.resolve(args));
        return this;
    }

    public PlaceholderTreeNode evalNumber(PlaceholderEvalNumberFunction function) {
        this.evalFunction = args -> PlaceholderValue.number(function.resolve(args));
        return this;
    }

    public PlaceholderTreeNode evalValue(PlaceholderEvalValueFunction function) {
        this.evalFunction = function::resolve;
        return this;
    }

    ///

    public PlaceholderTreeNode allowEmpty() {
        this.allowEmpty = true;
        return this;
    }

    public boolean allowsEmpty() {
        return allowEmpty;
    }

    public String key() {
        return key;
    }

    public PlaceholderTreeNode resolveChild(String segment, List<String> captured) {
        PlaceholderTreeNode named = children.get(segment);

        if(named != null) {
            return named;
        }

        if(indexChild != null && PlaceholderTreeNode.isNumeric(segment)) {
            captured.add(segment);
            return indexChild;
        }

        if(stringChild != null) {
            captured.add(segment);
            return stringChild;
        }

        return null;
    }

    private static boolean isNumeric(String s) {
        if(s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if(!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    public boolean hasResolver() {
        return resolver != null;
    }

    public boolean hasEval() {
        return evalFunction != null;
    }

    public PlaceholderValue resolveValue(List<String> indices) {
        return resolver.apply(indices);
    }

    public PlaceholderValue resolveEval(List<PlaceholderValue> args) {
        return evalFunction.apply(args);
    }
}
