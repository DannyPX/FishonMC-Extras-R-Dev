package dannypx.foe.screens.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.render_module.element.iElement;
import dannypx.foe.common.type.Pair;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HudRenderHandler {
    private static HudRenderHandler INSTANCE = new HudRenderHandler();

    public static HudRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new HudRenderHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    List<Pair<String, iElement>> elements = new ArrayList<>();
    //endregion

    //region Methods
    public void init(LayeredDrawerWrapper layeredDrawerWrapper) {
        addElements(layeredDrawerWrapper);
    }

    private void addElements(LayeredDrawerWrapper layeredDrawerWrapper) {
        elements.add(Pair.of("profile_hud", new ProfileElement(minecraftClient)));

        elements.forEach(element -> layeredDrawerWrapper.attachLayerAfter(IdentifiedLayer.EXPERIENCE_LEVEL,
                Identifier.of(FishOnMCExtras.MOD_ID, element.v1()), element.v2()::render));
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, Tooltip>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), null)
        );
    }
    //endregion
}
