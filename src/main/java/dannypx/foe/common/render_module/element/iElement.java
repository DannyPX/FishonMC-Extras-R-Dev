package dannypx.foe.common.render_module.element;

import dannypx.foe.config.Configs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public abstract class iElement {
    public float xPercent;
    public float yPercent;
    public final boolean isCopy;

    protected iElement(boolean isCopy) {
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
