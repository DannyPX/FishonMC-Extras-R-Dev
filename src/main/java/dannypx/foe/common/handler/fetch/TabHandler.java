package dannypx.foe.common.handler.fetch;

import com.mojang.authlib.GameProfile;
import dannypx.foe.common.constants.Rank;
import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import dannypx.foe.mixin.accessor.PlayerListHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TabHandler {
    private static TabHandler INSTANCE = new TabHandler();

    public static TabHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TabHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private Text playerName = Text.empty();
    private Rank rank = Rank.DEFAULT;
    private String instance = "";
    private boolean isInInstance = false;

    public Text getPlayerName() {
        return playerName;
    }

    public Rank getRank() {
        return rank;
    }

    public String getInstance() {
        return instance;
    }

    public boolean isInInstance() {
        return isInInstance;
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
            this.rank = Rank.valueOfTagString(this.playerName.getString());
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
                "playerName", Pair.of(Text.literal(getPlayerName().getString()), Text.empty()),
                "rank", Pair.of(Text.literal(getRank().ID), Text.empty()),
                "instance", Pair.of(Text.literal(getInstance()), Text.empty()),
                "isInInstance", Pair.of(TextHelper.literal(isInInstance()), Text.empty())
        );
    }
    //endregion
}
