package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public class LightHandler extends Handler {
    private static LightHandler INSTANCE = new LightHandler();

    public static LightHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new LightHandler();
        }
        return INSTANCE;
    }

    //region Fields

    private static BlockPos lastPos = null;
    private static BlockPos prevPos = null;
    //endregion

    //region Methods
    @Override
    public void tick() {
        this.updateBobberLight();
    }

    public void updateBobberLight() {
        if(minecraftClient.world != null
                && minecraftClient.player != null
                && Configs.rendererConfig.showLightBobber.get()
        ) {
            FishingBobberEntity bobber = minecraftClient.player.fishHook;

            if (bobber != null) {
                BlockPos currentPos = bobber.getBlockPos();

                if (prevPos != null) {
                    this.scheduleBlockUpdate(minecraftClient, prevPos);
                }

                this.scheduleBlockUpdate(minecraftClient, currentPos);

                prevPos = currentPos;
                lastPos = currentPos;
            } else {
                if (lastPos != null) {
                    this.scheduleBlockUpdate(minecraftClient, lastPos);
                }

                if (prevPos != null) {
                    this.scheduleBlockUpdate(minecraftClient, prevPos);
                }

                lastPos = null;
                prevPos = null;
            }
        }
    }

    public int calculateBobberLight(long blockPos) {
        if(minecraftClient.player != null
                && Configs.rendererConfig.showLightBobber.get()
        ) {
            int RADIUS = Configs.rendererConfig.lightRadiusBobber.get();
            int MAX_LIGHT = Configs.rendererConfig.maxLightLevelBobber.get();

            FishingBobberEntity bobber = minecraftClient.player.fishHook;
            if(bobber != null) {
                BlockPos pos = BlockPos.fromLong(blockPos);

                double dx = pos.getX() + 0.5 - bobber.getX();
                double dy = pos.getY() + 0.5 - bobber.getY();
                double dz = pos.getZ() + 0.5 - bobber.getZ();

                double distSq = dx * dx + dy * dy + dz * dz;
                double maxDistSq = RADIUS * RADIUS;

                if (distSq > maxDistSq) return 0;

                double factor = 1.0 - (distSq / maxDistSq);
                return (int)(MAX_LIGHT * factor);
            }
        }
        return 0;
    }

    private void scheduleBlockUpdate(MinecraftClient client, BlockPos pos) {
        int RADIUS = Configs.rendererConfig.lightRadiusBobber.get();

        client.worldRenderer.scheduleBlockRenders(
                pos.getX() - RADIUS, pos.getY() - RADIUS, pos.getZ() - RADIUS,
                pos.getX() + RADIUS, pos.getY() + RADIUS, pos.getZ() + RADIUS
        );
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
