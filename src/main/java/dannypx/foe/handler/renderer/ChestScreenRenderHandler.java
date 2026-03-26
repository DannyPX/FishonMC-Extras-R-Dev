package dannypx.foe.handler.renderer;

import dannypx.foe.handler.ScreenHandler;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.handler.logic.SearchHandler;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
            List<ClickableWidget> widgets = new ArrayList<>();

            widgets.add(SearchHandler.getSearchBar(
                    minecraftClient.getWindow().getScaledWidth() / 2 - 80,
                    minecraftClient.getWindow().getScaledHeight() / 2 - 133,
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
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
