package dannypx.foe.screens.widget;

import com.mojang.brigadier.StringReader;
import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.screens.element.BoxElement;
import dannypx.foe.screens.element.Element;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmallButtonWidget extends ClickableWidget {
    MinecraftClient minecraftClient = MinecraftClient.getInstance();


    private final ClickCallback clickCallback;

    Pattern NAMESPACED = Pattern.compile("^[a-z_]+:[a-z_]+$");
    Pattern PATTERN = Pattern.compile("^(?:([a-z_]+:[a-z_]+)(?:\\[(.*)\\])?|(.))$");

    private final String icon;


    Pair<String, Element> box;
    Pair<String, Element> box_hover;

    List<Pair<String, Element>> elements = new ArrayList<>();

    public SmallButtonWidget(int x, int y, int width, int height, String icon, @Nullable Tooltip tooltip, Text message, ClickCallback clickCallback) {
        super(x, y, width, height, message);
        this.icon = icon;
        this.clickCallback = clickCallback;
        this.setTooltip(tooltip);
        this.init();

        box = Pair.of("button_box", new BoxElement(minecraftClient,
                getX(),
                getY(),
                1,
                width, height, true, false));

        box_hover = Pair.of("button_hover_box", new BoxElement(minecraftClient,
                getX(),
                getY(),
                1,
                width, height, true, true));
    }

    private void init() {
        this.initElements();
    }

    private void initElements() {
        elements.clear();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBox(context);
        this.renderIcon(context);
    }

    private void renderBox(DrawContext context) {
        (hovered ? box_hover : box).value2().render(context, minecraftClient.getRenderTickCounter());
    }

    private void renderIcon(DrawContext context) {
        Matcher m = PATTERN.matcher(icon);

        if (m.matches()) {
            if(m.group(1) != null) {
                if(minecraftClient.player != null) {
                    RegistryWrapper.WrapperLookup lookup = minecraftClient.player.getRegistryManager();

                    ItemStringReader reader = new ItemStringReader(lookup);
                    StringReader stringReader = new StringReader(icon);
                    try {
                        ItemStringReader.ItemResult result = reader.consume(stringReader);

                        ItemStack itemStack = new ItemStack(result.item(), 1);
                        itemStack.applyUnvalidatedChanges(result.components());

                        context.getMatrices().push();
                        context.getMatrices().translate(getX() + ((float) width / 2) - 6, getY() + ((float) height / 2) - 6, 1.0f);
                        context.getMatrices().scale(12f / 16f, 12f / 16f, 1.0f);

                        context.drawItem(itemStack, 0, 0);

                        context.getMatrices().pop();
                    } catch (Exception e) {
                        LoggerHandler._debug(e.getMessage());
                    }
                }
            } else {
                int textWidth = minecraftClient.textRenderer.getWidth(TextHelper.smallText(icon));
                context.getMatrices().push();
                context.getMatrices().translate(0.0f, 0.0f, 1.0f);

                DrawHelper.drawText(context,
                        minecraftClient.textRenderer,
                        Text.literal(icon),
                        getX() + (width / 2) - textWidth / 2, getY() + (height / 2) - minecraftClient.textRenderer.fontHeight / 2,
                        true,
                        true,
                        false,
                        true
                );

                context.getMatrices().pop();
            }
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        if(clickCallback != null) {
            this.clickCallback.onClick(this);
        }
    }

    public interface ClickCallback {
        void onClick(SmallButtonWidget smallButtonWidget);
    }
}
