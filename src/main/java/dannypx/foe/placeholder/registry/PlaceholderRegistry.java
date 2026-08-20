package dannypx.foe.placeholder.registry;

import dannypx.foe.handler.fetch.BossEventHandler;
import dannypx.foe.helper.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.HashMap;
import java.util.Map;

import static dannypx.foe.placeholder.registry.PlaceholderTreeNode.node;

public class PlaceholderRegistry {
    private static final Map<String, PlaceholderTreeNode> ROOTS = new HashMap<>();

    public static void init() {
        register(
                node("test")
                        .branch(node("segment_first").valueComponent(() -> Component.literal("First").withStyle(ChatFormatting.GREEN)))
                        .branch(node("segment_second").valueString(() -> ""))
                        .branch(node("segment_third").valueComponent(Component::empty).allowEmpty())
        );
    }

    public static void register(PlaceholderTreeNode root) {
        ROOTS.put(root.key(), root);
    }

    public static PlaceholderTreeNode getRoot(String key) {
        return ROOTS.get(key);
    }
}
