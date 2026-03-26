package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.StringValue;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.regex.Pattern;

public class NetworkHandler extends Handler {
    private static NetworkHandler INSTANCE = new NetworkHandler();

    public static NetworkHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private int ping = 0;

    public int getPing() {
        return ping;
    }

    public Pair<Boolean, CustomTextValue> getNetwork(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(ping)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "ping" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(getPing())));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods

    @Override
    public void tick() {
        this.fetchFromPlayerListEntry();
    }

    private void fetchFromPlayerListEntry() {
        if(minecraftClient.player != null && minecraftClient.getNetworkHandler() != null) {
            PlayerListEntry entry = minecraftClient.getNetworkHandler().getPlayerListEntry(minecraftClient.player.getUuid());

            if(entry != null) {
                ping = entry.getLatency();
            }
        }
    }

    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
