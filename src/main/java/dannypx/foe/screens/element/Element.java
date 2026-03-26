package dannypx.foe.screens.element;

import dannypx.foe.type.Alignment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public abstract class Element {
    public int width;
    public int height;
    public Alignment alignment;
    public float xPos;
    public float yPos;
    public Text message;
    public final boolean isCopy;

    protected Element(int width, int height, float xPos, float yPos, Alignment alignment, Text message, boolean isCopy) {
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
        this.alignment = alignment;
        this.message = message;
        this.isCopy = isCopy;
    }

    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {}

    public void setXPercent(float percent) {
        xPos = percent;
    }

    public void setYPercent(float percent) {
        yPos = percent;
    }
}
