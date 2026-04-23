package dannypx.foe.handler.store;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.io.DataFileHandler;
import dannypx.foe.handler.io.DataModels;
import dannypx.foe.handler.logic.NotifierHandler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.helper.ComponentHelper;
import dannypx.foe.item.FishTagObject;
import dannypx.foe.item.TagObject;
import dannypx.foe.item.PetTagObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import dannypx.foe.type.tuple.Triplet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

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

    public Pair<Boolean, PlaceholderValue> getStatsData(String[] params) {
        if(params.length > 0) {
            Pattern categoryPattern = Pattern.compile("^(fish|pet|item)$");

            if(Objects.equals(params[0], "data")
                    && params.length >= 3
                    && categoryPattern.matcher(params[1]).matches()
            ) {
                return switch (params[1]) {
                    case "fish" -> {
                        if(Objects.equals(params[2], "total")) yield PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(getStatsData().fishTotal)));
                        if(params.length >= 5) {
                            Map<String, Map<String, Stat<Integer, Integer>>> fishData = getStatsData().fishData;
                            yield getStatsData(fishData, params[2], params[3], params[4], getStatsData().fishTotal);
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "pet" -> {
                        if(Objects.equals(params[2], "total")) yield PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(getStatsData().petTotal)));
                        if(Objects.equals(params[2], "dry_streak")) yield PlaceholderHandler.getPlaceholderValue(new StringValue(
                                String.valueOf(getStatsData().fishTotal - getStatsData().petData.getOrDefault(PetTagObject.RARITY, new HashMap<>()).values().stream().mapToInt(stat -> stat.caughtOn).max().orElse(0))
                        ));
                        if(params.length >= 5) {
                            Map<String, Map<String, Stat<Integer, Integer>>> petData = getStatsData().petData;
                            yield getStatsData(petData, params[2], params[3], params[4], getStatsData().fishTotal);
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "item" -> {
                        if(params.length >= 4) {
                            Map<String, Stat<Integer, Integer>> itemData = getStatsData().itemData;
                            yield getStatsData(itemData, params[2], params[3], getStatsData().fishTotal);
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }

    private Pair<Boolean, PlaceholderValue> getStatsData(Map<String, Map<String, Stat<Integer, Integer>>> category, String subCategory, String field, String type, int total) {
        if(Objects.equals(subCategory, "rating")) field = ComponentHelper.smallCaps(field);
        Map<String, Stat<Integer, Integer>> subCatMap = category.getOrDefault(subCategory, null);
        if(subCatMap != null) {
            return getStatsData(subCatMap, field, type, total);
        }
        return PlaceholderHandler.noResult();
    }

    private Pair<Boolean, PlaceholderValue> getStatsData(Map<String, Stat<Integer, Integer>> subCategory, String field, String type, int total) {
        Stat<Integer, Integer> stat = subCategory.getOrDefault(field, null);
        if(stat != null) {
            return switch (type) {
                case "count" -> PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(stat.amount())));
                case "dry_streak" -> PlaceholderHandler.getPlaceholderValue(new StringValue(String.valueOf(total - stat.caughtOn())));
                default -> PlaceholderHandler.noResult();
            };
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        if(statsData.uuid == null && minecraft.player != null) {
            statsData.uuid = minecraft.player.getUUID();
        } else if(statsData.uuid != null && needsUpdate) {
            this.updateStatsData();
        } else if(!StatsDataModel.STATS_DATA_MODEL_VERSION.equals(statsData.version)) {
            statsData.version = StatsDataModel.STATS_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraft.player != null) this.setUUID(minecraft.player.getUUID());
    }

    private void setUUID(UUID uuid) {
        this.statsData.uuid = uuid;
    }

    public Triplet<Pair<String, Integer>, Pair<String, Integer>, Pair<String, Integer>> setFish(FishTagObject fish) {
        statsData.fishTotal++;

        Pair<String, Integer> rarityDrystreak = this.updateFishData(statsData, FishTagObject.RARITY, fish.getRarity(), 1);
        ConstantDataHandler.instance().updateFishData(FishTagObject.RARITY, fish.getRarity(), fish.getRarityComponent());

        Pair<String, Integer> variantDrystreak = this.updateFishData(statsData, FishTagObject.VARIANT, fish.getVariant(), 1);
        ConstantDataHandler.instance().updateFishData(FishTagObject.VARIANT, fish.getVariant(), fish.getVariantComponent());

        Pair<String, Integer> sizeDryStreak = this.updateFishData(statsData, FishTagObject.FISH_SIZE, fish.getFishSize(), 1);
        ConstantDataHandler.instance().updateFishData(FishTagObject.FISH_SIZE, fish.getFishSize(), fish.getFishSizeComponent());

        return Triplet.of(rarityDrystreak, variantDrystreak, sizeDryStreak);
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

    public void setItem(TagObject item, int count) {
        Pair<Boolean, PetTagObject> isPet = ValidateItem.isPet(item);
        if(isPet.value1()) setPet(isPet.value2());
        else setOtherItem(item, count);
    }

    private void setPet(PetTagObject pet) {
        statsData.petTotal++;

        Pair<String, Integer> rarityDrystreak = this.updatePetData(statsData, PetTagObject.RARITY, pet.getRarity());
        ConstantDataHandler.instance().updatePetData(PetTagObject.RARITY, pet.getRarity(), pet.getRarityComponent());

        Pair<String, Integer> ratingDrystreak = this.updatePetData(statsData, PetTagObject.RATING, pet.getRatingComponent().getString());
        ConstantDataHandler.instance().updatePetData(PetTagObject.RATING, pet.getRatingComponent().getString(), pet.getRatingComponent());

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

    private void setOtherItem(TagObject item, int count) {
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

    public void updateImportStats(boolean updatedStats, @NotNull Map<String, Map<String, Stat<Integer, Integer>>> newData) {
        if(updatedStats) {
            this.statsData.fishData = newData;
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

    public record Stat<Amount, CaughtOn>(Amount amount, CaughtOn caughtOn) {
        public static <Amount, CaughtOn> Stat<Amount, CaughtOn> of(Amount amount, CaughtOn caughtOn) {
            return new Stat<>(amount, caughtOn);
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "statsData", Pair.of(Component.literal("[statsData]"), ComponentHelper.literal(getStatsData()))
        );
    }
    //endregion
}
