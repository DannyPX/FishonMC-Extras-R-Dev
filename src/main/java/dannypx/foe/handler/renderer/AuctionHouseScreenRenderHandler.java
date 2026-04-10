package dannypx.foe.handler.renderer;

import dannypx.foe.handler.ScreenHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.handler.logic.SearchHandler;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuctionHouseScreenRenderHandler extends ScreenHandler {
    private static AuctionHouseScreenRenderHandler INSTANCE = new AuctionHouseScreenRenderHandler();

    public static AuctionHouseScreenRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new AuctionHouseScreenRenderHandler();
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
        SearchHandler.instance().setOnScreen(true);
        this.setOnScreen(true);
        this.initWidgets(screen);
    }

    private void initWidgets(Screen screen) {
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.mainConfig.enableMod.get()
        ) {
            List<ClickableWidget> widgets = new ArrayList<>();

            widgets.add(SearchHandler.getSearchBar(
                    minecraftClient.getWindow().getScaledWidth() / 2 - 80,
                    minecraftClient.getWindow().getScaledHeight() / 2 - 155,
                    160,
                    20
            ));

            widgets.forEach(Screens.getButtons(screen)::add);
        }
    }

    public void checkMouseScroll(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(isOnScreen
                && screen instanceof GenericContainerScreen containerScreen
        ) {
            int syncId = containerScreen.getScreenHandler().syncId;
            int moveSlot = -1;

            if(verticalAmount > 0) moveSlot = 46;
            else if (verticalAmount < 0) moveSlot = 51;

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
