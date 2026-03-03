package dannypx.foe.screens.element.hud;

import dannypx.foe.common.handler.logic.LoadingHandler;
import dannypx.foe.common.handler.logic.NotifierHandler;
import dannypx.foe.common.type.Pair;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.Element;
import dannypx.foe.screens.element.NotificationElement;
import dannypx.foe.screens.interfaces.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NotifierElement extends Element implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private final List<NotificationElement> notificationElements = new ArrayList<>();

    private static final int WIDTH = 200;
    //endregion

    public NotifierElement(MinecraftClient minecraftClient) {
        super(WIDTH,
                50,
                Configs.hudConfig.notifierElementXPosition.get() / 100f,
                Configs.hudConfig.notifierElementYPosition.get() / 100f,
                Configs.hudConfig.notifierElementAlignment.get(),
                Configs.hudConfig.notifierElementGroup.translation("Notifier Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    public NotifierElement(MinecraftClient minecraftClient, boolean isCopy) {
        super(WIDTH,
                50,
                Configs.hudConfig.notifierElementXPosition.get() / 100f,
                Configs.hudConfig.notifierElementYPosition.get() / 100f,
                Configs.hudConfig.notifierElementAlignment.get(),
                Configs.hudConfig.notifierElementGroup.translation("Notifier Element"),
                isCopy);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        int scaledWidth = (int) (minecraftClient.getWindow().getScaledWidth() * (1 / Configs.hudConfig.notifierElementScale.get()));
        int scaledHeight = (int) (minecraftClient.getWindow().getScaledHeight() * (1 / Configs.hudConfig.notifierElementScale.get()));

        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(Configs.hudConfig.notifierElementScale.get(), Configs.hudConfig.notifierElementScale.get(), 1f);
        if(LoadingHandler.instance().isLoadingDone()
                && Configs.hudConfig.showNotifierElement.get()
        ) {
            // Position
            if(!isCopy) {
                xPos = Configs.hudConfig.notifierElementXPosition.get() / 100f;
                yPos = Configs.hudConfig.notifierElementYPosition.get() / 100f;
            }

            int x = switch (Configs.hudConfig.notifierElementAlignment.get()) {
                case TOP_LEFT, BOTTOM_LEFT -> Math.round(scaledWidth * xPos);
                case TOP_RIGHT, BOTTOM_RIGHT -> scaledWidth
                        - Math.round(scaledWidth * xPos);
                default -> 0;
            };

            int y = switch (Configs.hudConfig.notifierElementAlignment.get()) {
                case TOP_LEFT, TOP_RIGHT -> Math.round(scaledHeight * yPos);
                case BOTTOM_LEFT, BOTTOM_RIGHT -> scaledHeight
                        - Math.round(scaledHeight * yPos);
                default -> 0;
            };

            Pair<Integer, Integer> dimensions = this.assembleNotificationElements();

            x = switch (Configs.hudConfig.notifierElementAlignment.get()) {
                case TOP_RIGHT, BOTTOM_RIGHT -> x - dimensions.v1();
                default -> x;
            };

            y = switch (Configs.hudConfig.notifierElementAlignment.get()) {
                case BOTTOM_LEFT, BOTTOM_RIGHT -> y - dimensions.v2();
                default -> y;
            };

            this.renderNotifications(drawContext, tickCounter, x, y);
        }
        drawContext.getMatrices().pop();
    }

    private void renderNotifications(DrawContext drawContext, RenderTickCounter tickCounter, int x, int y) {
        AtomicInteger yTranslation = new AtomicInteger(y);
        for (NotificationElement notificationElement : notificationElements) {
            notificationElement.setX(x);
            notificationElement.setY(yTranslation.get());
            notificationElement.render(drawContext, tickCounter);

            yTranslation.addAndGet(notificationElement.height + PADDING_QUART);
        }
    }

    private Pair<Integer, Integer> assembleNotificationElements() {
        notificationElements.clear();
        NotifierHandler.instance().getNotifications().forEach(notification -> {
            notificationElements.add(new NotificationElement(
                    minecraftClient,
                    WIDTH,
                    notification.item,
                    notification.rows,
                    notification.columns,
                    notification.textList
            ));
        });

        return Pair.of(WIDTH, notificationElements.stream().mapToInt(n -> n.height).sum() + PADDING_QUART * notificationElements.size() - PADDING_QUART);
    }
    //endregion
}
