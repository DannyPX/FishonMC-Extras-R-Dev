package dannypx.foe.screens.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.screens.element.*;
import dannypx.foe.common.type.Pair;
import dannypx.foe.screens.element.hud.*;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

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

    List<Pair<String, Element>> elements = new ArrayList<>();
    //endregion

    //region Methods
    public void init(LayeredDrawerWrapper layeredDrawerWrapper) {
        addElements(layeredDrawerWrapper);
    }

    private void addElements(LayeredDrawerWrapper layeredDrawerWrapper) {
        elements.clear();
        elements.add(Pair.of("profile_hud", new ProfileElement(minecraftClient)));
        elements.add(Pair.of("location_hud", new LocationElement(minecraftClient)));
        elements.add(Pair.of("hotbar_hud", new HotbarElement(minecraftClient)));
        elements.add(Pair.of("pet_hud", new PetElement(minecraftClient)));
        elements.add(Pair.of("notifier_hud", new NotifierElement(minecraftClient)));
        elements.add(Pair.of("debug_field_hud", new _DebugField(minecraftClient)));

        elements.forEach(element -> layeredDrawerWrapper.attachLayerAfter(IdentifiedLayer.EXPERIENCE_LEVEL,
                Identifier.of(FishOnMCExtras.MOD_ID, element.v1()), element.v2()::render));
    }
    //endregion
}
