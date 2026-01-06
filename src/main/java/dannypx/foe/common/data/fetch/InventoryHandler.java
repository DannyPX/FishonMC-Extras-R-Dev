package dannypx.foe.common.data.fetch;

import dannypx.foe.common.data.logic.ExampleHandler;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;

import java.util.Map;

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
    //endregion

    //region Methods
    public void tick() {

    }
    //endregion

    //region Dev
    protected Map<String, Pair<Text, Tooltip>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), null)
        );
    }
    //endregion
}
