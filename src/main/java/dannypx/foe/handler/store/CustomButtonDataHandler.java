package dannypx.foe.handler.store;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.io.DataFileHandler;
import dannypx.foe.handler.io.DataModels;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

public class CustomButtonDataHandler extends Handler {
    private static CustomButtonDataHandler INSTANCE = new CustomButtonDataHandler();

    public static CustomButtonDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomButtonDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private CustomButtonDataModel customButtonData = new CustomButtonDataModel();
    private boolean needsUpdate = false;

    public CustomButtonDataModel getCustomButtonData() {
        return customButtonData;
    }

    public void setCustomButtonData(CustomButtonDataModel customButtonData) {
        this.customButtonData = customButtonData;
        this.updateCustomButtonData();
    }

    private void updateCustomButtonData() {
        if(needsUpdate) {
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.CUSTOM_BUTTON_DATA);
        }
        this.needsUpdate = false;
    }
    //endregion

    //region Methods
    public void tick() {
        if(customButtonData.uuid == null && minecraftClient.player != null) {
            customButtonData.uuid = minecraftClient.player.getUuid();
        } else if(customButtonData.uuid != null && this.needsUpdate) {
            this.updateCustomButtonData();
        } else if(!CustomButtonDataModel.CUSTOM_BUTTON_DATA_MODEL_VERSION.equals(customButtonData.version)) {
            customButtonData.version = CustomButtonDataModel.CUSTOM_BUTTON_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraftClient.player != null) this.setUUID(minecraftClient.player.getUuid());
    }

    public void init(String screenID) {
        if(!this.customButtonData.buttonList.containsKey(screenID)) {
            this.customButtonData.buttonList.put(screenID, Pair.of(new ArrayList<>(), false));
            needsUpdate = true;
        }
    }

    private void setUUID(UUID uuid) {
        this.customButtonData.uuid = uuid;
    }

    public void createNewButton(String namespace, String id) {
        Pair<List<CustomButton>, Boolean> buttonList = customButtonData.buttonList.getOrDefault(namespace, Pair.of(new ArrayList<>(), false));

        buttonList.value1().add(new CustomButton(id));

        customButtonData.buttonList.put(namespace, buttonList);

        needsUpdate = true;
    }

    public void createNewButton(String namespace, String id, int pos) {
        Pair<List<CustomButton>, Boolean> buttonList = customButtonData.buttonList.getOrDefault(namespace, Pair.of(new ArrayList<>(), false));

        buttonList.value1().add(pos, new CustomButton(id));

        customButtonData.buttonList.put(namespace, buttonList);

        needsUpdate = true;
    }

    public void createNewButton(String namespace, CustomButton newButton) {
        customButtonData.buttonList.getOrDefault(namespace, Pair.of(new ArrayList<>(), false)).value1().add(newButton);

        needsUpdate = true;
    }

    public void deleteButton(String namespace, String id) {
        Pair<List<CustomButton>, Boolean> buttonList = customButtonData.buttonList.getOrDefault(namespace, Pair.of(new ArrayList<>(), false));

        buttonList.value1().removeIf(button -> Objects.equals(button.name, id));

        customButtonData.buttonList.put(namespace, buttonList);

        needsUpdate = true;
    }

    public void updateButton(String namespace, CustomButton customButton, String newName, String desc, String action, String icon, boolean showButton) {
        Pair<List<CustomButton>, Boolean> buttonList = customButtonData.buttonList.getOrDefault(namespace, Pair.of(new ArrayList<>(), false));
        int index = buttonList.value1().indexOf(customButton);
        CustomButton updatedButton = buttonList.value1().get(index);

        updatedButton.name = newName;
        updatedButton.description = desc;
        updatedButton.action = action;
        updatedButton.icon = icon;
        updatedButton.showButton = showButton;

        buttonList.value1().set(index, updatedButton);

        customButtonData.buttonList.put(namespace, buttonList);

        needsUpdate = true;
    }

    public void updateButton(String namespace, boolean showButton) {
        Pair<List<CustomButton>, Boolean> buttonList = customButtonData.buttonList.getOrDefault(namespace, Pair.of(new ArrayList<>(), false));
        buttonList = Pair.of(buttonList.value1(), showButton);

        customButtonData.buttonList.put(namespace, buttonList);

        needsUpdate = true;
    }
    //endregion

    //region Model
    public static class CustomButtonDataModel extends DataModels.DataModel {
        private static final String CUSTOM_BUTTON_DATA_MODEL_VERSION = "0.2";

        //Screen <Button List, isMenuOpen>
        public Map<String, Pair<List<CustomButton>, Boolean>> buttonList = new HashMap<>();

        public CustomButtonDataModel() {
            super(CUSTOM_BUTTON_DATA_MODEL_VERSION, null);
        }
    }
    //endregion

    //region Button Object
    public static class CustomButton {
        public String name;
        public String description;
        public String action;
        public String icon;
        public boolean showButton;

        public CustomButton(String name) {
            this.name = name;
            this.description = "Example description";
            this.action = "/";
            this.icon = "E";
            this.showButton = true;
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "customButtonData", Pair.of(Text.literal("[customButtonData]"), TextHelper.literal(getCustomButtonData()))
        );
    }
    //endregion
}
