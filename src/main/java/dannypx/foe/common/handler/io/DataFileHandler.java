package dannypx.foe.common.handler.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.handler.store.ConstantDataHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.handler.store.StatsDataHandler;
import dannypx.foe.common.type.Pair;
import dannypx.foe.common.type.type_adapter.ItemStackAdapter;
import dannypx.foe.common.type.type_adapter.TextAdapter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
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
    public void tick() {
        ProfileDataHandler.instance().tick();
        StatsDataHandler.instance().tick();
        ConstantDataHandler.instance().tick();
    }

    public void init() {
        loadDataToMemory(DataModels.DataModelType.PROFILE_DATA);
        loadDataToMemory(DataModels.DataModelType.STATS_DATA);
        loadDataToMemory(DataModels.DataModelType.CONSTANT_DATA);
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
                return this.saveToFile(dataModelType);
            }
            String jsonFromFile = Files.readString(filePath);
            setData(dataModelType, jsonFromFile);
            this.isDataLoaded = true;

        } catch (IOException e) {
            LoggerHandler.error(e);
        }
        return false;
    }

    public boolean saveToFile(DataModels.DataModelType dataModelType) {
        DataModels.DataModel data = this.getData(dataModelType);
        try {
            Path configDir = getConfigDir(data.uuid);
            Files.createDirectories(configDir);
            Path filePath = configDir.resolve(dataModelType.FILENAME + ".json");
            String resultJson = dataModelToJson(data);
            Files.writeString(filePath, resultJson);

            LoggerHandler.info("Updating file: " + dataModelType.FILENAME + ".json");
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
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeAdapter(Text.class, new TextAdapter())
                .create();
        return gson.toJson(dataModel);
    }

    private DataModels.DataModel getData(DataModels.DataModelType dataModelType) {
        return switch (dataModelType) {
            case PROFILE_DATA -> ProfileDataHandler.instance().getProfileData();
            case STATS_DATA -> StatsDataHandler.instance().getStatsData();
            case CONSTANT_DATA -> ConstantDataHandler.instance().getConstantData();
        };
    }

    private void setData(DataModels.DataModelType dataModelType, String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeAdapter(Text.class, new TextAdapter())
                .create();
        switch (dataModelType) {
            case PROFILE_DATA ->
                    ProfileDataHandler.instance().setProfileData(gson.fromJson(json, ProfileDataHandler.ProfileDataModel.class));
            case STATS_DATA ->
                    StatsDataHandler.instance().setStatsData(gson.fromJson(json, StatsDataHandler.StatsDataModel.class));
            case CONSTANT_DATA ->
                    ConstantDataHandler.instance().setConstantData(gson.fromJson(json, ConstantDataHandler.ConstantDataModel.class));
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "isDataLoaded", Pair.of(Text.literal(Boolean.toString(isDataLoaded())), Text.empty())
        );
    }
    //endregion
}
