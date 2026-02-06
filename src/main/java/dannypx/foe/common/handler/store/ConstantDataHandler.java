package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class ConstantDataHandler {
    private static ConstantDataHandler INSTANCE = new ConstantDataHandler();

    public static ConstantDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ConstantDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private ConstantDataModel constantData = new ConstantDataModel();
    private boolean needsUpdate = false;

    public ConstantDataModel getConstantData() {
        return constantData;
    }

    public void setConstantData(ConstantDataModel constantData) {
        this.constantData = constantData;
        this.updateConstantData();
    }

    private void updateConstantData() {
        this.needsUpdate = false;
        DataFileHandler.instance().saveToFile(DataModels.DataModelType.CONSTANT_DATA);
    }
    //endregion

    //region Methods
    public void tick() {
        if(constantData.uuid == null && minecraftClient.player != null) {
            constantData.uuid = minecraftClient.player.getUuid();
        } else if(constantData.uuid != null && this.needsUpdate) {
            this.updateConstantData();
        }
    }

    public void init() {
        if(minecraftClient.player != null) this.setUUID(minecraftClient.player.getUuid());
    }

    private void setUUID(UUID uuid) {
        this.constantData.uuid = uuid;
    }

    public void updateFishData(String category, String field, Text value) {
        Map<String, Text> categoryData = this.constantData.fishData.getOrDefault(category, new HashMap<>());
        if(!categoryData.containsKey(field)) {
            categoryData.put(field, value);
            this.constantData.fishData.put(category, categoryData);
            this.needsUpdate = true;
        }
    }

    public void updatePetData(String category, String field, Text value) {
        Map<String, Text> categoryData = this.constantData.petData.getOrDefault(category, new HashMap<>());
        if(!categoryData.containsKey(field)) {
            categoryData.put(field, value);
            this.constantData.petData.put(category, categoryData);
            this.needsUpdate = true;
        }
    }

    public void updateItemData(String category, ItemStack itemStack) {
        List<ItemStack> categoryData = this.constantData.itemData.getOrDefault(category, new ArrayList<>());
        boolean containsItem = categoryData.stream().anyMatch(item -> item.getName().getString().equals(itemStack.getName().getString()));
        if(!containsItem) {
            categoryData.add(itemStack);
            this.constantData.itemData.put(category, categoryData);
            this.needsUpdate = true;
        }
    }
    //endregion

    //region Model
    public static class ConstantDataModel extends DataModels.DataModel {
        private static final String CONSTANT_DATA_MODEL_VERSION = "0.1";

        /**
         * Fish
         * - Rarities
         * - Size
         * - Variants
         */
        private Map<String, Map<String, Text>> fishData = new HashMap<>(
                Map.of(
                        "size", Map.of(
                                "baby", Text.literal("ʙᴀʙʏ").withColor(0x468CE7),
                                "juvenile", Text.literal("ᴊᴜᴠᴇɴɪʟᴇ").withColor(0x22EA08),
                                "adult", Text.literal("ᴀᴅᴜʟᴛ").withColor(0x1C7DA0),
                                "large", Text.literal("ʟᴀʀɢᴇ").withColor(0xFF9000),
                                "gigantic", Text.literal("ɢɪɢᴀɴᴛɪᴄ").withColor(0xAF3333)
                        ),
                        "variant", Map.of(
                                "normal", Text.literal("").formatted(Formatting.WHITE),
                                "albino", Text.literal("\uF041").formatted(Formatting.WHITE),
                                "melanistic", Text.literal("\uF042").formatted(Formatting.WHITE),
                                "trophy", Text.literal("\uF043").formatted(Formatting.WHITE),
                                "fabled", Text.literal("\uF044").formatted(Formatting.WHITE)
                        ),
                        "rarity", Map.of(
                                "common", Text.literal("\uF033").formatted(Formatting.WHITE),
                                "rare", Text.literal("\uF034").formatted(Formatting.WHITE),
                                "epic", Text.literal("\uF035").formatted(Formatting.WHITE),
                                "legendary", Text.literal("\uF036").formatted(Formatting.WHITE),
                                "mythical", Text.literal("\uF037").formatted(Formatting.WHITE)
                        )
                )
        );

        /**
         * Pet
         * - Rarities
         * - Rating
         */
        private Map<String, Map<String, Text>> petData = new HashMap<>();

        private Map<String, List<ItemStack>> itemData = new HashMap<>();

        public ConstantDataModel() {
            super(CONSTANT_DATA_MODEL_VERSION, null);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "constantData", Pair.of(Text.literal("[constantData]"), TextHelper.literal(getConstantData()))
        );
    }
    //endregion
}
