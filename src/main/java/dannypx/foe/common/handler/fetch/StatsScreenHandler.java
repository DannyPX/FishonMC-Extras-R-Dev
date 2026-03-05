package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.logic.CodeExecuterHandler;
import dannypx.foe.common.handler.logic.NotifierHandler;
import dannypx.foe.common.handler.store.ConstantDataHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.handler.store.Stat;
import dannypx.foe.common.handler.store.StatsDataHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.tuple.Triplet;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsScreenHandler extends Handler {
    private static StatsScreenHandler INSTANCE = new StatsScreenHandler();

    public static StatsScreenHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new StatsScreenHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private boolean importStats = false;
    private List<Text> statsLore = new ArrayList<>();

    public void setImportStats(boolean importStats) {
        this.importStats = importStats;
    }

    public List<Text> getStatsLore() {
        return statsLore;
    }
    //endregion

    //region Methods
    public void checkStats(GenericContainerScreenHandler genericContainerScreenHandler) {
        if(this.importStats) {
            CodeExecuterHandler.runLater(2, () -> {
                Slot statSlot = genericContainerScreenHandler.getSlot(23);
                boolean completed = this.extractData(statSlot.getStack());

                if(completed) {
                    ProfileDataHandler.instance().updateImportStats(true);
                    StatsDataHandler.instance().updateImportStats(true);
                    NotifierHandler.instance().notifyImportStatsCompleted();
                }
            });

            this.importStats = false;
        }
    }

    private boolean extractData(ItemStack stack) {
        if(stack.get(DataComponentTypes.LORE) != null) {
            List<Text> lines = stack.get(DataComponentTypes.LORE).lines();
            this.statsLore = lines;
            if(lines.size() > 7) {
                int totalFish = this.extractTotal(lines.get(5));
                StatsDataHandler.instance().getStatsData().fishTotal = totalFish;

                // Rarity
                for (int i = 7; i < 12; i++) {
                    Text line = lines.get(i);
                    Triplet<Boolean, String, Integer> data =
                            this.extractStat(ConstantDataHandler.instance().getConstantData().fishData.getOrDefault(FishNbtObject.RARITY, new HashMap<>()), line);

                    if(data.value1()) {
                        StatsDataHandler.instance().getStatsData().fishData
                                .getOrDefault(FishNbtObject.RARITY, new HashMap<>())
                                .put(data.value2(), new Stat<>(data.value3(), totalFish));
                    }
                }

                // Fish Size
                for (int i = 13; i < 18; i++) {
                    Text line = lines.get(i);
                    Triplet<Boolean, String, Integer> data =
                            this.extractStat(ConstantDataHandler.instance().getConstantData().fishData.getOrDefault(FishNbtObject.FISH_SIZE, new HashMap<>()), line);

                    if(data.value1()) {
                        StatsDataHandler.instance().getStatsData().fishData
                                .getOrDefault(FishNbtObject.FISH_SIZE, new HashMap<>())
                                .put(data.value2(), new Stat<>(data.value3(), totalFish));
                    }
                }

                // Variant
                AtomicInteger normalCount = new AtomicInteger(totalFish);
                for (int i = 19; i < 23; i++) {
                    Text line = lines.get(i);
                    Triplet<Boolean, String, Integer> data =
                            this.extractStat(ConstantDataHandler.instance().getConstantData().fishData.getOrDefault(FishNbtObject.VARIANT, new HashMap<>()), line);

                    if(data.value1()) {
                        normalCount.set(normalCount.get() - data.value3());
                        StatsDataHandler.instance().getStatsData().fishData
                                .getOrDefault(FishNbtObject.VARIANT, new HashMap<>())
                                .put(data.value2(), new Stat<>(data.value3(), totalFish));
                    }
                }
                StatsDataHandler.instance().getStatsData().fishData
                        .getOrDefault(FishNbtObject.VARIANT, new HashMap<>())
                        .put("normal", new Stat<>(normalCount.get(), totalFish));
                return true;
            }
        }
        return false;
    }

    private Triplet<Boolean, String, Integer> extractStat(Map<String, Text> constants, Text line) {
        if(line.getSiblings().size() > 2) {
            String field = line.getSiblings().get(1).getString().trim();
            String key = ConstantDataHandler.keysFromField(constants, field).findFirst().orElse(null);
            if(key != null) {
                int amount = TextHelper.toIntFromString(line.getSiblings().get(2).getString());

                return Triplet.of(key, amount);
            }
        }
        return Triplet.ofFalse("", 0);
    }

    private int extractTotal(Text text) {
        return TextHelper.toIntFromString(text.getSiblings().get(2).getString());
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "statsLore", Pair.of(Text.literal("[statsLore]"), TextHelper.literal(getStatsLore()))
        );
    }
    //endregion
}
