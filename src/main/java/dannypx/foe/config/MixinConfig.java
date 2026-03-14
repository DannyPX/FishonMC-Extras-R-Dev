package dannypx.foe.config;

import dannypx.foe.FishOnMCExtras;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Version(version = 1)
@Translatable.Name("Mixin Toggles")
@Translatable.Desc("§4WARNING §7These are the toggles to mixins. Do not touch these unless you know what you are doing")
public class MixinConfig extends Config {
    public MixinConfig() {
        super(Identifier.of(FishOnMCExtras.MOD_ID, "mixin_config"));
    }

    @Name("ArmorFeatureRenderer Mixin")
    public ConfigGroup armorFeatureRendererMixinGroup = new ConfigGroup("armor_feature_renderer_mixin_group");

    @ConfigGroup.Pop
    public ValidatedBoolean armorFeatureRendererRenderArmor = new ValidatedBoolean(true);

    @Name("BossBar Mixin")
    public ConfigGroup bossBarMixinGroup = new ConfigGroup("bossbar_mixin_group");

    @ConfigGroup.Pop
    public ValidatedBoolean bossBarDisableRender = new ValidatedBoolean(true);

    @Name("ClientPlayNetworkHandler Mixin")
    public ConfigGroup clientPlayNetworkHandlerMixinGroup = new ConfigGroup("client_play_network_handler_mixin_group");

    public ValidatedBoolean clientPlayNetworkHandlerOnPlayerList = new ValidatedBoolean(true);

    @ConfigGroup.Pop
    public ValidatedBoolean clientPlayNetworkHandlerOnPlayerRemove = new ValidatedBoolean(true);

    @Name("DrawContext Mixin")
    public ConfigGroup drawContextMixinGroup = new ConfigGroup("drawcontext_mixin_group");

    public ValidatedBoolean drawContextAlterDrawStackCount = new ValidatedBoolean(true);

    @ConfigGroup.Pop
    public ValidatedBoolean drawContextAlterDrawStackOverlay = new ValidatedBoolean(true);

    @Name("FishingBobberEntityRenderer Mixin")
    public ConfigGroup fishingBobberEntityRendererMixinGroup = new ConfigGroup("fishing_bobber_entity_renderer_mixin_group");

    public ValidatedBoolean fishingBobberEntityRendererVertex = new ValidatedBoolean(true);

    @ConfigGroup.Pop
    public ValidatedBoolean fishingBobberEntityRendererRender = new ValidatedBoolean(true);

    @Name("HandledScreen Mixin")
    public ConfigGroup handledScreenMixinGroup = new ConfigGroup("handled_screen_mixin_group");

    @ConfigGroup.Pop
    public ValidatedBoolean handledScreenMixinGroupKeyPressed = new ValidatedBoolean(true);

    @Name("InGameHud Mixin")
    public ConfigGroup inGameHudMixinGroup = new ConfigGroup("in_game_hud");

    public ValidatedBoolean inGameHudSetTitle = new ValidatedBoolean(true);

    public ValidatedBoolean inGameHudSetSubtitle = new ValidatedBoolean(true);

    public ValidatedBoolean inGameHudRenderHeldItemToolTip = new ValidatedBoolean(true);

    public ValidatedBoolean inGameHudRenderScoreBoardSidebar = new ValidatedBoolean(true);

    public ValidatedBoolean inGameHudRenderExperienceBar = new ValidatedBoolean(true);

    public ValidatedBoolean inGameHudRenderExperienceLevel = new ValidatedBoolean(true);

    @ConfigGroup.Pop
    public ValidatedBoolean inGameHudRenderHotbar = new ValidatedBoolean(true);

    @Name("PlayerListHud Mixin")
    public ConfigGroup playerListHudMixinGroup = new ConfigGroup("player_list_hud_mixin_group");

    public ValidatedBoolean playerListHudRedirectRender = new ValidatedBoolean(true);

    public ValidatedBoolean playerListHudInjectRender = new ValidatedBoolean(true);

    @ConfigGroup.Pop
    public ValidatedBoolean playerListHudCollectPlayerEntries = new ValidatedBoolean(true);

    @Name("RecipeBookScreen Mixin")
    public ConfigGroup recipeBookScreenMixinGroup = new ConfigGroup("recipe_book_screen");

    @ConfigGroup.Pop
    public ValidatedBoolean recipeBookScreenAddRecipeBook = new ValidatedBoolean(true);

    @Name("RecipeBookWidget Mixin")
    public ConfigGroup recipeBookWidgetMixinGroup = new ConfigGroup("recipe_book_widget");

    @ConfigGroup.Pop
    public ValidatedBoolean recipeBookWidgetIsOpen = new ValidatedBoolean(true);

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSON;
    }
}
