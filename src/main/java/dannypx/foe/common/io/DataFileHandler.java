package dannypx.foe.common.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.data.logic.LoggerHandler;
import dannypx.foe.common.data.store.ProfileHandler;
import dannypx.foe.common.type.Pair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public class DataFileHandler {
    private static DataFileHandler INSTANCE = new DataFileHandler();
    public static DataFileHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new DataFileHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final Path DATA_FOLDER = Path.of("data");
    private boolean isDataLoaded = false;

    public boolean isDataLoaded() {
        return isDataLoaded;
    }
    //endregion

    //region Methods
    public boolean init() {
        return loadDataToMemory(DataModels.DataModelType.PROFILE_DATA);
    }

    private boolean loadDataToMemory(DataModels.DataModelType dataModelType) {
        DataModels.DataModel data = this.getData(dataModelType);
        try {
            Path configDir = getConfigDir(data.uuid);
            Files.createDirectories(configDir);
            Path filePath = configDir.resolve(dataModelType.FILENAME + ".json");
            if(!checkIfFileExist(filePath)) {
                Files.createFile(filePath);
                this.isDataLoaded = true;
                this.saveToFile(dataModelType);
                return true;
            }
            String jsonFromFile = Files.readString(filePath);
            setData(dataModelType, jsonFromFile);
            this.isDataLoaded = true;

        } catch (IOException e) {
            LoggerHandler.error(e);
        }
        return true;
    }

    public boolean saveToFile(DataModels.DataModelType dataModelType) {
        DataModels.DataModel data = this.getData(dataModelType);
        try {
            Path configDir = getConfigDir(data.uuid);
            Files.createDirectories(configDir);
            Path filePath = configDir.resolve(dataModelType.FILENAME + ".json");
            String resultJson = dataModelToJson(data);
            Files.writeString(filePath, resultJson);
        } catch (IOException e) {
            LoggerHandler.error(e);
        }
        return true;
    }

    private Path getConfigDir(UUID uuid) {
        return FabricLoader
                .getInstance()
                .getConfigDir()
                .resolve(FishOnMCExtras.MOD_ID)
                .resolve(DATA_FOLDER)
                .resolve(uuid.toString());
    }

    private boolean checkIfFileExist(Path filePath) {
        return Files.exists(filePath);
    }

    private String dataModelToJson(DataModels.DataModel dataModel) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(dataModel);
    }

    private DataModels.DataModel getData(DataModels.DataModelType dataModelType) {
        return switch (dataModelType) {
            case PROFILE_DATA -> ProfileHandler.instance().getProfileData();
        };
    }

    private void setData(DataModels.DataModelType dataModelType, String json) {
        Gson gson = new GsonBuilder().create();
        switch (dataModelType) {
            case PROFILE_DATA ->
                    ProfileHandler.instance().setProfileData(gson.fromJson(json, DataModels.ProfileDataModel.class));
        };
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<Text, Tooltip>> getFields() {
        return Map.of(
                "isDataLoaded", Pair.of(Text.literal(Boolean.toString(isDataLoaded())), null)
        );
    }
    //endregion
}
