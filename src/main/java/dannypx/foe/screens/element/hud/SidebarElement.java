package dannypx.foe.screens.element.hud;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.fetch.BossBarHandler;
import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.handler.store.ConstantDataHandler;
import dannypx.foe.common.handler.store.QuestDataHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.Element;
import dannypx.foe.screens.interfaces.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SidebarElement extends Element implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    // isCentre, Line
    private List<Pair<Boolean, Text>> textLines = new ArrayList<>();
    private Pair<Integer, Integer> contentDimensions = Pair.of(0, 0);

    private int boxWidth = 0;
    private int boxHeight = 0;

    private static final Identifier SIDEBAR_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/sidebar_atlas.png");
    private static final int TEXTURE_WIDTH = 17;
    private static final int TEXTURE_HEIGHT = 11;
    private static final int BOX_PADDING = 5;
    private static final int MIN_WIDTH = 75;
    //endregion

    public SidebarElement(MinecraftClient minecraftClient) {
        super(75,
                50,
                Configs.hudConfig.sidebarElementXPosition.get() / 100f,
                Configs.hudConfig.sidebarElementYPosition.get() / 100f,
                Configs.hudConfig.sidebarElementAlignment.get(),
                Configs.hudConfig.sidebarElementGroup.translation("SideBarElement"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    public SidebarElement(MinecraftClient minecraftClient, boolean isCopy) {
        super(75,
                50,
                Configs.hudConfig.sidebarElementXPosition.get() / 100f,
                Configs.hudConfig.sidebarElementYPosition.get() / 100f,
                Configs.hudConfig.sidebarElementAlignment.get(),
                Configs.hudConfig.sidebarElementGroup.translation("SideBarElement"),
                isCopy);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.hudConfig.showSidebarElement.get()
        ) {
            // Position
            if(!isCopy) {
                xPos = Configs.hudConfig.sidebarElementXPosition.get() / 100f;
                yPos = Configs.hudConfig.sidebarElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.sidebarElementAlignment.get()) {
                case TOP_LEFT, BOTTOM_LEFT, LEFT -> Math.round(minecraftClient.getWindow().getScaledWidth() * xPos);
                case TOP_RIGHT, BOTTOM_RIGHT, RIGHT -> minecraftClient.getWindow().getScaledWidth()
                        - Math.round(minecraftClient.getWindow().getScaledWidth() * xPos);
                default -> 0;
            };

            int y = switch (Configs.hudConfig.sidebarElementAlignment.get()) {
                case TOP_LEFT, TOP_RIGHT, LEFT, RIGHT -> Math.round(minecraftClient.getWindow().getScaledHeight() * yPos);
                case BOTTOM_LEFT, BOTTOM_RIGHT -> minecraftClient.getWindow().getScaledHeight()
                        - Math.round(minecraftClient.getWindow().getScaledHeight() * yPos);
                default -> 0;
            };

            contentDimensions = this.assembleSidebarElements();
            boxWidth = contentDimensions.v1() + BOX_PADDING * 2 + PADDING * 2;
            boxHeight = contentDimensions.v2() + BOX_PADDING * 2 + PADDING * 2;
            if(textLines.isEmpty()) {
                return;
            }

            x = switch (Configs.hudConfig.sidebarElementAlignment.get()) {
                case TOP_RIGHT, BOTTOM_RIGHT, RIGHT -> x - boxWidth;
                default -> x;
            };

            y = switch (Configs.hudConfig.sidebarElementAlignment.get()) {
                case BOTTOM_LEFT, BOTTOM_RIGHT -> y - boxHeight;
                case LEFT, RIGHT -> y - boxHeight / 2;
                default -> y;
            };

            this.renderBox(drawContext, tickCounter, x, y);
            this.renderText(drawContext, tickCounter, x, y);
        }
    }

    private void renderText(DrawContext drawContext, RenderTickCounter tickCounter, int x, int y) {
        int textX = x + PADDING + BOX_PADDING;
        int textY = y + PADDING + BOX_PADDING;

        AtomicInteger line = new AtomicInteger(0);
        textLines.forEach(text -> {
            if(text.v1()) {
                DrawHelper.drawText(drawContext, textRenderer, text.v2(),
                        x + (boxWidth / 2) - textRenderer.getWidth(Text.literal(TextHelper.smallText(text.v2().getString())).setStyle(text.v2().getStyle())) / 2,
                        textY + line.getAndIncrement() * textRenderer.fontHeight,
                        true, true, true, true
                        );
            } else {
                DrawHelper.drawText(drawContext, textRenderer, text.v2(),
                        textX,
                        textY + line.getAndIncrement() * textRenderer.fontHeight,
                        true, true, true, true
                );
            }
        });
    }

    private void renderBox(DrawContext drawContext, RenderTickCounter tickCounter, int x, int y) {
        int ATLAS_CORNER = 8;
        int ATLAS_BAR_WIDTH = 1;
        int ATLAS_BAR_HEIGHT = 5;
        int NIB_HEIGHT = 3;

        // Alpha Box
        drawContext.fill(
                x + BOX_PADDING, y + BOX_PADDING,
                x + this.boxWidth - BOX_PADDING, y + this.boxHeight - BOX_PADDING,
                0x7f000000
        );

        // Top Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                SIDEBAR_TEXTURE,
                x, y,
                0, NIB_HEIGHT,
                ATLAS_CORNER, ATLAS_CORNER,
                ATLAS_CORNER, ATLAS_CORNER,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Top
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                SIDEBAR_TEXTURE,
                x + ATLAS_CORNER, y,
                ATLAS_CORNER, NIB_HEIGHT,
                this.boxWidth - ATLAS_CORNER * 2, ATLAS_BAR_HEIGHT,
                ATLAS_BAR_WIDTH, ATLAS_BAR_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Top Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                SIDEBAR_TEXTURE,
                x + this.boxWidth - ATLAS_CORNER, y,
                ATLAS_CORNER + ATLAS_BAR_WIDTH, NIB_HEIGHT,
                ATLAS_CORNER, ATLAS_CORNER,
                ATLAS_CORNER, ATLAS_CORNER,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Bottom Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                SIDEBAR_TEXTURE,
                x, y + this.boxHeight - ATLAS_CORNER,
                0, 0,
                ATLAS_CORNER, ATLAS_CORNER,
                ATLAS_CORNER, ATLAS_CORNER,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Bottom
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                SIDEBAR_TEXTURE,
                x + ATLAS_CORNER, y + this.boxHeight - ATLAS_CORNER + NIB_HEIGHT,
                ATLAS_CORNER, NIB_HEIGHT,
                this.boxWidth - ATLAS_CORNER * 2, ATLAS_BAR_HEIGHT,
                ATLAS_BAR_WIDTH, ATLAS_BAR_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // Bottom Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                SIDEBAR_TEXTURE,
                x + this.boxWidth - ATLAS_CORNER, y + this.boxHeight - ATLAS_CORNER,
                ATLAS_CORNER + ATLAS_BAR_WIDTH, 0,
                ATLAS_CORNER, ATLAS_CORNER,
                ATLAS_CORNER, ATLAS_CORNER,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
    }

    private Pair<Integer, Integer> assembleSidebarElements() {
        textLines.clear();
        if(Configs.hudConfig.showQuest.get()) {

            List<QuestDataHandler.Quest> questList = QuestDataHandler.instance().getQuestData().questList.getOrDefault(BossBarHandler.instance().getLocation().getString(), new ArrayList<>());
            if(!questList.isEmpty()) {
                textLines.add(Pair.of(true, TextHelper.concat(
                        Text.literal("-- ").formatted(Formatting.BOLD, Formatting.GRAY),
                        Text.literal("Quests ").formatted(Formatting.BOLD),
                        Text.literal("--").formatted(Formatting.BOLD, Formatting.GRAY)
                ).formatted(Formatting.BOLD)));
                textLines.add(Pair.of(true, Text.empty()));
                questList.forEach(quest -> {
                    Text goal = ConstantDataHandler.instance().getConstantFishText(quest.goal);
                    if(Objects.equals(goal, Text.empty())) {
                        goal = Text.literal(TextHelper.capitalize(quest.goal));
                    }
                    if(quest.isDone()) {
                        textLines.add(Pair.of(false, TextHelper.concat(
                                goal,
                                Text.literal(" "),
                                Text.literal("completed").formatted(Formatting.GREEN)
                        )));
                    } else {
                        textLines.add(Pair.of(false, TextHelper.concat(
                                goal,
                                Text.literal(" "),
                                TextHelper.literal(quest.current).formatted(Formatting.YELLOW),
                                Text.literal("/").formatted(Formatting.GRAY),
                                TextHelper.literal(quest.max).formatted(Formatting.WHITE)
                        )));
                    }
                });
            }
        }

        return Pair.of(
                Math.max(MIN_WIDTH, textLines.stream().mapToInt(line -> textRenderer.getWidth(TextHelper.smallText(line.v2().getString()))).max().orElse(0)),
                textRenderer.fontHeight * textLines.size()
        );
    }
    //endregion
}
