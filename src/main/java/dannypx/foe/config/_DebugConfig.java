package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.debug._DebugHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.type.Alignment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 0)
@Translatable.Name("Debug Configuration")
public class _DebugConfig extends Config {
    public _DebugConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "debug_config"));
    }

    @Name("Enable Debug Mode")
    @Desc("§4WARNING §7This is for advanced users")
    public ValidatedBoolean debugMode = new ValidatedBoolean(false);

    @Name("Debug Field Element")
    @Desc("§7This is your profile shown on the hud. (Name, Level, Picture)")
    public ConfigGroup debugFieldGroup = new ConfigGroup("profile_element_group");

    @Name("Show Profile Element")
    public ValidatedBoolean debugFieldElement = new ValidatedBoolean(true);

    @Name("X Position in %")
    public ValidatedInt debugFieldXPosition = new ValidatedInt(1, 100, 0, ValidatedNumber.WidgetType.SLIDER);

    @Name("Y Position in %")
    public ValidatedInt debugFieldYPosition = new ValidatedInt(2, 100, 0, ValidatedNumber.WidgetType.SLIDER);


    @Name("Anchor point")
    public ValidatedChoice<Alignment> debugFieldAlignment = new ValidatedChoice<>(Alignment.BOTTOM_RIGHT, Alignment.getCorners(), new ValidatedEnum<>(Alignment.class).instanceEntry(), ValidatedChoice.WidgetType.CYCLING);

    @Name("Chosen Handler")
    public ValidatedChoice<String> debugFieldHandlerChoice = new ValidatedChoice<>(
            LoadingHandler.class.getName(),
            _DebugHandler.instance()._getHandlerNames(),
            new ValidatedString().instanceEntry(),
            ValidatedChoice.WidgetType.SCROLLABLE);

    @ConfigGroup.Pop
    @Name("Chosen Field")
    public ValidatedChoice<String> debugFieldFieldChoice = new ValidatedChoice<>(
            _DebugHandler.instance()._getFields().get(LoadingHandler.class.getName()).keySet().stream().findFirst().orElse(""),
            _DebugHandler.instance()._getFieldNames(),
            new ValidatedString().instanceEntry(),
            ValidatedChoice.WidgetType.SCROLLABLE);

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON5;
    }
}
