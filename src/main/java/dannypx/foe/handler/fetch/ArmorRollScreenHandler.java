package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.CodeExecuterHandler;
import dannypx.foe.item.ArmorTagObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.type.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

public class ArmorRollScreenHandler extends Handler {
    private static ArmorRollScreenHandler INSTANCE = new ArmorRollScreenHandler();

    public static ArmorRollScreenHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ArmorRollScreenHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private List<ItemStack> rollList = new ArrayList<>();
    private ArmorTagObject armor = ArmorTagObject.empty();

    public List<ItemStack> getRollList() {
        return rollList;
    }

    public ArmorTagObject getArmor() {
        return armor;
    }
    //endregion

    //region Methods
    public void checkArmorRolls(ChestMenu chestMenu) {
        CodeExecuterHandler.runLater(2, () -> {
            rollList.clear();
            armor = ArmorTagObject.empty();
            for (int i = 11; i < 16; i++) {
                rollList.add(chestMenu.getSlot(i).getItem());
            }

            Pair<Boolean, ArmorTagObject> validatedArmor = ValidateItem.isArmor(chestMenu.getSlot(31).getItem());
            if(validatedArmor.value1()) {
                armor = validatedArmor.value2();
            }
        });
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
