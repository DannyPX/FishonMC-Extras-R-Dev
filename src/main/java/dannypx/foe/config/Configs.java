package dannypx.foe.config;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;

public class Configs {
    public static MainConfig mainConfig = ConfigApiJava.registerAndLoadConfig(MainConfig::new, RegisterType.CLIENT);

    public static HUDConfig hudConfig = ConfigApiJava.registerAndLoadConfig(HUDConfig::new, RegisterType.CLIENT);
    public static HandlerConfig handlerConfig = ConfigApiJava.registerAndLoadConfig(HandlerConfig::new, RegisterType.CLIENT);
    public static _DebugConfig debugConfig = ConfigApiJava.registerAndLoadConfig(_DebugConfig::new, RegisterType.CLIENT);

    public static void init() {}
}
