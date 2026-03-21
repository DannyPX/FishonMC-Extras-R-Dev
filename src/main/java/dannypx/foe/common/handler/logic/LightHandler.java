package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.tuple.Triplet;
import dannypx.foe.config.Configs;
import dannypx.foe.mixin.accessor.WorldRendererAccessor;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

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
    private BlockPos prevPos = null;
    //endregion

    //region Methods
    @Override
    public void tick() {
        this.updateBobberLight();
    }

    private int getRadius() {
        return Configs.rendererConfig.lightRadiusBobber.get();
    }

    private int getMaxLight() {
        return Configs.rendererConfig.maxLightLevelBobber.get();
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
                    this.scheduleBlockUpdate(prevPos);
                }

                this.scheduleBlockUpdate(currentPos);

                prevPos = currentPos;
            } else {
                if (prevPos != null) {
                    this.scheduleBlockUpdate(prevPos);
                }
                prevPos = null;
            }
        }
    }

    private void scheduleBlockUpdate(BlockPos pos) {
        this.scheduleChunkRenders3x3x3(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getY()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    private void scheduleChunkRenders3x3x3(int x, int y, int z) {
        this.scheduleChunkRenders(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    private void scheduleChunkRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            for (int chunkY = minY; chunkY <= maxY; chunkY++) {
                for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
                    ((WorldRendererAccessor) minecraftClient.worldRenderer).foer$scheduleChunkRender(chunkX, chunkY, chunkZ, false);
                }
            }
        }
    }

    public int calculateBobberLight(long blockPos, int lightLevel) {
        if(minecraftClient.player != null
                && Configs.rendererConfig.showLightBobber.get()
        ) {
            FishingBobberEntity bobber = minecraftClient.player.fishHook;
            if(bobber != null) {
                BlockPos pos = BlockPos.fromLong(blockPos);

                Triplet<Boolean, Double, Double> bobberInRange = calculateIfWithinDistance(pos, bobber);

                if (bobberInRange.value1()) return lightLevel;

                double factor = 1.0 - (bobberInRange.value2() / bobberInRange.value3());
                int dynamicLightLevel = (int)(getMaxLight() * factor);

                if(dynamicLightLevel > lightLevel) {
                    return dynamicLightLevel;
                }
            }
        }
        return lightLevel;
    }

    public int calculateBobberLight(BlockPos pos, int lightMap) {
        if(minecraftClient.player != null
                && Configs.rendererConfig.showLightBobber.get()
        ) {
            FishingBobberEntity bobber = minecraftClient.player.fishHook;
            if(bobber != null) {
                Triplet<Boolean, Double, Double> bobberInRange = calculateIfWithinDistance(pos, bobber);

                if (bobberInRange.value1()) return lightMap;

                double factor = 1.0 - (bobberInRange.value2() / bobberInRange.value3());
                int dynamicLightlevel = (int)(getMaxLight() * factor);

                if(dynamicLightlevel > 0) {
                    int blockLevel = LightmapTextureManager.getBlockLightCoordinates(lightMap);
                    if (dynamicLightlevel > blockLevel) {
                        int luminance = (int) (dynamicLightlevel * 16.0);
                        lightMap &= 0xfff00000;
                        lightMap |= luminance & 0x000fffff;
                    }
                }
            }
        }
        return lightMap;
    }

    private Triplet<Boolean, Double, Double> calculateIfWithinDistance(BlockPos pos, FishingBobberEntity bobber) {
        double dx = pos.getX() + 0.5 - bobber.getX();
        double dy = pos.getY() + 0.5 - bobber.getY();
        double dz = pos.getZ() + 0.5 - bobber.getZ();

        double distSq = dx * dx + dy * dy + dz * dz;
        double maxDistSq = getRadius() * getRadius();

        if (distSq > maxDistSq) return Triplet.of(true, distSq, maxDistSq);
        else return Triplet.of(false, distSq, maxDistSq);
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
