package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 0)
@Translatable.Name("Miscellaneous Configuration")
@Translatable.Desc("§7Configure miscellaneous stuff")
public class MiscConfig extends Config {
    public MiscConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "misc_config"));
    }

    @Name("Use the 3D bobber texture")
    public ValidatedBoolean showNewBobber = new ValidatedBoolean(true);

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON5;
    }
}
