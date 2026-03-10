package dannypx.foe;

import dannypx.foe.config.Configs;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class FishOnMCExtras implements ModInitializer {
	public static final String MOD_ID = "fishonmcextras";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static String VERSION = FishOnMCExtras.getModVersion();
	public static Integer HUD_VERSION = 2;
	public static Integer BUTTON_VERSION = 1;

	public static final String QUEST_SCREEN = "\uEEE4\uD539";
	public static final String STATS_SCREEN = "\uEEE4\uD532";
	public static final String AUCTION_HOUSE_SCREEN = "\uEEE4\uD543";
	public static final String PERSONAL_VAULT_SCREEN = "Personal Vault #";
	public static final String STORAGE_SCREEN = " ";
	public static final String INVENTORY_SCREEN = "Inventory Screen";

	@Override
	public void onInitialize() {
		Configs.init();
	}

	private static String getModVersion() {
		if(getModContainer().isPresent()) {
			return getModContainer().get().getMetadata().getVersion().getFriendlyString();
		}
		return "N/A";
	}

	private static Optional<ModContainer> getModContainer() {
		return FabricLoader.getInstance().getModContainer(MOD_ID);
	}
}