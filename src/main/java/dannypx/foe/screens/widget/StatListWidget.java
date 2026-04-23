package dannypx.foe.screens.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dannypx.foe.handler.logic.LoadingHandler;
import dannypx.foe.helper.GuiGraphicsHelper;
import dannypx.foe.helper.ComponentHelper;
import dannypx.foe.screens.interfaces.ScreenConstants;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class StatListWidget extends AbstractSelectionList<StatListWidget.@NotNull StatEntry> implements ScreenConstants {

    public StatListWidget(Minecraft client, int width, int height, int x, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.setPosition(x - ((width / 4) / 3), y);
    }

    @Override
    protected void extractListItems(@NotNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        RenderSystem.disableScissorForRenderTypeDraws();
        super.extractListItems(guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    @Override
    public int getRowLeft() {
        return this.getX();
    }

    @Override
    protected int scrollBarX() {
        return this.getX() + width - 14;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

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
    protected void extractListBackground(@NotNull GuiGraphicsExtractor guiGraphicsExtractor) {}

    @Override
    protected void extractListSeparators(@NotNull GuiGraphicsExtractor guiGraphicsExtractor) {}

    public static class StatEntry extends ContainerObjectSelectionList.Entry<@NotNull StatEntry>{
        boolean isHeader;
        Component category;
        Component firstColumnField;
        Component secondColumnField;
        Component thirdColumnField;
        List<ItemStack> itemStacks;
        int width = 0;

        public StatEntry(Component category, Component firstColumnField, Component secondColumnField, Component thirdColumnField, List<ItemStack> itemStacks, boolean isHeader) {
            this.isHeader = isHeader;
            this.category = category;
            this.firstColumnField = firstColumnField;
            this.secondColumnField = secondColumnField;
            this.thirdColumnField = thirdColumnField;
            this.itemStacks = itemStacks;
        }

        public StatEntry(Component category, boolean isHeader) {
            this.isHeader = isHeader;
            this.category = category;
            this.firstColumnField = Component.empty();
            this.secondColumnField = Component.empty();
            this.thirdColumnField = Component.empty();
            this.itemStacks = List.of();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of();
        }

        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public void extractContent(@NotNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            if(!LoadingHandler.instance().isLoadingDone()) {
                return;
            }

            Font font = Minecraft.getInstance().font;
            int height = font.lineHeight;
            int posY = getY() - height / 2;


            if(isHeader) {
                int headerX = getX() + width / 2;
                int headerWidth = font.width(
                        Component.literal(ComponentHelper.smallCaps(category.getString())).setStyle(category.getStyle())
                );

                GuiGraphicsHelper.text(guiGraphicsExtractor, font, category,
                        headerX - headerWidth / 2, posY,
                        true, true, true, true);
            } else {
                Component fieldComponent;
                if(!itemStacks.isEmpty()) {
                    fieldComponent = secondColumnField;
                } else if(!Objects.equals(secondColumnField, Component.empty())) {
                    fieldComponent = ComponentHelper.concat(firstColumnField, Component.literal(" "), secondColumnField);
                } else {
                    fieldComponent = firstColumnField;
                }
                int fieldComponentX = itemStacks.isEmpty() ? getX() + 17 + PADDING_QUART : getY() + 17 + PADDING_QUART + 16 + PADDING_QUART ;

                int field3X = getX() + (width/4) * 3;
                int field3Width = font.width(ComponentHelper.smallCaps(thirdColumnField.getString()));

                GuiGraphicsHelper.text(guiGraphicsExtractor, font, fieldComponent,
                        fieldComponentX, posY,
                        true, true, true, true);

                GuiGraphicsHelper.text(guiGraphicsExtractor, font, thirdColumnField,
                        field3X - field3Width / 2, posY,
                        true, true, true, true);

                if(!itemStacks.isEmpty()) {
                    long seconds = System.currentTimeMillis() / 1000;
                    int itemIndex = (int) (seconds % itemStacks.size());
                    guiGraphicsExtractor.item(itemStacks.get(itemIndex), getX() + 17 + PADDING_QUART, getY() - 16 / 2);
                }
            }
        }
    }
}
