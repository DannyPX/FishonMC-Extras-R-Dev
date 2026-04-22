package dannypx.foe.handler.renderer;

import dannypx.foe.handler.ScreenHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.handler.logic.SearchHandler;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChestScreenRenderHandler extends ScreenHandler {
    private static ChestScreenRenderHandler INSTANCE = new ChestScreenRenderHandler();

    public static ChestScreenRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ChestScreenRenderHandler();
        }
        return INSTANCE;
    }

    //region Fields
    //endregion

    //region Methods
    public void init(Screen screen) {
        SearchHandler.instance().setOnScreen(true);
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

            widgets.forEach(Screens.getButtons(screen)::add);
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
