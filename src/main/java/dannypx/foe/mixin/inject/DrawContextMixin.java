package dannypx.foe.mixin.inject;

import dannypx.foe.common.helper.DrawHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow public abstract MatrixStack getMatrices();

    @Inject(method = "drawStackCount", at = @At("HEAD"), cancellable = true)
    private void drawStackCountInject(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo ci) {

        Pair<Boolean, NbtObject> validatedItem = ValidateItem.isServerItem(stack);

        int count = validatedItem.v2().getCount();
        Text countText = TextHelper.literal(TextHelper.smallText(TextHelper.shortenNumber(count, 0)));
        int countWidth = textRenderer.getWidth(countText);

        this.getMatrices().push();
        this.getMatrices().translate(0.0F, 0.0F, 200.0F);
        if(count > 1) DrawHelper.drawText((DrawContext) (Object) this, textRenderer, countText,
                x + 19 - 2 - countWidth, y + 6 + 4,
                true,
                true,
                false,
                true
        );
        this.getMatrices().pop();

        ci.cancel();
    }
}
