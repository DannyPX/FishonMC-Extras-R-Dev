package dannypx.foe.common.handler.logic;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.type.AdvancedKeyBinding;
import dannypx.foe.common.type.Pair;
import dannypx.foe.screens.MainScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class KeyBindHandler extends Handler {
    private static KeyBindHandler INSTANCE = new KeyBindHandler();

    public static KeyBindHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new KeyBindHandler();
        }
        return INSTANCE;
    }

    //region Fields
    public final AdvancedKeyBinding openMainKeybind =
            new AdvancedKeyBinding("key." + FishOnMCExtras.MOD_ID + ".openmain",
                    GLFW.GLFW_KEY_O,
                    "category." + FishOnMCExtras.MOD_ID + ".general");
    //endregion

    //region Methods
    public void init() {
        this.registerKeybinds();
    }

    public void tick() {
        this.openMainKeybind.onPressed(() -> minecraftClient.setScreen(new MainScreen(minecraftClient.currentScreen)));
    }

    private void registerKeybinds() {
        KeyBindHandler.register(
                this.openMainKeybind
        );
    }

    private static void register(KeyBinding... keybindings) {
        for(KeyBinding keyBinding : keybindings) {
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "openMainKeybind", Pair.of(Text.literal(openMainKeybind.getBoundKeyTranslationKey()), Text.empty())
        );
    }
    //endregion
}
