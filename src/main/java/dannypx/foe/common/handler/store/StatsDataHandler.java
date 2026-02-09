package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.handler.logic.NotifierHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.PetNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class StatsDataHandler {
    private static StatsDataHandler INSTANCE = new StatsDataHandler();

    public static StatsDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new StatsDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
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
        this.needsUpdate = false;
        DataFileHandler.instance().saveToFile(DataModels.DataModelType.STATS_DATA);
    }
    //endregion

    //region Methods
    public void tick() {
        if(statsData.uuid == null && minecraftClient.player != null) {
            statsData.uuid = minecraftClient.player.getUuid();
        } else if(statsData.uuid != null && needsUpdate) {
            this.updateStatsData();
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
        if(isPet.v1()) setPet(isPet.v2());
        else setOtherItem(item, count);
    }

    private void setPet(PetNbtObject pet) {
        statsData.petTotal++;

        Pair<String, Integer> rarityDrystreak = this.updatePetData(statsData, PetNbtObject.RARITY, pet.getRarity(), 1);
        ConstantDataHandler.instance().updatePetData(PetNbtObject.RARITY, pet.getRarity(), pet.getRarityText());

        Pair<String, Integer> ratingDrystreak = this.updatePetData(statsData, PetNbtObject.RATING, pet.getRatingText().getString(), 1);
        ConstantDataHandler.instance().updatePetData(PetNbtObject.RATING, pet.getRatingText().getString(), pet.getRatingText());

        // Notify Pet
        NotifierHandler.instance().notifyPet(pet, rarityDrystreak, ratingDrystreak);
    }

    // Field, Old Drystreak
    private Pair<String, Integer> updatePetData(StatsDataModel statsData, String category, String field, int valueToAdd) {
        Map<String, Stat<Integer, Integer>> categoryMapData = statsData.petData.getOrDefault(category, new HashMap<>());
        Stat<Integer, Integer> fieldStat = categoryMapData.getOrDefault(field, Stat.of(0, statsData.fishTotal));

        Stat<Integer, Integer> newFieldStat = Stat.of(fieldStat.amount() + valueToAdd, statsData.fishTotal);
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
