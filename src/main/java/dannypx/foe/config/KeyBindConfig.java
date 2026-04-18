package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.screen.context.ContextInput;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedKeybind;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@Version(version = 2)
@Translatable.Name("Controls")
public class KeyBindConfig extends Config {
    public KeyBindConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "keybinding_config"));
    }

    @Name("Open FOER Menu")
    public ValidatedKeybind openMainKeybind = new ValidatedKeybind(GLFW.GLFW_KEY_O, ContextInput.KEYBOARD, false, false, false);

    @Name("Inspect Key")
    public ValidatedKeybind inspectKeybind = new ValidatedKeybind(GLFW.GLFW_KEY_LEFT_SHIFT, ContextInput.KEYBOARD, false, false, false);

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON;
    }
}
