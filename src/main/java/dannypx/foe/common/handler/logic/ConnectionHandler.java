package dannypx.foe.common.handler.logic;

import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.MutableText;

import java.util.Map;

public class ConnectionHandler {
    private static ConnectionHandler INSTANCE = new ConnectionHandler();

    public static ConnectionHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private boolean isOnServer = false;
    private boolean wasOnServer = false;

    public boolean isOnServer() {
        return isOnServer;
    }

    public boolean wasOnServer() {
        return wasOnServer;
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
            LoggerHandler.info("On server. (play.fishonmc.net)");
            wasOnServer = true;
        }
    }

    private boolean checkFOMCAddress() {
        ServerInfo serverEntry = minecraftClient.getCurrentServerEntry();
        if(serverEntry != null) {
            return serverEntry.address.equalsIgnoreCase("play.fishonmc.net")
                    || serverEntry.address.equalsIgnoreCase("fishonmc.net");
        } return false;
    }
    //endregion

    //region Dev
    protected Map<String, Pair<MutableText, Tooltip>> _getFields() {
        return Map.of(
                "isOnServer", Pair.of(TextHelper.literal(isOnServer()), null),
                "wasOnServer", Pair.of(TextHelper.literal(wasOnServer()), null)
        );
    }
    //endregion
}
