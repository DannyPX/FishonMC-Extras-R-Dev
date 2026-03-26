package dannypx.foe.handler.logic;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.config.Configs;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class LoggerHandler {
    //region Methods
    public static void error(Exception e) {
        error(e.getMessage());
    }

    public static void error(String e) {
        FishOnMCExtras.LOGGER.error("[FoER] {}", e);
    }

    public static void info(String m) {
        FishOnMCExtras.LOGGER.info("[FoER] {}", m);
    }

    public static void info(Text m) {
        LoggerHandler.info(m.getString());
    }

    public static void _debug(String m) {
        _debug(Text.literal(m).formatted(Formatting.YELLOW));
    }

    public static void _debug(String m, ItemStack item) {
        _debug(Text.literal(m).formatted(Formatting.YELLOW), item);
    }

    public static void _debug(Text m) {
        if(Configs.debugConfig.debugMode.get()) {
            LoggerHandler.info(Text.empty().append("DEBUG: ").append(m));

            if(Configs.debugConfig.showNotification.get()) {
                NotifierHandler.instance().addNotification(
                        new NotifierHandler.Notification(1, 1, Configs.handlerConfig.debugDismissalTime.get(), List.of(m))
                );
            }

        }
    }

    public static void _debug(Text m, ItemStack item) {
        if(Configs.debugConfig.debugMode.get()) {
            LoggerHandler.info(Text.empty().append("DEBUG: ").append(m));

            if(Configs.debugConfig.showNotification.get()) {
                NotifierHandler.instance().addNotification(
                        new NotifierHandler.Notification(item, 1, 1, Configs.handlerConfig.debugDismissalTime.get(), List.of(m))
                );
            }

        }
    }
    //endregion
}
