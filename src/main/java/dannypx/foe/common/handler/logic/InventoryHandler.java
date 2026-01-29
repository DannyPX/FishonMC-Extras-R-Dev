package dannypx.foe.common.handler.logic;

import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryHandler {
    private static InventoryHandler INSTANCE = new InventoryHandler();

    public static InventoryHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private List<UUID> trackedFish = new ArrayList<>();

    public List<UUID> getTrackedFish() {
        return trackedFish;
    }
    //endregion

    //region Methods
    public void tick() {

    }

    public boolean trackAllFish() {
        if(minecraftClient.player != null) {
            trackedFish.clear();
            minecraftClient.player.getInventory().main.forEach(itemStack -> {
                Pair<Boolean, NbtObject> validatedItem = ValidateItem.isFish(itemStack);
                if(validatedItem.v1() && validatedItem.v2().isOwn()) {
                    trackedFish.add(validatedItem.v2().getUUID());
                }
            });
            return true;
        }
        return false;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "trackedFish", Pair.of(Text.literal("[trackedFish]"), TextHelper.literal(getTrackedFish()))
        );
    }
    //endregion
}
