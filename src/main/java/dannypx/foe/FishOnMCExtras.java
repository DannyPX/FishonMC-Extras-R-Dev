package dannypx.foe;

import dannypx.foe.config.Configs;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishOnMCExtras implements ModInitializer {
	public static final String MOD_ID = "fishonmcextras";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Configs.init();
	}
}