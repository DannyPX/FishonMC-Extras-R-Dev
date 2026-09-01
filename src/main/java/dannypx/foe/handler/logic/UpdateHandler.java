package dannypx.foe.handler.logic;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.store.CustomHudDataHandler;
import dannypx.foe.handler.store.CustomTrackerDataHandler;
import dannypx.foe.handler.store.ProfileDataHandler;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.version.Version;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;

public class UpdateHandler extends Handler {
    private static UpdateHandler INSTANCE = new UpdateHandler();

    public static UpdateHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new UpdateHandler();
        }
        return INSTANCE;
    }

    //region Fields
    //endregion

    //region Methods
    public static void checkUpdate() {
        Version before = Version.of(ProfileDataHandler.instance().getProfileData().modVersion);
        Version now = Version.of(FishOnMCExtras.VERSION);
        Version v038 = Version.of("0.3.8");

        if(before.get() == null || now.compareTo(before) > 0) {
            // Put all update cycles here
            if(before.get() == null  || (v038.compareTo(before) > 0)) updateToV038();
        }
    }

    public static void updateToV038() {
        LoggerHandler.info("Update to 0.3.8 defaults");

        CustomTrackerDataHandler.instance().fixDefault();
        CustomHudDataHandler.instance().fixDefault();

        ProfileDataHandler.instance().updateVersion();
    }
    //endregion

    //region Dev

    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "key", Pair.of(Component.literal("value"), Component.empty())
        );
    }
    //endregion
}
