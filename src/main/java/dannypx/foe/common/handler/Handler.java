package dannypx.foe.common.handler;

import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;

import java.util.Map;

public abstract class Handler {
    protected static final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public void init() {}
    public void tick() {}
    protected abstract Map<String, Pair<MutableText, MutableText>> _getFields();
}
