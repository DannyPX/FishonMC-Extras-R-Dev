package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.logic.CrewHandler;
import dannypx.foe.common.handler.logic.NotifierHandler;
import dannypx.foe.common.handler.store.CrewDataHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.type.tuple.Pair;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;

public class CrewScreenHandler extends Handler {
    private static CrewScreenHandler INSTANCE = new CrewScreenHandler();

    public static CrewScreenHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new CrewScreenHandler();
        }
        return INSTANCE;
    }

    //region Fields
    //endregion

    //region Methods
    public void checkCrewInfo(GenericContainerScreenHandler genericContainerScreenHandler) {
        ItemStack crewInfoStack = genericContainerScreenHandler.getSlot(13).getStack();
        if(!ScoreboardHandler.instance().getCrew().getString().isBlank()
                && crewInfoStack.get(DataComponentTypes.LORE) != null
        ) {
            List<Text> textList = crewInfoStack.get(DataComponentTypes.LORE).lines();
            if(textList.size() > 3
                    && textList.get(2).getSiblings().size() > 4
                    && Objects.equals(textList.get(2).getSiblings().get(3).getString(), ScoreboardHandler.instance().getCrew().getString())
            ) {
                Map<UUID, Pair<String, ItemStack>> crewMembers = new HashMap<>();

                for (int i = 28; i < 44; i++) {
                    ItemStack itemStack = genericContainerScreenHandler.getSlot(i).getStack();
                    if(itemStack.getItem() == Items.PLAYER_HEAD
                            && itemStack.get(DataComponentTypes.PROFILE) != null
                            && itemStack.get(DataComponentTypes.PROFILE).id().isPresent()
                    ) {
                        crewMembers.put(itemStack.get(DataComponentTypes.PROFILE).id().get(), Pair.of(itemStack.get(DataComponentTypes.PROFILE).name().get(), itemStack));
                    }
                }

                if(!crewMembers.isEmpty()) {
                    CrewDataHandler.instance().updateCrewList(crewMembers);
                    CrewHandler.instance().updateCrewOrderedList(crewMembers);

                    if(!ProfileDataHandler.instance().getProfileData().hasImportedCrew) {
                        NotifierHandler.instance().notifyImportCrewCompleted();
                    }

                    ProfileDataHandler.instance().updateImportCrew(true);
                }
            }
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
