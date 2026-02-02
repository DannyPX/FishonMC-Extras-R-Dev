package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Objects;
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
    private ProfileDataModel profileData = new ProfileDataModel();
    private ProfileDataModel profileDataOld = new ProfileDataModel();

    public ProfileDataModel getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileDataModel profileData) {
        this.profileData = profileData;
        this.updateProfileData(profileData);
    }

    private void updateProfileData(ProfileDataModel profileData) {
        this.profileDataOld = profileData.copy();
        DataFileHandler.instance().saveToFile(DataModels.DataModelType.PROFILE_DATA);
    }
    //endregion

    //region Methods
    public void tick() {
        if(!profileDataOld.equals(profileData)) {
            this.updateProfileData(profileData);
        }
    }

    public void init() {
        if(minecraftClient.player != null) this.setUUID(minecraftClient.player.getUuid());
    }

    private void setUUID(UUID uuid) {
        this.profileData.uuid = uuid;
    }
    //endregion

    //region Model
    public static class ProfileDataModel extends DataModels.DataModel {
        private static final String PROFILE_DATA_MODEL_VERSION = "0";

        public ProfileDataModel() {
            super(PROFILE_DATA_MODEL_VERSION, null);
        }

        public ProfileDataModel(ProfileDataModel oldData) {
            super(oldData.version, oldData.uuid);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;

            return obj instanceof ProfileDataModel oldStatsData
                    && this.uuid.equals(oldStatsData.uuid);
        }

        public ProfileDataModel copy() {
            return new ProfileDataModel(this);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "profileData", Pair.of(Text.literal("[profileData]"), TextHelper.literal(getProfileData()))
        );
    }
    //endregion
}
