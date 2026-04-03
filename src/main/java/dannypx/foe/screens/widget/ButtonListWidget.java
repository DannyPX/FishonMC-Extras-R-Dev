package dannypx.foe.screens.widget;

import dannypx.foe.screens.interfaces.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ButtonListWidget extends EntryListWidget<ButtonListWidget.ButtonEntry> implements ScreenConstants {

    private final String headerTitle;

    public ButtonListWidget(MinecraftClient client,
                            int width,
                            int height,
                            int top,
                            int bottom,
                            int itemHeight,
                            String title) {
        super(client, width, height, top, bottom, itemHeight);
        headerTitle = title;
    }

    @Override
    protected int getScrollbarX() {
        return width / 2 + (BUTTON_WIDTH + PADDING * 2) / 2;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected void renderHeader(DrawContext context, int x, int y) {
        context.drawCenteredTextWithShadow(
                client.textRenderer,
                Text.literal(headerTitle),
                x + getRowWidth() / 2,
                y + 4,
                0xFFFFFF
        );
    }

    @Override
    public int addEntry(ButtonEntry entry) {
        return super.addEntry(entry);
    }

    public int addEntry(ButtonEntry entry, int pos) {
        this.children().add(pos, entry);
        return this.children().size() - 1;
    }

    public void swapUp(int pos) {
        Collections.swap(this.children(), pos, pos - 1);
    }

    public void swapDown(int pos) {
        Collections.swap(this.children(), pos, pos + 1);
    }

    @Override
    public boolean removeEntry(ButtonEntry entry) {
        return super.removeEntry(entry);
    }

    public static class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {

        private final ButtonWidget button;
        private final ButtonWidget smallButton;
        private final ButtonWidget upButton;
        private final ButtonWidget downButton;

        public ButtonEntry(ButtonWidget button) {
            this.button = button;
            this.smallButton = null;
            this.upButton = null;
            this.downButton = null;
        }

        public ButtonEntry(ButtonWidget button, ButtonWidget smallButton, ButtonWidget upButton, ButtonWidget downButton) {
            this.button = button;
            this.smallButton = smallButton;
            this.upButton = upButton;
            this.downButton = downButton;
        }

        @Override
        public List<? extends Element> children() {
            List<ButtonWidget> children = new ArrayList<>(List.of(button));

            if(smallButton != null) {
                children.add(smallButton);
            }

            if(upButton != null) children.add(upButton);
            if(downButton != null) children.add(downButton);

            return children;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            List<ButtonWidget> children = new ArrayList<>(List.of(button));

            if(smallButton != null) {
                children.add(smallButton);
            }

            if(upButton != null) children.add(upButton);
            if(downButton != null) children.add(downButton);

            return children;
        }

        @Override
        public void render(
                DrawContext context,
                int index,
                int y,
                int x,
                int entryWidth,
                int entryHeight,
                int mouseX,
                int mouseY,
                boolean hovered,
                float delta
        ) {
            button.setPosition(
                    x + (entryWidth - button.getWidth()) / 2,
                    y
            );
            button.render(context, mouseX, mouseY, delta);

            if(smallButton != null) {
                smallButton.setPosition(
                        x + entryWidth - smallButton.getWidth() - PADDING,
                        y
                );

                smallButton.render(context, mouseX, mouseY, delta);
            }

            if(upButton != null) {
                upButton.setPosition(
                        x + PADDING,
                        y
                );

                upButton.render(context, mouseX, mouseY, delta);
            }

            if(downButton != null) {
                downButton.setPosition(
                        x + PADDING,
                        y + entryHeight / 2
                );

                downButton.render(context, mouseX, mouseY, delta);
            }
        }
    }
}
