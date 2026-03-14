package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.handler.logic.NotifierHandler;
import dannypx.foe.common.handler.logic.PlaceholderHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.custom_text.CustomTextValue;
import dannypx.foe.common.type.custom_text.StringValue;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Pattern;

public class ProfileDataHandler extends Handler {
    private static ProfileDataHandler INSTANCE = new ProfileDataHandler();

    public static ProfileDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ProfileDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
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

    public Pair<Boolean, CustomTextValue> getProfileData(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(active_pet_slot|has_imported_stats|is_in_crew_chat|has_imported_crew)$");

            if(Objects.equals(params[0], "data")
                    && params.length == 3
                    && fieldPattern.matcher(params[1]).matches()
            ) {
                return switch(params[1]) {
                    case "active_pet_slot" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(getProfileData().activePetSlot)));
                    case "has_imported_stats" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(getProfileData().hasImportedStats)));
                    case "is_in_crew_chat" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(getProfileData().isInCrewChat)));
                    case "has_imported_crew" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(getProfileData().hasImportedCrew)));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
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

    public void updateImportCrew(boolean importedCrew) {
        if(minecraftClient.player != null) {
            if(importedCrew) {
                NotifierHandler.instance().removeNotification(NotifierHandler.IMPORT_CREW_KEY);
                profileData.hasImportedCrew = true;
                this.needsUpdate = true;
            }
        }
    }
    //endregion

    //region Model
    public static class ProfileDataModel extends DataModels.DataModel {
        private static final String PROFILE_DATA_MODEL_VERSION = "0.4";

        public int activePetSlot = -1;
        public boolean hasImportedStats = false;

        public boolean hasImportedCrew = false;
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
