package dannypx.foe.handler;

import dannypx.foe.config.Configs;
import dannypx.foe.helper.KeyBindHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ScreenHandler implements ScreenConstants {
    protected final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public void init(Screen screen) {}
    public void render(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {}
    protected abstract Map<String, Pair<MutableText, MutableText>> _getFields();

    public void renderButtonHelp(DrawContext drawContext, boolean showInspect, boolean showScroll) {
        TextRenderer textRenderer = minecraftClient.textRenderer;
        List<Text> listHelp = new ArrayList<>();

        if(showInspect) listHelp.add(TextHelper.concat(
                Text.literal("Show more info "),
                Text.literal(KeyBindHelper.getKeyUnicode(Configs.keyBindConfig.inspectKeybind))
        ));
        if(showScroll) listHelp.add(Text.literal("Scroll through pages \uDB80\uDC67"));

        for (int i = 0; i < listHelp.size(); i++) {
            Text text = listHelp.get(i);

            drawContext.drawText(
                    textRenderer, text,
                    minecraftClient.getWindow().getScaledWidth() - PADDING - textRenderer.getWidth(text),
                    minecraftClient.getWindow().getScaledHeight() - PADDING - textRenderer.fontHeight
                            - (textRenderer.fontHeight + 12) * i,
                    Colors.WHITE,
                    true
            );
        }
    }
}
