package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Translatable.Name("§7Data Handler")
@Translatable.Desc("§4Advanced §7These are the back-end handlers for data. Disabling these might stop some functions from working. Do not touch these unless you know what you are doing.")
public class DataHandlerConfig extends Config {
    public DataHandlerConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "data_handler_config"));
    }

    @Name("Fetch Handlers")
    @Desc("§7Handles fetching data from various Minecraft elements")
    public fetchHandlerSection fetchHandlerSection = new fetchHandlerSection();
    public static class fetchHandlerSection extends ConfigSection {
        public fetchHandlerSection() {
            super();
        }

        @Desc("§7Data from Player List (Tab)")
        public ValidatedBoolean tabHandler = new ValidatedBoolean(true);

        @Desc("§7Data from Player Inventory")
        public ValidatedBoolean inventoryHandler = new ValidatedBoolean(true);
    }

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON5;
    }
}
