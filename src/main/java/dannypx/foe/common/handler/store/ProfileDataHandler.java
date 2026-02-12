package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.handler.logic.NotifierHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
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
    private ProfileDataModel profileData = new ProfileDataModel();
    private boolean needsUpdate = false;

    public ProfileDataModel getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileDataModel profileData) {
        this.profileData = profileData;
        this.updateProfileData();
    }

    private void updateProfileData() {
        if(needsUpdate) {
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.PROFILE_DATA);
        }
        this.needsUpdate = false;
    }
    //endregion

    //region Methods
    public void tick() {
        if(profileData.uuid == null && minecraftClient.player != null) {
            profileData.uuid = minecraftClient.player.getUuid();
        } else if(profileData.uuid != null && this.needsUpdate) {
            this.updateProfileData();
        } else if(!ProfileDataModel.PROFILE_DATA_MODEL_VERSION.equals(profileData.version)) {
            profileData.version = ProfileDataModel.PROFILE_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraftClient.player != null) this.setUUID(minecraftClient.player.getUuid());
    }

    private void setUUID(UUID uuid) {
        this.profileData.uuid = uuid;
    }

    public void updatePet(boolean enablePet) {
        if (minecraftClient.player != null) {
            if(enablePet) {
                profileData.activePetSlot = minecraftClient.player.getInventory().selectedSlot;
            } else {
                profileData.activePetSlot = -1;
            }

            this.needsUpdate = true;
        }
    }

    public void updateImportStats(boolean importedStats) {
        if(minecraftClient.player != null) {
            if(importedStats) {
                NotifierHandler.instance().removeNotification(NotifierHandler.IMPORT_STATS_KEY);
                profileData.hasImportedStats = true;
                this.needsUpdate = true;
            }
        }
    }

    public void updateCrewChat(boolean isInCrewChat) {
        if(minecraftClient.player != null) {
            profileData.isInCrewChat = isInCrewChat;
            this.needsUpdate = true;
        }
    }
    //endregion

    //region Model
    public static class ProfileDataModel extends DataModels.DataModel {
        private static final String PROFILE_DATA_MODEL_VERSION = "0.3";

        public int activePetSlot = -1;
        public boolean hasImportedStats = false;

        public boolean isInCrewChat = false;

        public ProfileDataModel() {
            super(PROFILE_DATA_MODEL_VERSION, null);
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
