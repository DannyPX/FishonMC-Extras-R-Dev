package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.UUID;

public class ProfileDataHandler {
    private static ProfileDataHandler INSTANCE = new ProfileDataHandler();

    public static ProfileDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ProfileDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private DataModels.ProfileDataModel profileData = new DataModels.ProfileDataModel();

    public DataModels.ProfileDataModel getProfileData() {
        return profileData;
    }

    public void setProfileData(DataModels.ProfileDataModel profileData) {
        this.profileData = profileData;
    }
    //endregion

    //region Methods
    public void init() {
        if(minecraftClient.player != null) this.setUUID(minecraftClient.player.getUuid());
    }

    private void setUUID(UUID uuid) {
        this.profileData.uuid = uuid;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, Tooltip>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), null)
        );
    }
    //endregion
}
