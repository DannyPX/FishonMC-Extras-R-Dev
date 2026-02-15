package dannypx.foe.screens.chat;

import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import dannypx.foe.mixin.accessor.ChatScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class ChatRenderHandler {
    private static ChatRenderHandler INSTANCE = new ChatRenderHandler();

    public static ChatRenderHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatRenderHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = minecraftClient.textRenderer;
    //endregion

    //region Methods

    public void init(Screen screen) {

    }

    public void render(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        if(screen instanceof ChatScreen chatScreen) {
            TextFieldWidget textfield = ((ChatScreenAccessor) chatScreen).getChatField();
            if(textfield.getText().isBlank() && ProfileDataHandler.instance().getProfileData().isInCrewChat) {
                drawContext.drawText(textRenderer,
                        Text.literal(TextHelper.smallText("You are in crew chat")).formatted(Formatting.GREEN),
                        4,
                        minecraftClient.getWindow().getScaledHeight() - textRenderer.fontHeight - 4,
                        0xFFFFFF,
                        true);
            }
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(

        );
    }
    //endregion
}
