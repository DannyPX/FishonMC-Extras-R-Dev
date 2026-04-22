package dannypx.foe.handler.store;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.io.DataFileHandler;
import dannypx.foe.handler.io.DataModels;
import dannypx.foe.handler.logic.NotifierHandler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.helper.ComponentHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.PlaceholderValue;
import dannypx.foe.type.custom_text.StringValue;
import java.util.*;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

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

    public Pair<Boolean, PlaceholderValue> getProfileData(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(active_pet_slot|has_imported_stats|is_in_crew_chat|has_imported_crew|tournament_contribution)$");

            if(Objects.equals(params[0], "data")
                    && params.length >= 2
                    && fieldPattern.matcher(params[1]).matches()
            ) {
                return switch(params[1]) {
                    case "active_pet_slot" -> PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(getProfileData().activePetSlot)));
                    case "has_imported_stats" -> PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(getProfileData().hasImportedStats)));
                    case "is_in_crew_chat" -> PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(getProfileData().isInCrewChat)));
                    case "has_imported_crew" -> PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(getProfileData().hasImportedCrew)));
                    case "tournament_contribution" -> PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(getProfileData().tournamentContribution)));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        if(profileData.uuid == null && minecraft.player != null) {
            profileData.uuid = minecraft.player.getUUID();
        } else if(profileData.uuid != null && this.needsUpdate) {
            this.updateProfileData();
        } else if(!ProfileDataModel.PROFILE_DATA_MODEL_VERSION.equals(profileData.version)) {
            profileData.version = ProfileDataModel.PROFILE_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraft.player != null) this.setUUID(minecraft.player.getUUID());
    }

    private void setUUID(UUID uuid) {
        this.profileData.uuid = uuid;
    }

    public void updatePet(boolean enablePet) {
        if (minecraft.player != null) {
            if(enablePet) {
                profileData.activePetSlot = minecraft.player.getInventory().getSelectedSlot();
            } else {
                profileData.activePetSlot = -1;
            }

            this.needsUpdate = true;
        }
    }

    public void updateImportStats(boolean importedStats) {
        if(minecraft.player != null) {
            if(importedStats) {
                NotifierHandler.instance().removeNotification(NotifierHandler.IMPORT_STATS_KEY);
                profileData.hasImportedStats = true;
                this.needsUpdate = true;
            }
        }
    }

    public void updateCrewChat(boolean isInCrewChat) {
        if(minecraft.player != null) {
            profileData.isInCrewChat = isInCrewChat;
            this.needsUpdate = true;
        }
    }

    public void updateTournamentContribution(boolean isTournamentContribution) {
        if(minecraft.player != null) {
            profileData.tournamentContribution = isTournamentContribution;
            this.needsUpdate = true;
        }
    }

    public void updateImportCrew(boolean importedCrew) {
        if(minecraft.player != null) {
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
        private static final String PROFILE_DATA_MODEL_VERSION = "0.5";

        public int activePetSlot = -1;
        public boolean hasImportedStats = false;

        public boolean hasImportedCrew = false;
        public boolean isInCrewChat = false;

        public boolean tournamentContribution = true;

        public ProfileDataModel() {
            super(PROFILE_DATA_MODEL_VERSION, null);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "profileData", Pair.of(Component.literal("[profileData]"), ComponentHelper.literal(getProfileData()))
        );
    }
    //endregion
}
