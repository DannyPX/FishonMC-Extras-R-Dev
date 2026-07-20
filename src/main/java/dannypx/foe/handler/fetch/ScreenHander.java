package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.CodeExecuterHandler;
import dannypx.foe.handler.logic.EventHandler;
import dannypx.foe.handler.logic.InventoryHandler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.handler.renderer.ChatScreenRenderHandler;
import dannypx.foe.handler.renderer.InventoryScreenRenderHandler;
import dannypx.foe.type.placeholder.ComponentValue;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import dannypx.foe.type.tuple.Pair;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.regex.Pattern;

public class ScreenHander extends Handler {
    private static ScreenHander INSTANCE = new ScreenHander();

    public static ScreenHander instance() {
        if (INSTANCE == null) {
            INSTANCE = new ScreenHander();
        }
        return INSTANCE;
    }

    //region Fields
    private Component lastScreen = Component.empty();

    public Pair<Boolean, PlaceholderValue> getScreen(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(last_screen)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "last_screen" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(lastScreen));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void onAfterInitScreen(Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) {
        if(screen instanceof InventoryScreen inventoryScreen) {
            InventoryScreenRenderHandler.instance().init(inventoryScreen);
            ScreenMouseEvents.afterMouseScroll(inventoryScreen).register(InventoryScreenRenderHandler.instance()::onMouseScrolled);
        } else if(screen instanceof ContainerScreen genericContainerScreen) {
            GenericContainerScreenHandler.instance().init(genericContainerScreen);
            ScreenEvents.afterRender(screen).register(GenericContainerScreenHandler.instance()::render);
        } else if(screen instanceof ChatScreen) {
            ScreenEvents.afterRender(screen).register(ChatScreenRenderHandler.instance()::render);
        }

        this.dispatchLastScreen(screen.getTitle());
        ScreenEvents.remove(screen).register(this::onRemoveScreen);
    }

    public void dispatchLastScreen(Component lastScreen) {
        CodeExecuterHandler.runLater(3, () -> {
            this.lastScreen = lastScreen;
            EventHandler.instance().onScreenOpen();
        });
    }

    private void onRemoveScreen(Screen screen) {
        InventoryHandler.instance().trackFishOffSide();
        EventHandler.instance().onScreenClose();
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
