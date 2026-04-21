package dannypx.foe.handler.logic;

import dannypx.foe.handler.Handler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.StringValue;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.regex.Pattern;

public class ConnectionHandler extends Handler {
    private static ConnectionHandler INSTANCE = new ConnectionHandler();

    public static ConnectionHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private boolean isOnServer = false;
    private boolean wasOnServer = false;

    public boolean isOnServer() {
        return isOnServer;
    }

    public boolean wasOnServer() {
        return wasOnServer;
    }

    public Pair<Boolean, CustomTextValue> getConnection(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(is_on_server|was_on_server)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "is_on_server" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(isOnServer())));
                    case "was_on_server" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(wasOnServer())));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void init() {
        this.checkIfOnServer();
    }

    public void onLeave() {
        isOnServer = false;
    }

    private void checkIfOnServer() {
        isOnServer = this.checkFOMCAddress();
        if(isOnServer) {
            LoggerHandler.info("On server (play.fishonmc.net)");
            wasOnServer = true;
        } else {
            LoggerHandler.info("Not on server");
        }
    }

    private boolean checkFOMCAddress() {
        ServerInfo serverEntry = minecraftClient.getCurrentServerEntry();
        if(serverEntry != null) {
            return serverEntry.address.equalsIgnoreCase("play.fishonmc.net")
                    || serverEntry.address.equalsIgnoreCase("asia.fishonmc.net")
                    || serverEntry.address.equalsIgnoreCase("fishonmc.net");
        } return false;
    }
    //endregion

    //region Dev
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "isOnServer", Pair.of(TextHelper.literal(isOnServer()), Text.empty()),
                "wasOnServer", Pair.of(TextHelper.literal(wasOnServer()), Text.empty())
        );
    }
    //endregion
}
