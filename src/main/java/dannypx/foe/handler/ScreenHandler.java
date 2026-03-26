package dannypx.foe.handler;

import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;

import java.util.Map;

public abstract class ScreenHandler {
    protected final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public void init(Screen screen) {}
    public void render(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {}
    protected abstract Map<String, Pair<MutableText, MutableText>> _getFields();
}
