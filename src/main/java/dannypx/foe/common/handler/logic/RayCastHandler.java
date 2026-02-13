package dannypx.foe.common.handler.logic;

import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RayCastHandler {
    private static RayCastHandler INSTANCE = new RayCastHandler();

    public static RayCastHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new RayCastHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private HitResult hitResult = null;
    private @Nullable BlockHitResult blockHitResult = null;
    private @Nullable EntityHitResult entityHitResult = null;

    private @Nullable ItemStack itemFrameItem = ItemStack.EMPTY;

    public @Nullable BlockHitResult getBlockHitResult() {
        return blockHitResult;
    }

    public @Nullable EntityHitResult getEntityHitResult() {
        return entityHitResult;
    }

    public @Nullable ItemStack getItemFrameItem() {
        return itemFrameItem;
    }
    //endregion

    //region Methods
    public void tick() {
        hitResult = minecraftClient.crosshairTarget;

        if(hitResult instanceof BlockHitResult blockHit) this.blockHitResult = blockHit;
        else this.blockHitResult = null;

        if(hitResult instanceof EntityHitResult entityHit) this.entityHitResult = entityHit;
        else this.entityHitResult = null;
        
        this.checkEntity();
    }

    private void checkEntity() {
        if(this.entityHitResult != null && entityHitResult.getEntity() instanceof ItemFrameEntity itemFrameEntity) {
            itemFrameItem = itemFrameEntity.getHeldItemStack();
        } else {
            itemFrameItem = ItemStack.EMPTY;
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
