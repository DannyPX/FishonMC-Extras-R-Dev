package dannypx.foe.handler.logic;

import dannypx.foe.handler.Handler;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.CustomTextValue;
import dannypx.foe.type.custom_text.TextValue;
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
import java.util.regex.Pattern;

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

    public Pair<Boolean, CustomTextValue> getRayCast(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(block_hit_result|entity_hit_result|item_frame_item)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "block_hit_result" -> {
                        if(getBlockHitResult() != null && !getBlockFromHitResult().getString().contains("Air")) {
                            yield PlaceholderHandler.getTextValue(new TextValue(getBlockFromHitResult()));
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "entity_hit_result" -> {
                        if(getEntityHitResult() != null && !getEntityHitResult().getEntity().getName().getString().isBlank()) {
                            yield PlaceholderHandler.getTextValue(new TextValue(getEntityHitResult().getEntity().getName()));
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "item_frame_item" -> {
                        if(getItemFrameItem() != ItemStack.EMPTY) {
                            yield PlaceholderHandler.getTextValue(new TextValue(getItemFrameItem().getName()));
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
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

    private MutableText getBlockFromHitResult() {
        if(getBlockHitResult() != null && minecraftClient.world != null) {
            BlockPos blockPos = getBlockHitResult().getBlockPos();
            Block block = minecraftClient.world.getBlockState(blockPos).getBlock();
            return block.getName();
        }
        return null;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "entityHitResult", Pair.of(getEntityHitResult() != null ? getEntityHitResult().getEntity().getName().copy() : Text.empty(), Text.empty()),
                "blockHitResult" , Pair.of(getBlockHitResult() != null ? getBlockFromHitResult() : Text.empty(), Text.empty())
        );
    }
    //endregion
}
