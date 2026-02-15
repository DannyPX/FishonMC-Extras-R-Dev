package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 0)
@Translatable.Name("Renderer Configuration")
@Translatable.Desc("§7Configure renderer of various things")
public class RendererConfig extends Config {
    public RendererConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "renderer_config"));
    }

    @Name("Item Rarity Marker")
    @Desc("§7This is the small marker on items that shows rarity")
    public ConfigGroup itemRarityGroup = new ConfigGroup("item_rarity_group");

    @Name("Show Rarity marker")
    public ValidatedBoolean showMarker = new ValidatedBoolean(true);

    @ConfigGroup.Pop
    @Name("Blacklist items from rendering rarity marker")
    @Desc("§7Blacklist item types here.\nYou can put item types here separated by commas.\n\nIf you don't know what item type certain items are, check in the Debug Screen.\n\n1. Turn on Debug mode in Debug Configurations\n2. Hold an item\n3. Go to the Debug Screen\n4. Scroll to InventoryHandler\n5. You can read the item type in Current Held Item")
    public ValidatedString blackListItems = new ValidatedString.Builder("")
            .withCorrector().build();

    @Name("Entity Model Renderer")
    @Desc("§7This is the custom model renderer for entities")
    public ConfigGroup entityModelGroup = new ConfigGroup("entity_model_renderer_group");

    @ConfigGroup.Pop
    @Name("Use the 3D bobber texture")
    public ValidatedBoolean showNewBobber = new ValidatedBoolean(true);

    @Name("Small Stack Count Number")
    public ConfigGroup smallStackCountGroup = new ConfigGroup("small_stack_count_group");

    @Name("Use Small Stack Count Number")
    @Desc("§7Disabling this will also stop the counter from displaying on baits/lures")
    public ValidatedBoolean useSmallStackCountNumber = new ValidatedBoolean(true);

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON;
    }
}
