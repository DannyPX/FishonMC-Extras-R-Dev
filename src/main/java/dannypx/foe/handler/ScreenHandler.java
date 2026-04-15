package dannypx.foe.handler;

import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ScreenHandler implements ScreenConstants {
    protected final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public void init(Screen screen) {}
    public void render(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {}
    protected abstract Map<String, Pair<MutableText, MutableText>> _getFields();

    public void renderButtonHelp(DrawContext drawContext, boolean showShift, boolean showScroll) {
        TextRenderer textRenderer = minecraftClient.textRenderer;
        List<Text> listHelp = new ArrayList<>();

        if(showShift) listHelp.add(Text.literal("Show more info \uDB80\uDC00"));
        if(showScroll) listHelp.add(Text.literal("Scroll through pages \uDB80\uDC01"));

        for (int i = 0; i < listHelp.size(); i++) {
            Text text = listHelp.get(i);

            drawContext.drawText(
                    textRenderer, text,
                    minecraftClient.getWindow().getScaledWidth() - PADDING - textRenderer.getWidth(text),
                    minecraftClient.getWindow().getScaledHeight() - PADDING - textRenderer.fontHeight
                            - (textRenderer.fontHeight + 12) * i,
                    0xFFFFFF,
                    true
            );
        }
    }
}
