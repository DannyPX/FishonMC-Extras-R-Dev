package dannypx.foe.mixin.accessor;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.client.gui.components.AbstractSelectionList$Entry")
public interface AbstractSelectionListEntryAccessor {

    @Invoker("setY")
    void callSetY(int y);

    @Invoker("setHeight")
    void callSetHeight(int i);

    @Invoker("setWidth")
    void callSetWidth(int i);

    @Invoker("setX")
    void callSetX(int x);
}
