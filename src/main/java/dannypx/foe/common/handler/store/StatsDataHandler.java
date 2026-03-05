package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.handler.logic.NotifierHandler;
import dannypx.foe.common.handler.logic.PlaceholderHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.PetNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.custom_text.CustomTextValue;
import dannypx.foe.common.type.custom_text.StringValue;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Pattern;

public class StatsDataHandler extends Handler {
    private static StatsDataHandler INSTANCE = new StatsDataHandler();

    public static StatsDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new StatsDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private StatsDataModel statsData = new StatsDataModel();
    private boolean needsUpdate = false;

    public StatsDataModel getStatsData() {
        return statsData;
    }

    public void setStatsData(StatsDataModel statsData) {
        this.statsData = statsData;
        this.updateStatsData();
    }

    private void updateStatsData() {
        if(needsUpdate) {
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.STATS_DATA);
        }
        this.needsUpdate = false;
    }

    public Pair<Boolean, CustomTextValue> getStatsData(String[] params) {
        if(params.length > 0) {
            Pattern categoryPattern = Pattern.compile("^(fish|pet|item)$");

            if(Objects.equals(params[0], "data")
                    && params.length >= 3 || params.length <= 5
                    && categoryPattern.matcher(params[1]).matches()
            ) {
                return switch (params[1]) {
                    case "fish" -> {
                        if(Objects.equals(params[2], "total")) yield PlaceholderHandler.getTextValue(new StringValue(String.valueOf(getStatsData().fishTotal)));
                        Map<String, Map<String, Stat<Integer, Integer>>> fishData = getStatsData().fishData;
                        yield getStatsData(fishData, params[2], params[3], params[4], getStatsData().fishTotal);
                    }
                    case "pet" -> {
                        if(Objects.equals(params[2], "total")) yield PlaceholderHandler.getTextValue(new StringValue(String.valueOf(getStatsData().petTotal)));
                        Map<String, Map<String, Stat<Integer, Integer>>> petData = getStatsData().petData;
                        yield getStatsData(petData, params[2], params[3], params[4], getStatsData().fishTotal);
                    }
                    case "item" -> {
                        Map<String, Stat<Integer, Integer>> itemData = getStatsData().itemData;
                        yield getStatsData(itemData, params[2], params[3], getStatsData().fishTotal);
                    }
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }

    private Pair<Boolean, CustomTextValue> getStatsData(Map<String, Map<String, Stat<Integer, Integer>>> category, String subCategory, String field, String type, int total) {
        if(Objects.equals(subCategory, "rating")) field = TextHelper.smallText(field);
        Map<String, Stat<Integer, Integer>> subCatMap = category.getOrDefault(subCategory, null);
        if(subCatMap != null) {
            return getStatsData(subCatMap, field, type, total);
        }
        return PlaceholderHandler.noResult();
    }

    private Pair<Boolean, CustomTextValue> getStatsData(Map<String, Stat<Integer, Integer>> subCategory, String field, String type, int total) {
        Stat<Integer, Integer> stat = subCategory.getOrDefault(field, null);
        if(stat != null) {
            return switch (type) {
                case "count" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(stat.amount())));
                case "dry_streak" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(total - stat.caughtOn())));
                default -> PlaceholderHandler.noResult();
            };
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        if(statsData.uuid == null && minecraftClient.player != null) {
            statsData.uuid = minecraftClient.player.getUuid();
        } else if(statsData.uuid != null && needsUpdate) {
            this.updateStatsData();
        } else if(!StatsDataModel.STATS_DATA_MODEL_VERSION.equals(statsData.version)) {
            statsData.version = StatsDataModel.STATS_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraftClient.player != null) this.setUUID(minecraftClient.player.getUuid());
    }

    private void setUUID(UUID uuid) {
        this.statsData.uuid = uuid;
    }

    public void setFish(FishNbtObject fish) {
        statsData.fishTotal++;

        Pair<String, Integer> rarityDrystreak = this.updateFishData(statsData, FishNbtObject.RARITY, fish.getRarity(), 1);
        ConstantDataHandler.instance().updateFishData(FishNbtObject.RARITY, fish.getRarity(), fish.getRarityText());

        Pair<String, Integer> variantDrystreak = this.updateFishData(statsData, FishNbtObject.VARIANT, fish.getVariant(), 1);
        ConstantDataHandler.instance().updateFishData(FishNbtObject.VARIANT, fish.getVariant(), fish.getVariantText());

        Pair<String, Integer> sizeDryStreak = this.updateFishData(statsData, FishNbtObject.FISH_SIZE, fish.getFishSize(), 1);
        ConstantDataHandler.instance().updateFishData(FishNbtObject.FISH_SIZE, fish.getFishSize(), fish.getFishSizeText());

        // Notify Fish
        NotifierHandler.instance().notifyFish(fish, rarityDrystreak, variantDrystreak, sizeDryStreak);
    }



    // Field, Old Drystreak
    private Pair<String, Integer> updateFishData(StatsDataModel statsData, String category, String field, int valueToAdd) {
        Map<String, Stat<Integer, Integer>> categoryMapData = statsData.fishData.getOrDefault(category, new HashMap<>());
        Stat<Integer, Integer> fieldStat = categoryMapData.getOrDefault(field, Stat.of(0, statsData.fishTotal));

        Stat<Integer, Integer> newFieldStat = Stat.of(fieldStat.amount() + valueToAdd, statsData.fishTotal);
        categoryMapData.put(field, newFieldStat);
        statsData.fishData.put(category, categoryMapData);
        this.needsUpdate = true;

        return Pair.of(field, statsData.fishTotal - fieldStat.caughtOn());
    }

    public void setItem(NbtObject item, int count) {
        Pair<Boolean, PetNbtObject> isPet = ValidateItem.isPet(item);
        if(isPet.value1()) setPet(isPet.value2());
        else setOtherItem(item, count);
    }

    private void setPet(PetNbtObject pet) {
        statsData.petTotal++;

        Pair<String, Integer> rarityDrystreak = this.updatePetData(statsData, PetNbtObject.RARITY, pet.getRarity());
        ConstantDataHandler.instance().updatePetData(PetNbtObject.RARITY, pet.getRarity(), pet.getRarityText());

        Pair<String, Integer> ratingDrystreak = this.updatePetData(statsData, PetNbtObject.RATING, pet.getRatingText().getString());
        ConstantDataHandler.instance().updatePetData(PetNbtObject.RATING, pet.getRatingText().getString(), pet.getRatingText());

        // Notify Pet
        NotifierHandler.instance().notifyPet(pet, rarityDrystreak, ratingDrystreak);
    }

    // Field, Old Drystreak
    private Pair<String, Integer> updatePetData(StatsDataModel statsData, String category, String field) {
        Map<String, Stat<Integer, Integer>> categoryMapData = statsData.petData.getOrDefault(category, new HashMap<>());
        Stat<Integer, Integer> fieldStat = categoryMapData.getOrDefault(field, Stat.of(0, statsData.fishTotal));

        Stat<Integer, Integer> newFieldStat = Stat.of(fieldStat.amount() + 1, statsData.fishTotal);
        categoryMapData.put(field, newFieldStat);
        statsData.petData.put(category, categoryMapData);
        this.needsUpdate = true;

        return Pair.of(field, statsData.fishTotal - fieldStat.caughtOn());
    }

    private void setOtherItem(NbtObject item, int count) {
        Pair<String, Integer> itemDrystreak = this.updateOtherItemData(statsData, item.getType(), count);
        ConstantDataHandler.instance().updateItemData(item.getType(), item.getItemStack());

        // Notify Item
        NotifierHandler.instance().notifyItem(item, count, itemDrystreak);
    }

    private Pair<String, Integer> updateOtherItemData(StatsDataModel statsData, String item, int valueToAdd) {
        Stat<Integer, Integer> itemStat = statsData.itemData.getOrDefault(item, Stat.of(0, statsData.fishTotal));

        Stat<Integer, Integer> newItemStat = Stat.of(itemStat.amount() + valueToAdd, statsData.fishTotal);
        statsData.itemData.put(item, newItemStat);
        this.needsUpdate = true;

        return Pair.of(item, statsData.fishTotal - itemStat.caughtOn());
    }

    public void updateImportStats(boolean updatedStats) {
        if(updatedStats) {
            this.needsUpdate = true;
        }
    }



    public void resetStats() {
        this.reset();
        this.needsUpdate = true;
    }

    private void reset() {
        statsData.fishData = new HashMap<>();
        statsData.petData = new HashMap<>();
        statsData.itemData = new HashMap<>();
        statsData.fishTotal = 0;
        statsData.petTotal = 0;
        this.needsUpdate = true;
    }
    //endregion

    //region Model
    public static class StatsDataModel extends DataModels.DataModel {
        public static final String STATS_DATA_MODEL_VERSION = "0.2";

        /**
         * Fish
         * - Rarities
         * - Size
         * - Variants
         * Pair: Amount, Drystreak
         */
        public Map<String, Map<String, Stat<Integer, Integer>>> fishData = new HashMap<>();
        public int fishTotal = 0;

        /**
         * Pet
         * - Rarities
         * - Rating
         * Pair: Amount, Drystreak
         */
        public Map<String, Map<String, Stat<Integer, Integer>>> petData = new HashMap<>();
        public int petTotal = 0;

        /**
         * Other items
         */
        public Map<String, Stat<Integer, Integer>> itemData = new HashMap<>();

        public StatsDataModel() {
            super(STATS_DATA_MODEL_VERSION, null);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "statsData", Pair.of(Text.literal("[statsData]"), TextHelper.literal(getStatsData()))
        );
    }
    //endregion
}
