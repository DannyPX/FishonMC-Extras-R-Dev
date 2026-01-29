package dannypx.foe.common.handler.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dannypx.foe.common.handler.io.DataModels;
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

    private String dataModelToJson(DataModels.DataModel dataModel) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(dataModel);
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "profileData", Pair.of(Text.literal("[profileData]"), TextHelper.literal(profileData))
        );
    }
    //endregion
}
