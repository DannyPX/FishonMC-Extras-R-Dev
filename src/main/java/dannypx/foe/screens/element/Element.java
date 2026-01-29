package dannypx.foe.screens.element;

import dannypx.foe.common.type.Alignment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public abstract class Element {
    public int width;
    public int height;
    public Alignment alignment;
    public float xPercent;
    public float yPercent;
    public Text message;
    public final boolean isCopy;

    protected Element(int width, int height, float xPercent, float yPercent, Alignment alignment, Text message, boolean isCopy) {
        this.width = width;
        this.height = height;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.alignment = alignment;
        this.message = message;
        this.isCopy = isCopy;
    }

    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {};

    public void setXPercent(float percent) {
        xPercent = percent;
    };

    public void setYPercent(float percent) {
        yPercent = percent;
    };
}
