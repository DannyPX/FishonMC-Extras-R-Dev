package dannypx.foe.handler.logic;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.Handler;
import dannypx.foe.type.AdvancedKeyBinding;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.StringValue;
import dannypx.foe.screens.MainScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.regex.Pattern;

public class KeyBindHandler extends Handler {
    private static KeyBindHandler INSTANCE = new KeyBindHandler();

    public static KeyBindHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new KeyBindHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private boolean isPressingShift = false;

    public final AdvancedKeyBinding openMainKeybind =
            new AdvancedKeyBinding("key." + FishOnMCExtras.MOD_ID + ".openmain",
                    GLFW.GLFW_KEY_O,
                    "category." + FishOnMCExtras.MOD_ID + ".general");

    public boolean isPressingShift() {
        return isPressingShift;
    }

    public Pair<Boolean, CustomTextValue> getKeyBind(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(open_main_keybind)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "open_main_keybind" -> PlaceholderHandler.getTextValue(new StringValue(openMainKeybind.getBoundKeyTranslationKey()));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void init() {
        this.registerKeybinds();
    }

    public void tick() {
        this.openMainKeybind.onPressed(() -> minecraftClient.setScreen(new MainScreen(minecraftClient.currentScreen)));
    }

    public void checkKeyPresses(Screen screen, int key, int modifiers) {
        if(key == GLFW.GLFW_KEY_LEFT_SHIFT) isPressingShift = true;
    }

    public void afterKeyPressed(Screen screen, int key, int modifiers) {
        if(key == GLFW.GLFW_KEY_LEFT_SHIFT) isPressingShift = false;
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
