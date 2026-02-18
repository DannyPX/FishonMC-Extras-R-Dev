package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.type.Pair;
import net.minecraft.block.Block;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RayCastHandler extends Handler {
    private static RayCastHandler INSTANCE = new RayCastHandler();

    public static RayCastHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new RayCastHandler();
        }
        return INSTANCE;
    }

    //region Fields
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
                "entityHitResult", Pair.of(getEntityHitResult() != null ? getEntityHitResult().getEntity().getName().copy() : Text.empty(), Text.empty()),
                "blockHitResult" , Pair.of(getBlockFromHitResult() != null ? getBlockFromHitResult() : Text.empty(), Text.empty())
        );
    }

    private MutableText getBlockFromHitResult() {
        if(getBlockHitResult() != null && minecraftClient.world != null) {
            BlockPos blockPos = getBlockHitResult().getBlockPos();
            Block block = minecraftClient.world.getBlockState(blockPos).getBlock();
            return block.getName();
        }
        return null;
    }
    //endregion
}
