package dannypx.foe.common.handler.renderer;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.fetch.ScoreboardHandler;
import dannypx.foe.common.handler.logic.CrewHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.BoxElement;
import dannypx.foe.screens.element.Element;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public class TabRendererHandler extends Handler {
    private static TabRendererHandler INSTANCE = new TabRendererHandler();

    public static TabRendererHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TabRendererHandler();
        }
        return INSTANCE;
    }

    //region Fields
    public void renderCrewTab(DrawContext context, int x1, int y1, int x2, int y2, int color, int indexPlayerEntry, List<PlayerListEntry> playerEntries) {
        if(color == minecraftClient.options.getTextBackgroundColor(553648127)) {
            int index = indexPlayerEntry + 1 >= playerEntries.size() ? 0 : indexPlayerEntry + 1;

            if(!ScoreboardHandler.instance().getCrew().getString().isBlank()
                    && ProfileDataHandler.instance().getProfileData().hasImportedCrew
                    && !CrewHandler.instance().getOnlineMembers().isEmpty()
                    && index == 0
                    && Configs.rendererConfig.showOnlineCrewMembers.get()
            ) {
                // Header Crew Name
                int height = 16;
                int width = 40;

                Element crewBox = new BoxElement(MinecraftClient.getInstance(), x1, y1 - (height - 5) - 1, -1, width, height, true, false, true, true, false, true);
                crewBox.render(context, minecraftClient.getRenderTickCounter());

                Text crewText = Text.literal(ScoreboardHandler.instance().getCrew().getString());
                DrawHelper.drawText(context, minecraftClient.textRenderer, crewText, x1 + width / 2 - TextHelper.getWidth(minecraftClient.textRenderer, crewText, true) / 2, y1 - (height - 5) + (height - 5) / 2 - minecraftClient.textRenderer.fontHeight / 2 + 1, true, true, false, true);

                // Left Bar
                Element leftBar = new BoxElement(MinecraftClient.getInstance(), x1 - 5, y1 - 1, -1, 5, CrewHandler.instance().getOnlineMembers().size() * 9 + 1, true, false, true, false, true, true);
                leftBar.render(context, minecraftClient.getRenderTickCounter());

                int gradientWidth = 150;

                // Box
                DrawHelper.drawHorizontalGradient(context, x1, y1, x1 + gradientWidth, y1 + CrewHandler.instance().getOnlineMembers().size() * 9 - 1, 0x88FFAA00, 0x00FFAA00);

                // Border
                context.drawVerticalLine(x1 - 1, y1 - 1, y1 + CrewHandler.instance().getOnlineMembers().size() * 9 - 1, 0xFF000000);
                DrawHelper.drawHorizontalGradient(context, x1 - 1, y1 - 1, x1 + gradientWidth, y1, 0xFF000000, 0x00000000);
                DrawHelper.drawHorizontalGradient(context, x1 - 1, y1 + CrewHandler.instance().getOnlineMembers().size() * 9 - 1, x1 + gradientWidth, y1 + CrewHandler.instance().getOnlineMembers().size() * 9, 0xFF000000, 0x00000000);
            }
        }
    }
    //endregion

    //region Methods
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
