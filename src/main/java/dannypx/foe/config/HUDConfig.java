package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.type.Alignment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice;
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
    public ValidatedInt profileElementXPosition = new ValidatedInt(1, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @Name("Y Position in %")
    public ValidatedInt profileElementYPosition = new ValidatedInt(2, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @ConfigGroup.Pop
    @Name("Anchor point")
    public ValidatedChoice<Alignment> profileElementAlignment = new ValidatedChoice<>(Alignment.TOP_LEFT, Alignment.getTopCorners(), new ValidatedEnum<>(Alignment.class).instanceEntry(), ValidatedChoice.WidgetType.CYCLING);

    @Name("Location Element")
    @Desc("§7This the location element. (Time, Temperature, Weather, Location)")
    public ConfigGroup locationElementGroup = new ConfigGroup("location_element_group");

    @Name("Show Profile Element")
    public ValidatedBoolean showLocationElement = new ValidatedBoolean(true);

    @Name("X Position in %")
    public ValidatedInt locationElementXPosition = new ValidatedInt(1, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @Name("Y Position in %")
    public ValidatedInt locationElementYPosition = new ValidatedInt(2, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @ConfigGroup.Pop
    @Name("Anchor point")
    public ValidatedChoice<Alignment> locationElementAlignment = new ValidatedChoice<>(Alignment.TOP_RIGHT, Alignment.getTopCorners(), new ValidatedEnum<>(Alignment.class).instanceEntry(), ValidatedChoice.WidgetType.CYCLING);

    @Name("Hotbar Element")
    @Desc("§7This the hotbar element")
    public ConfigGroup hotbarElementGroup = new ConfigGroup("hotbar_element_group");

    @Name("Show Hotbar Element")
    public ValidatedBoolean showHotbarElement = new ValidatedBoolean(true);

    @Name("X Position in %")
    public ValidatedInt hotbarElementXPosition = new ValidatedInt(50, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @Name("Y Position in %")
    public ValidatedInt hotbarElementYPosition = new ValidatedInt(2, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @Name("Anchor point")
    public ValidatedChoice<Alignment> hotbarElementAlignment = new ValidatedChoice<>(Alignment.BOTTOM, Alignment.getBottom(), new ValidatedEnum<>(Alignment.class).instanceEntry(), ValidatedChoice.WidgetType.CYCLING);

    @Name("Hotbar Options")
    @Desc("§7Options for the hotbar")
    public ConfigGroup hotbarOptions = new ConfigGroup("hotbar_options_group");

    @Name("Show Armor")
    public ValidatedBoolean showHotbarArmor = new ValidatedBoolean(true);

    @Name("Show Fishing Parts")
    public ValidatedBoolean showHotbarParts = new ValidatedBoolean(true);

    @ConfigGroup.Pop
    @ConfigGroup.Pop
    @Name("Show Active Bait")
    public ValidatedBoolean showHotbarBait = new ValidatedBoolean(true);

    @Name("Pet Element")
    @Desc("§7This is your pet shown on the hud. (Name, Level, Picture)")
    public ConfigGroup petElementGroup = new ConfigGroup("pet_element_group");

    @Name("Show Pet Element")
    public ValidatedBoolean showPetElement = new ValidatedBoolean(true);

    @Name("X Position in %")
    public ValidatedInt petElementXPosition = new ValidatedInt(1, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @Name("Y Position in %")
    public ValidatedInt petElementYPosition = new ValidatedInt(20, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @ConfigGroup.Pop
    @Name("Anchor point")
    public ValidatedChoice<Alignment> petElementAlignment = new ValidatedChoice<>(Alignment.TOP_LEFT, Alignment.getTopCorners(), new ValidatedEnum<>(Alignment.class).instanceEntry(), ValidatedChoice.WidgetType.CYCLING);

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON5;
    }
}
