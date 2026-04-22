package dannypx.foe.handler.renderer;

import dannypx.foe.handler.ScreenHandler;
import dannypx.foe.handler.store.ProfileDataHandler;
import dannypx.foe.helper.ComponentHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.mixin.accessor.ChatScreenAccessor;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonColors;

public class ChatScreenRenderHandler extends ScreenHandler {
    private static ChatScreenRenderHandler INSTANCE = new ChatScreenRenderHandler();

    public static ChatScreenRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatScreenRenderHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final Font textRenderer = minecraft.font;
    //endregion

    //region Methods
    @Override
    public void render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        if(screen instanceof ChatScreen chatScreen) {
            EditBox chatBox = ((ChatScreenAccessor) chatScreen).getInput();
            if(chatBox.getValue().isBlank() && ProfileDataHandler.instance().getProfileData().isInCrewChat) {
                guiGraphics.drawString(textRenderer,
                        Component.literal(ComponentHelper.smallText("You are in crew chat")).withStyle(ChatFormatting.GREEN),
                        4,
                        minecraft.getWindow().getGuiScaledHeight() - textRenderer.lineHeight - 4,
                        CommonColors.WHITE,
                        true);
            }
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(

        );
    }
    //endregion
}
