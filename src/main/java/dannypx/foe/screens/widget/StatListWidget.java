package dannypx.foe.screens.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.helper.DrawHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.screens.interfaces.ScreenConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

public class StatListWidget extends EntryListWidget<StatListWidget.StatEntry> implements ScreenConstants {

    public StatListWidget(MinecraftClient client, int width, int height, int x, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.setPosition(x - ((width / 4) / 3), y);
    }

    @Override
    protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.disableScissor();
        super.renderList(context, mouseX, mouseY, delta);
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }

    @Override
    protected int getScrollbarX() {
        return this.getX() + width - 14;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public int addEntry(StatEntry entry) {
        entry.setWidth(width);
        return super.addEntry(entry);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    protected void drawMenuListBackground(DrawContext context) {}

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {}

    public static class StatEntry extends ElementListWidget.Entry<StatEntry>{
        boolean isHeader;
        Text category;
        Text field1;
        Text field2;
        Text field3;
        List<ItemStack> itemStacks;
        int width = 0;

        public StatEntry(Text category, Text field1, Text field2, Text field3, List<ItemStack> itemStacks, boolean isHeader) {
            this.isHeader = isHeader;
            this.category = category;
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.itemStacks = itemStacks;
        }

        public StatEntry(Text category, boolean isHeader) {
            this.isHeader = isHeader;
            this.category = category;
            this.field1 = Text.empty();
            this.field2 = Text.empty();
            this.field3 = Text.empty();
            this.itemStacks = List.of();
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }

        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if(!LoadingHandler.instance().isLoadingDone()) {
                return;
            }

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int height = textRenderer.fontHeight;
            int posY = y - height / 2;


            if(isHeader) {
                int headerX = x + width / 2;
                int headerWidth = textRenderer.getWidth(
                        Text.literal(TextHelper.smallText(category.getString())).setStyle(category.getStyle())
                );

                DrawHelper.drawText(context, textRenderer, category,
                        headerX - headerWidth / 2, posY,
                        true, true, true, true);
            } else {
                Text fieldText;
                if(!itemStacks.isEmpty()) {
                    fieldText = field2;
                } else if(!Objects.equals(field2, Text.empty())) {
                    fieldText = TextHelper.concat(field1, Text.literal(" "), field2);
                } else {
                    fieldText = field1;
                }
                int fieldTextX = itemStacks.isEmpty() ? x + 17 + PADDING_QUART : x + 17 + PADDING_QUART + 16 + PADDING_QUART ;

                int field3X = x + (width/4) * 3;
                int field3Width = textRenderer.getWidth(TextHelper.smallText(field3.getString()));

                DrawHelper.drawText(context, textRenderer, fieldText,
                        fieldTextX, posY,
                        true, true, true, true);

                DrawHelper.drawText(context, textRenderer, field3,
                        field3X - field3Width / 2, posY,
                        true, true, true, true);

                if(!itemStacks.isEmpty()) {
                    long seconds = System.currentTimeMillis() / 1000;
                    int itemIndex = (int) (seconds % itemStacks.size());
                    context.drawItem(itemStacks.get(itemIndex), x + 17 + PADDING_QUART, y - 16 / 2);
                }
            }
        }
    }
}
