package dannypx.foe.handler.store;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.io.DataFileHandler;
import dannypx.foe.handler.io.DataModels;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.TextValue;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ConstantDataHandler extends Handler {
    private static ConstantDataHandler INSTANCE = new ConstantDataHandler();

    public static ConstantDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ConstantDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
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
        if(needsUpdate) {
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.CONSTANT_DATA);
        }
        this.needsUpdate = false;
    }

    public Pair<Boolean, CustomTextValue> getConstantData(String[] params) {
        if(params.length > 0) {
            Pattern categoryPattern = Pattern.compile("^(fish|pet)$");

            if(Objects.equals(params[0], "data")
                    && categoryPattern.matcher(params[1]).matches()
                    && params.length == 4
            ) {
                return switch (params[1]) {
                    case "fish" -> {
                        Map<String, Text> subCat = getConstantData().fishData.getOrDefault(params[2], null);
                        if(subCat != null) {
                            Text field = subCat.getOrDefault(params[3], Text.empty());
                            if(!Objects.equals(field, Text.empty())) yield PlaceholderHandler.getTextValue(new TextValue(field));
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "pet" -> {
                        Map<String, Text> subCat = getConstantData().petData.getOrDefault(params[2], null);
                        if(subCat != null) {
                            String param = params[3];

                            if(Objects.equals(params[2], "rating")) {
                                param = TextHelper.smallText(params[3]);
                            }

                            Text field = subCat.getOrDefault(param, Text.empty());
                            if(!Objects.equals(field, Text.empty())) yield PlaceholderHandler.getTextValue(new TextValue(field));
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        if(constantData.uuid == null && minecraftClient.player != null) {
            constantData.uuid = minecraftClient.player.getUuid();
        } else if(constantData.uuid != null && this.needsUpdate) {
            this.updateConstantData();
        } else if(!ConstantDataModel.CONSTANT_DATA_MODEL_VERSION.equals(constantData.version)) {
            constantData.version = ConstantDataModel.CONSTANT_DATA_MODEL_VERSION;
            needsUpdate = true;
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

    public Text getConstantFishText(String field) {
        AtomicReference<Text> fieldText = new AtomicReference<>(Text.empty());
        this.getConstantData().fishData.forEach((key, mapFields) -> {
            Text result = mapFields.getOrDefault(field, Text.empty());

            if(!Objects.equals(result, Text.empty())) {
                fieldText.set(result);
            }
        });

        return fieldText.get();
    }

    public static Stream<String> keysFromField(Map<String, Text> map, String value) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue().getString()))
                .map(Map.Entry::getKey);
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
        public Map<String, Map<String, Text>> fishData = new HashMap<>(
                Map.of(
                        "size", Map.of(
                                "baby", Text.literal("ʙᴀʙʏ").withColor(0x468CE7),
                                "juvenile", Text.literal("ᴊᴜᴠᴇɴɪʟᴇ").withColor(0x22EA08),
                                "adult", Text.literal("ᴀᴅᴜʟᴛ").withColor(0x1C7DA0),
                                "large", Text.literal("ʟᴀʀɢᴇ").withColor(0xFF9000),
                                "gigantic", Text.literal("ɢɪɢᴀɴᴛɪᴄ").withColor(0xAF3333)
                        ),
                        "variant", Map.of(
                                "normal", Text.literal("\uF040").formatted(Formatting.WHITE),
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
        public Map<String, Map<String, Text>> petData = new HashMap<>();

        public Map<String, List<ItemStack>> itemData = new HashMap<>();

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
