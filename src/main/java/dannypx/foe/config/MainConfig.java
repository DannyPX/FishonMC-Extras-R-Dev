package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import me.fzzyhmstrs.fzzy_config.annotations.IgnoreVisibility;
import me.fzzyhmstrs.fzzy_config.annotations.RootConfig;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 0)
@RootConfig
@Translatable.Name("FishOnMCExtras Rebirth")
@IgnoreVisibility
public class MainConfig extends Config {
    public MainConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "main_config"));
    }

    @Name("Enable Mod")
    @Desc("This will turn off the mod when false")
    public ValidatedBoolean enableMod = new ValidatedBoolean(true);

    public ValidatedBoolean debugMode = new ValidatedBoolean(true);

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON5;
    }
}
