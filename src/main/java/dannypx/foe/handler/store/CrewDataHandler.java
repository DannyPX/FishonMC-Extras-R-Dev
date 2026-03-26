package dannypx.foe.handler.store;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.io.DataFileHandler;
import dannypx.foe.handler.io.DataModels;
import dannypx.foe.handler.logic.CrewHandler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.StringValue;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Pattern;

public class CrewDataHandler extends Handler {
    private static CrewDataHandler INSTANCE = new CrewDataHandler();

    public static CrewDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new CrewDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private CrewDataModel crewData = new CrewDataModel();
    private boolean needsUpdate = false;

    public CrewDataModel getCrewData() {
        return crewData;
    }

    public Pair<Boolean, CustomTextValue> getCrewData(String[] params) {
        if(params.length > 0) {
            Pattern intPattern = Pattern.compile("^-?\\d+$");
            Pattern crewPattern = Pattern.compile("^(id|name)$");

            if(Objects.equals(params[0], "data")
                    && params.length == 3
                    && intPattern.matcher(params[1]).matches()
                    && crewPattern.matcher(params[2]).matches()
            ) {
                int index = Integer.parseInt(params[1]);
                List<Pair<UUID, String>> crewList = CrewHandler.instance().getCrewListOrdered();
                if(crewList.size() > index) {
                    Pair<UUID, String> crew = crewList.get(index);
                    return switch (params[2]) {
                        case "id" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(crew.value1())));
                        case "name" -> PlaceholderHandler.getTextValue(new StringValue(crew.value2()));
                        default -> PlaceholderHandler.noResult();
                    };
                }
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        if(crewData.uuid == null && minecraftClient.player != null) {
            crewData.uuid = minecraftClient.player.getUuid();
        } else if(crewData.uuid != null && this.needsUpdate) {
            this.updateCrewData();
        } else if(!CrewDataModel.CREW_DATA_MODEL_VERSION.equals(crewData.version)) {
            crewData.version = CrewDataModel.CREW_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraftClient.player != null) this.setUUID(minecraftClient.player.getUuid());
    }

    private void setUUID(UUID uuid) {
        this.crewData.uuid = uuid;
    }

    public void updateCrewList(Map<UUID, Pair<String, ItemStack>> crewList) {
        this.crewData.crewList = crewList;
        this.needsUpdate = true;
    }

    public void setCrewData(CrewDataModel crewData) {
        this.crewData = crewData;
        this.updateCrewData();
    }

    private void updateCrewData() {
        if(needsUpdate) {
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.CREW_DATA);
        }
        this.needsUpdate = false;
    }
    //endregion

    //region Model
    public static class CrewDataModel extends DataModels.DataModel {
        private static final String CREW_DATA_MODEL_VERSION = "0.2";

        public Map<UUID, Pair<String, ItemStack>> crewList = new HashMap<>();

        public CrewDataModel() {
            super(CREW_DATA_MODEL_VERSION, null);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "crewData", Pair.of(Text.literal("[crewData]"), TextHelper.literal(getCrewData()))
        );
    }
    //endregion
}
