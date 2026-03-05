package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Alignment;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.tuple.Triplet;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

public class CustomHudDataHandler extends Handler {
    private static CustomHudDataHandler INSTANCE = new CustomHudDataHandler();

    public static CustomHudDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomHudDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private CustomHudDataModel customHudData = new CustomHudDataModel();

    private boolean needsUpdate = false;
    public boolean needsRenderUpdate = false;

    public CustomHudDataModel getCustomHudData() {
        return customHudData;
    }

    public void setCustomHudData(CustomHudDataModel customHudData) {
        this.customHudData = customHudData;
        this.needsRenderUpdate = true;
        this.updateCustomHudData();
    }

    private void updateCustomHudData() {
        if(needsUpdate) {
            needsRenderUpdate = true;
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.CUSTOM_HUD_DATA);
        }
        this.needsUpdate = false;
    }
    //endregion

    //region Methods
    public void tick() {
        if(customHudData.uuid == null && minecraftClient.player != null) {
            customHudData.uuid = minecraftClient.player.getUuid();
        } else if(customHudData.uuid != null && this.needsUpdate) {
            this.updateCustomHudData();
        } else if(!CustomHudDataModel.CUSTOM_HUD_DATA_MODEL_VERSION.equals(customHudData.version)) {
            customHudData.version = CustomHudDataModel.CUSTOM_HUD_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraftClient.player != null) {
            this.setUUID(minecraftClient.player.getUuid());
            needsRenderUpdate = true;
        }
    }

    private void setUUID(UUID uuid) {
        this.customHudData.uuid = uuid;
    }

    public void createNewCustomHud(String id) {
        customHudData.customHudRawDataList.put(id, new CustomHud());
        needsUpdate = true;
    }

    public void createNewCustomHud(String id, CustomHud customHud) {
        customHudData.customHudRawDataList.put(id, customHud);
        needsUpdate = true;
    }

    public CustomHud deleteCustomHud(String id) {
        needsUpdate = true;
        return customHudData.customHudRawDataList.remove(id);
    }

    public void updateHud(String currentSelectedHud, String newName, float scale, boolean showElement, List<Triplet<String, Boolean, Boolean>> list) {
        CustomHud newHud = customHudData.customHudRawDataList.get(currentSelectedHud);

        if(!Objects.equals(currentSelectedHud, newName)) {
            newHud = deleteCustomHud(currentSelectedHud);
            currentSelectedHud = newName;
        }

        newHud.textLines = list;
        newHud.scale = scale;
        newHud.showElement = showElement;

        customHudData.customHudRawDataList.put(currentSelectedHud, newHud);
        needsUpdate = true;
    }

    public void updateHud(String currentSelectedHud, int xPercent, int yPercent, Alignment alignment) {
        CustomHud newHud = customHudData.customHudRawDataList.get(currentSelectedHud);

        newHud.xPos = xPercent;
        newHud.yPos = yPercent;
        newHud.alignment = alignment;

        customHudData.customHudRawDataList.put(currentSelectedHud, newHud);
        needsUpdate = true;
    }
    //endregion

    //region Model
    public static class CustomHudDataModel extends DataModels.DataModel {
        private static final String CUSTOM_HUD_DATA_MODEL_VERSION = "0.2";

        // ID of the HUD
        public Map<String, CustomHud> customHudRawDataList = new HashMap<>(
                Map.of(
                        "Quest Hud",
                        new CustomHud(new ArrayList<>(Arrays.asList(
                                Triplet.of("&7&l- &fQuests &7-", true, true),
                                Triplet.of("", false, false),
                                Triplet.of("%quest_data.data.0.goal% &e%quest_data.data.0.current%&7/&f%quest_data.data.0.max%", false, true),
                                Triplet.of("%quest_data.data.1.goal% &e%quest_data.data.1.current%&7/&f%quest_data.data.1.max%", false, true),
                                Triplet.of("%quest_data.data.2.goal% &e%quest_data.data.2.current%&7/&f%quest_data.data.2.max%", false, true),
                                Triplet.of("%quest_data.data.3.goal% &e%quest_data.data.3.current%&7/&f%quest_data.data.3.max%", false, true),
                                Triplet.of("%quest_data.data.4.goal% &e%quest_data.data.4.current%&7/&f%quest_data.data.4.max%", false, true),
                                Triplet.of("%quest_data.data.5.goal% &e%quest_data.data.5.current%&7/&f%quest_data.data.5.max%", false, true),
                                Triplet.of("%quest_data.data.6.goal% &e%quest_data.data.6.current%&7/&f%quest_data.data.6.max%", false, true),
                                Triplet.of("%quest_data.data.7.goal% &e%quest_data.data.7.current%&7/&f%quest_data.data.7.max%", false, true)
                        )),
                                Alignment.TOP_RIGHT,
                                1,
                                15,
                                1.0f,
                                true
                        )
                )
        );

        public CustomHudDataModel() {
            super(CUSTOM_HUD_DATA_MODEL_VERSION, null);
        }
    }
    //endregion

    //region Hud Object
    public static class CustomHud {
        // String, isCentre, isSmall
        public List<Triplet<String, Boolean, Boolean>> textLines;
        public Alignment alignment;
        public int xPos;
        public int yPos;
        public float scale;
        public boolean showElement;

        public CustomHud(
                List<Triplet<String, Boolean, Boolean>> textLines,
                Alignment alignment,
                int xPos,
                int yPos,
                float scale,
                boolean showElement
        ) {
            this.textLines = textLines;
            this.alignment = alignment;
            this.xPos = xPos;
            this.yPos = yPos;
            this.scale = scale;
            this.showElement = showElement;
        }

        public CustomHud() {
            this.textLines = new ArrayList<>(List.of(
                    Triplet.of("Example text", false, false)
            ));
            this.alignment = Alignment.TOP_LEFT;
            this.xPos = 30;
            this.yPos = 30;
            this.scale = 1.0f;
            this.showElement = true;
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "customHudData", Pair.of(Text.literal("[customHudData]"), TextHelper.literal(getCustomHudData()))
        );
    }
    //endregion
}
