package dannypx.foe.handler.renderer;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.ScreenHandler;
import dannypx.foe.handler.logic.SearchHandler;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

public class PresetsScreenRenderHandler extends ScreenHandler {
    private static PresetsScreenRenderHandler INSTANCE = new PresetsScreenRenderHandler();

    public static PresetsScreenRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new PresetsScreenRenderHandler();
        }
        return INSTANCE;
    }

    //region Fields

    private boolean isOnScreen = false;

    public void setOnScreen(boolean onScreen) {
        isOnScreen = onScreen;
    }
    //endregion

    //region Methods
    public void init(Screen screen) {
        this.setOnScreen(true);
    }

    public void checkMouseScroll(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(isOnScreen
                && screen instanceof GenericContainerScreen containerScreen
        ) {
            int syncId = containerScreen.getScreenHandler().syncId;
            int moveSlot = -1;

            if(verticalAmount > 0) moveSlot = 46;
            else if (verticalAmount < 0) moveSlot = 52;

            if(moveSlot != -1
                    && !containerScreen.getScreenHandler().getSlot(moveSlot).getStack().isEmpty()
                    && containerScreen.getScreenHandler().getSlot(moveSlot).getStack().getItem() == Items.GUNPOWDER
                    && minecraftClient.interactionManager != null
            ) {
                minecraftClient.interactionManager.clickSlot(
                        syncId,
                        moveSlot,
                        0,
                        SlotActionType.PICKUP,
                        minecraftClient.player
                );
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
