package dannypx.foe.handler.renderer;

import dannypx.foe.handler.ScreenHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.handler.logic.SearchHandler;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonalVaultScreenRenderHandler extends ScreenHandler {
    private static PersonalVaultScreenRenderHandler INSTANCE = new PersonalVaultScreenRenderHandler();

    public static PersonalVaultScreenRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new PersonalVaultScreenRenderHandler();
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
            List<AbstractWidget> widgets = new ArrayList<>();

            widgets.add(SearchHandler.getSearchBar(
                    minecraft.getWindow().getGuiScaledWidth() / 2 - 80,
                    minecraft.getWindow().getGuiScaledHeight() / 2 - 133,
                    160,
                    20
            ));

            widgets.forEach(Screens.getWidgets(screen)::add);
        }
    }

    public void checkMouseScroll(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if(isOnScreen
                && screen instanceof ContainerScreen containerScreen
        ) {
            int syncId = containerScreen.getMenu().containerId;
            int moveSlot = -1;

            if(verticalAmount > 0) moveSlot = 46;
            else if (verticalAmount < 0) moveSlot = 52;

            if(moveSlot != -1
                    && !containerScreen.getMenu().getSlot(moveSlot).getItem().isEmpty()
                    && containerScreen.getMenu().getSlot(moveSlot).getItem().getItem() == Items.GUNPOWDER
                    && minecraft.gameMode != null
            ) {
                minecraft.gameMode.handleContainerInput(
                        syncId,
                        moveSlot,
                        0,
                        ContainerInput.PICKUP,
                        minecraft.player
                );
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
