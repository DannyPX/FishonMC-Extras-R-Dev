package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.type.HorizontalAlignment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 0)
@Translatable.Name("HUD Configuration")
@Translatable.Desc("§7Configure HUD elements")
public class HUDConfig extends Config {
    public HUDConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "hud_config"));
    }

    @Name("Profile Element")
    @Desc("§7This is your profile shown on the hud. (Name, Level, Picture)")
    public ConfigGroup profileElementGroup = new ConfigGroup("profile_element_group");

    @Name("Show Profile Element")
    public ValidatedBoolean showProfileElement = new ValidatedBoolean(true);

    @Name("X Position in %")
    public ValidatedInt xPosition = new ValidatedInt(1, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @Name("Y Position in %")
    public ValidatedInt yPosition = new ValidatedInt(2, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @ConfigGroup.Pop
    @Name("Anchor point")
    public ValidatedEnum<HorizontalAlignment> alignment = new ValidatedEnum<>(HorizontalAlignment.LEFT, ValidatedEnum.WidgetType.CYCLING);

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON5;
    }
}
