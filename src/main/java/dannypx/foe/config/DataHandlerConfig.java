package dannypx.foe.config;

import com.google.gson.JsonArray;
import dannypx.foe.FishOnMCExtras;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 0)
@Translatable.Name("Handlers")
@Translatable.Desc("§4WARNING §7These are the back-end handlers. Disabling these might stop some " +
        "functions from working. Do not touch these unless you know what you are doing")
public class DataHandlerConfig extends Config {

    public DataHandlerConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "data_handler_config"));
    }

    //region Fetch Handler Group
    @Name("Fetch Handlers")
    @Desc("§7Handles fetching data from various Minecraft elements")
    public ConfigGroup fetchHandlerGroup = new ConfigGroup("fetch_handler_group");

    @Desc("§7Data from Player List (Tab)")
    public ValidatedBoolean tabHandler = new ValidatedBoolean(true);

    @Desc("§7Data from Scoreboard")
    public ValidatedBoolean scoreboardHandler = new ValidatedBoolean(true);

    @Desc("§7Data from Client Player")
    public ValidatedBoolean clientPlayerHandler = new ValidatedBoolean(true);

    @Desc("§7Data from Player Inventory")
    public ValidatedBoolean inventoryHandler = new ValidatedBoolean(true);

    @ConfigGroup.Pop
    @Desc("§7Data from Boss Bar")
    public ValidatedBoolean bossBarHandler = new ValidatedBoolean(true);
    //endregion

    //region Logic Handler Group
    @Name("Logic Handlers")
    @Desc("§7Handles logic of data")
    public ConfigGroup fetchLogicGroup = new ConfigGroup("fetch_logic_group");


    @Desc("§7Handles loading mod when on server")
    public ValidatedBoolean loadingHandler = new ValidatedBoolean(true);

    @Desc("§7Handles key binds")
    public ValidatedBoolean keyBindHandler = new ValidatedBoolean(true);
    //endregion

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON5;
    }
}
