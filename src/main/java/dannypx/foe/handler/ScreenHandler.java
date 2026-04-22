package dannypx.foe.handler;

import dannypx.foe.config.Configs;
import dannypx.foe.helper.KeyBindHelper;
import dannypx.foe.helper.ComponentHelper;
import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.type.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonColors;

public abstract class ScreenHandler implements ScreenConstants {
    protected final Minecraft minecraft = Minecraft.getInstance();

    public void init(Screen screen) {}
    public void render(Screen screen, GuiGraphics drawContext, int mouseX, int mouseY, float tickDelta) {}
    protected abstract Map<String, Pair<MutableComponent, MutableComponent>> _getFields();

    public void renderButtonHelp(GuiGraphics drawContext, boolean showInspect, boolean showScroll) {
        Font textRenderer = minecraft.font;
        List<Component> listHelp = new ArrayList<>();

        if(showInspect) listHelp.add(ComponentHelper.concat(
                Component.literal("Show more info "),
                Component.literal(KeyBindHelper.getKeyUnicode(Configs.keyBindConfig.inspectKeybind))
        ));
        if(showScroll) listHelp.add(Component.literal("Scroll through pages \uDB80\uDC67"));

        for (int i = 0; i < listHelp.size(); i++) {
            Component text = listHelp.get(i);

            drawContext.drawString(
                    textRenderer, text,
                    minecraft.getWindow().getGuiScaledWidth() - PADDING - textRenderer.width(text),
                    minecraft.getWindow().getGuiScaledHeight() - PADDING - textRenderer.lineHeight
                            - (textRenderer.lineHeight + 12) * i,
                    CommonColors.WHITE,
                    true
            );
        }
    }
}
