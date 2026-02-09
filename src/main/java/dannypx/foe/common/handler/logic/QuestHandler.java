package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.fetch.BossBarHandler;
import dannypx.foe.common.handler.store.QuestDataHandler;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuestHandler {
    private static QuestHandler INSTANCE = new QuestHandler();

    public static QuestHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new QuestHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    //endregion

    //region Methods
    public void checkQuests(GenericContainerScreenHandler genericContainerScreenHandler) {
        List<QuestDataHandler.Quest> questList = new ArrayList<>();

        minecraftClient.execute(() -> {
            genericContainerScreenHandler.slots.forEach(slot -> {
                if (minecraftClient.player != null
                        && slot.inventory != minecraftClient.player.getInventory()
                        && slot.getStack().isIn(ItemTags.SHULKER_BOXES)
                        && slot.getStack().getItem() != Items.WHITE_SHULKER_BOX
                        && slot.getStack().getName().getString().startsWith("Fishing Quest")
                ) {
                    QuestDataHandler.Quest quest = this.extractQuestData(slot.getStack());

                    if(quest != null) {
                        questList.add(quest);
                    }
                }
            });

            if(!questList.isEmpty()) QuestDataHandler.instance().setQuest(questList);
        });
    }

    private QuestDataHandler.Quest extractQuestData(ItemStack stack) {
        if(stack.get(DataComponentTypes.LORE) != null) {
            List<Text> lines = stack.get(DataComponentTypes.LORE).lines();
            if(lines.size() > 6) {
                String goal = lines.get(3).getSiblings().get(3).getString().toLowerCase(Locale.US).trim();
                int max = Integer.parseInt(lines.get(6).getSiblings().get(5).getString());
                int current = Integer.parseInt(lines.get(6).getSiblings().get(3).getString());

                String location = lines.get(4).getSiblings().get(2).getString().trim();

                if(location.equals(BossBarHandler.instance().getLocation().getString().trim())) {
                    return new QuestDataHandler.Quest(goal, max, current);
                }
            }
        }
        return null;
    }

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
