package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.PetNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private StatsDataModel statsDataOld = new StatsDataModel();

    public StatsDataModel getStatsData() {
        return statsData;
    }

    public void setStatsData(StatsDataModel statsData) {
        this.statsData = statsData;
        this.updateStatsData(statsData);
    }

    private void updateStatsData(StatsDataModel statsData) {
        this.statsDataOld = statsData.copy();
        DataFileHandler.instance().saveToFile(DataModels.DataModelType.STATS_DATA);
    }

        //endregion

    //region Methods
    public void tick() {
        if(!statsDataOld.equals(statsData)) {
            this.updateStatsData(statsData);
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

        Data<String, Integer> rarityDrystreak = this.updateFishData(FishNbtObject.RARITY, fish.getRarity(), 1);
        Data<String, Integer> variantDrystreak = this.updateFishData(FishNbtObject.VARIANT, fish.getVariant(), 1);
        Data<String, Integer> sizeDryStreak = this.updateFishData(FishNbtObject.SIZE, fish.getSize(), 1);
        //TODO Notify Fish
    }

    // Field, Old Drystreak
    private Data<String, Integer> updateFishData(String category, String field, int valueToAdd) {
        Map<String, Data<Integer, Integer>> categoryMapData = statsData.fishData.getOrDefault(category, new HashMap<>());
        Data<Integer, Integer> fieldData = categoryMapData.getOrDefault(field, Data.of(0, statsData.fishTotal));

        Data<Integer, Integer> newFieldData = Data.of(fieldData.amount() + valueToAdd, statsData.fishTotal);
        categoryMapData.put(field, newFieldData);
        statsData.fishData.put(category, categoryMapData);

        return Data.of(field, statsData.fishTotal - fieldData.caughtOn());
    }

    public void setItem(NbtObject item, int count) {
        Pair<Boolean, @Nullable PetNbtObject> isPet = ValidateItem.isPet(item);
        if(isPet.v1()) setPet(isPet.v2());
        else setOtherItem(item, count);
    }

    private void setPet(PetNbtObject pet) {
        statsData.petTotal++;

        Data<String, Integer> rarityDrystreak = this.updatePetData(NbtObject.RARITY, pet.getRarity(), 1);
        //TODO Rating
        //TODO Notify Pet
    }

    // Field, Old Drystreak
    private Data<String, Integer> updatePetData(String category, String field, int valueToAdd) {
        Map<String, Data<Integer, Integer>> categoryMapData = statsData.petData.getOrDefault(category, new HashMap<>());
        Data<Integer, Integer> fieldData = categoryMapData.getOrDefault(field, Data.of(0, statsData.fishTotal));

        Data<Integer, Integer> newFieldData = Data.of(fieldData.amount() + valueToAdd, statsData.fishTotal);
        categoryMapData.put(field, newFieldData);
        statsData.petData.put(category, categoryMapData);

        return Data.of(field, statsData.fishTotal - fieldData.caughtOn());
    }

    private void setOtherItem(NbtObject item, int count) {
        Data<String, Integer> itemDrystreak = this.updateOtherItemData(item.getType(), count);
        //TODO Notify Item
    }

    private Data<String, Integer> updateOtherItemData(String item, int valueToAdd) {
        Data<Integer, Integer> itemData = statsData.itemData.getOrDefault(item, Data.of(0, statsData.fishTotal));

        Data<Integer, Integer> newItemData = Data.of(itemData.amount() + valueToAdd, statsData.fishTotal);
        statsData.itemData.put(item, newItemData);

        return Data.of(item, statsData.fishTotal - itemData.caughtOn());
    }
    //endregion

    //region Model
    public static class StatsDataModel extends DataModels.DataModel {
        public static final String STATS_DATA_MODEL_VERSION = "0";

        /**
         * Fish
         * - Rarities
         * - Size
         * - Variants
         * Pair: Amount, Drystreak
         */
        public Map<String, Map<String, Data<Integer, Integer>>> fishData = new HashMap<>();
        public int fishTotal = 0;

        /**
         * Pet
         * - Rarities
         * - Rating
         * Pair: Amount, Drystreak
         */
        public Map<String, Map<String, Data<Integer, Integer>>> petData = new HashMap<>();
        public int petTotal = 0;

        /**
         * Other items
         */
        public Map<String, Data<Integer, Integer>> itemData = new HashMap<>();

        public StatsDataModel() {
            super(STATS_DATA_MODEL_VERSION, null);
        }

        public StatsDataModel(StatsDataModel oldData) {
            super(oldData.version, oldData.uuid);
            this.fishData = new HashMap<>(oldData.fishData);
            this.fishTotal = oldData.fishTotal;
            this.petData = new HashMap<>(oldData.petData);
            this.petTotal = oldData.petTotal;
            this.itemData = new HashMap<>(oldData.itemData);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;

            return obj instanceof StatsDataModel oldStatsData
                    && this.uuid.equals(oldStatsData.uuid)
                    && this.fishData.equals(oldStatsData.fishData)
                    && this.fishTotal == oldStatsData.fishTotal
                    && this.petData.equals(oldStatsData.petData)
                    && this.petTotal == oldStatsData.petTotal
                    && this.itemData.equals(oldStatsData.itemData);
        }

        public StatsDataModel copy() {
            return new StatsDataModel(this);
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
