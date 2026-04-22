package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.CrewHandler;
import dannypx.foe.handler.logic.NotifierHandler;
import dannypx.foe.handler.store.CrewDataHandler;
import dannypx.foe.handler.store.ProfileDataHandler;
import dannypx.foe.type.tuple.Pair;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
    public void checkCrewInfo(ChestMenu chestMenu) {
        ItemStack crewInfoStack = chestMenu.getSlot(13).getItem();
        if(!ScoreboardHandler.instance().getCrew().getString().isBlank()
                && crewInfoStack.get(DataComponents.LORE) != null
        ) {
            List<Component> componentList = crewInfoStack.get(DataComponents.LORE).lines();
            if(componentList.size() > 3
                    && componentList.get(2).getSiblings().size() > 4
                    && Objects.equals(componentList.get(2).getSiblings().get(3).getString(), ScoreboardHandler.instance().getCrew().getString())
            ) {
                Map<UUID, Pair<String, ItemStack>> crewMembers = new HashMap<>();

                for (int i = 28; i < 44; i++) {
                    ItemStack itemStack = chestMenu.getSlot(i).getItem();
                    if(itemStack.getItem() == Items.PLAYER_HEAD
                            && itemStack.get(DataComponents.PROFILE) != null
                            && itemStack.get(DataComponents.PROFILE).partialProfile().id() != null
                    ) {
                        crewMembers.put(itemStack.get(DataComponents.PROFILE).partialProfile().id(), Pair.of(itemStack.get(DataComponents.PROFILE).partialProfile().name(), itemStack));
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
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "key", Pair.of(Component.literal("value"), Component.empty())
        );
    }
    //endregion
}
