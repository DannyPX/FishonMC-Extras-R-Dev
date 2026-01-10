package dannypx.foe.common.handler.logic;

import dannypx.foe.FishOnMCExtras;
import net.minecraft.text.Text;

public class LoggerHandler {
    //region Methods
    public static void error(Exception e) {
        FishOnMCExtras.LOGGER.error("[FoER] {}", e.getMessage());
    }

    public static void info(String m) {
        FishOnMCExtras.LOGGER.info("[FoER] {}", m);
    }

    public static void info(Text m) {
        LoggerHandler.info(m.getString());
    }
    //endregion
}
