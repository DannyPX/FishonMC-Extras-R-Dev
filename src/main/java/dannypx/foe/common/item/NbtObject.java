package dannypx.foe.common.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class NbtObject {

    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    private static final String UUID = "id";
    private static final String CATCHER = "catcher";
    private final NbtCompound nbtCompound;

    private NbtObject(NbtCompound nbtCompound) {
        this.nbtCompound = nbtCompound;
    }

    public UUID getUUID() {
        return this.nbtCompound.getUuid(UUID);
    }

    public UUID getPlayerUUID() {
        if(this.nbtCompound.contains(CATCHER)) {
            return this.nbtCompound.getUuid(CATCHER);
        }
        return null;
    }

    public boolean isOwn() {
        if(minecraftClient.player != null && getPlayerUUID() != null) {
            return minecraftClient.player.getUuid().equals(getPlayerUUID());
        }
        return false;
    }

    public static NbtObject of(NbtCompound nbtCompound) {
        return new NbtObject(nbtCompound);
    }

    //{
    //    date: "01/15/2026",
    //    nature: "jolly",
    //    rod: "§fStandard Fishing Rod",
    //    scientific: "Pomoxis annularis",
    //    sex: "♂",
    //    length: 5.46f,
    //    catcher: [I; -2053775741, -1988999195, -1691904089, 957736602],
    //    weight: 0.06855662f,
    //    water: "freshwater",
    //    lifestyle: "demersal",
    //    conservation: "LC",
    //    size: "juvenile",
    //    native: "native",
    //    fish: "whitecrappie",
    //    xp: 50.0f,
    //    migration: "non-migratory",
    //    variant: "normal",
    //    location: "spawn",
    //    id: [I; 1577787886, -2043916372, -1960242947, 415098906],
    //    value: 50.0f,
    //    group: "panfishes",
    //    rarity: "rare"
    //}
}
