package dannypx.foe.handler.fetch;

import com.mojang.authlib.GameProfile;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.StringValue;
import dannypx.foe.type.custom_text.TextValue;
import dannypx.foe.mixin.accessor.PlayerListHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class TabHandler extends Handler {
    private static TabHandler INSTANCE = new TabHandler();

    public static TabHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TabHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private Text playerName = Text.empty();
    private String instance = "";
    private boolean isInInstance = false;

    public Text getPlayerName() {
        return playerName;
    }

    public String getInstance() {
        return instance;
    }

    public boolean isInInstance() {
        return isInInstance;
    }

    public Pair<Boolean, CustomTextValue> getTab(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(player_name|instance|is_in_instance)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "player_name" -> PlaceholderHandler.getTextValue(new TextValue(getPlayerName()));
                    case "instance" -> PlaceholderHandler.getTextValue(new StringValue(getInstance()));
                    case "is_in_instance" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(isInInstance())));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        this.fetchFromPlayerList();
    }

    public static GameProfile getPlayer(UUID uuid) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(uuid);
            return playerListEntry != null ? playerListEntry.getProfile() : null;
        }
        return null;
    }

    private void fetchFromPlayerList() {
        try {
            PlayerListHud playerListHud = minecraftClient.inGameHud.getPlayerListHud();
            this.fetchFromListing(playerListHud);
            this.fetchFromHeaderAndFooter(playerListHud);
        } catch (Exception e) {
            LoggerHandler.error(e);
        }
    }

    private void fetchFromListing(PlayerListHud playerListHud) {
        if (minecraftClient.player != null) {
            this.playerName = playerListHud.getPlayerName(
                    Objects.requireNonNull(minecraftClient.getNetworkHandler())
                            .getPlayerListEntry(minecraftClient.player.getUuid())
            );
        }
    }

    private void fetchFromHeaderAndFooter(PlayerListHud playerListHud) {
        if(((PlayerListHudAccessor) playerListHud).getFooter() != null) {
            if(((PlayerListHudAccessor) playerListHud).getFooter().getString().contains("ɪɴꜱᴛᴀɴᴄᴇ")) {
                this.isInInstance = true;
                String footer = ((PlayerListHudAccessor) playerListHud).getFooter().getString();
                this.instance = footer.substring(
                        footer.indexOf("ɪɴꜱᴛᴀɴᴄᴇ") + 8,
                        footer.lastIndexOf("(")
                ).trim();
            } else {
                this.isInInstance = false;
            }
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "playerName", Pair.of(getPlayerName().copy(), Text.empty()),
                "instance", Pair.of(Text.literal(getInstance()), Text.empty()),
                "isInInstance", Pair.of(TextHelper.literal(isInInstance()), Text.empty())
        );
    }
    //endregion
}
