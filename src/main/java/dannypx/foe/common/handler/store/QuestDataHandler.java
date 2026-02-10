package dannypx.foe.common.handler.store;

import dannypx.foe.common.handler.fetch.BossBarHandler;
import dannypx.foe.common.handler.io.DataFileHandler;
import dannypx.foe.common.handler.io.DataModels;
import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.handler.fetch.QuestScreenHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

public class QuestDataHandler {
    private static QuestDataHandler INSTANCE = new QuestDataHandler();

    public static QuestDataHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new QuestDataHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private QuestDataModel questData = new QuestDataModel();
    private boolean needsUpdate = false;

    public QuestDataModel getQuestData() {
        return questData;
    }

    public void setQuestData(QuestDataModel questData) {
        this.questData = questData;
        this.updateQuestData();
    }

    private void updateQuestData() {
        if(needsUpdate) {
            DataFileHandler.instance().saveToFile(DataModels.DataModelType.QUEST_DATA);
        }
        this.needsUpdate = false;
    }
    //endregion

    //region Methods
    public void tick() {
        if(questData.uuid == null && minecraftClient.player != null) {
            questData.uuid = minecraftClient.player.getUuid();
        } else if(questData.uuid != null && this.needsUpdate) {
            this.updateQuestData();
        } else if(!QuestDataModel.QUEST_DATA_MODEL_VERSION.equals(questData.version)) {
            questData.version = QuestDataModel.QUEST_DATA_MODEL_VERSION;
            needsUpdate = true;
        }
    }

    public void init() {
        if(minecraftClient.player != null) this.setUUID(minecraftClient.player.getUuid());
    }

    private void setUUID(UUID uuid) {
        this.questData.uuid = uuid;
    }

    public void setQuest(List<Quest> questList) {
        String location = BossBarHandler.instance().getLocation().getString();

        questData.questList.put(location, questList);
        LoggerHandler.info("Quests updated");
        this.needsUpdate = true;
    }

    public void setFish(FishNbtObject fishNbtObject) {
        questData.questList.getOrDefault(BossBarHandler.instance().getLocation().getString(), new ArrayList<>()).forEach(quest -> {
            if(Objects.equals(quest.goal, fishNbtObject.getFishSize()) || Objects.equals(quest.goal, fishNbtObject.getRarity())) {
                quest.addCurrent();
                this.needsUpdate = true;
            }
        });
        QuestScreenHandler.instance().checkForCompletedQuests();
    }
    //endregion

    //region Model
    public static class QuestDataModel extends DataModels.DataModel {
        private static final String QUEST_DATA_MODEL_VERSION = "0.2";

        // Location, Quests
        public Map<String, List<Quest>> questList = new HashMap<>();

        public QuestDataModel() {
            super(QUEST_DATA_MODEL_VERSION, null);
        }

    }
    //endregion

    //region Quest Object
    public static class Quest {
        public final String goal;
        public final int max;
        public int current;

        public Quest(String goal, int max, int current) {
            this.goal = goal;
            this.max = max;
            this.current = current;
        }

        public void addCurrent() {
            if(this.current < this.max) {
                this.current++;
            }
        }

        public boolean isDone() {
            return current == max;
        }
    }
    //

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "questData", Pair.of(Text.literal("[questData]"), TextHelper.literal(getQuestData()))
        );
    }
    //endregion
}
