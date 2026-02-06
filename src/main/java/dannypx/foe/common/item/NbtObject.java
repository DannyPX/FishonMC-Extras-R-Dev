package dannypx.foe.common.item;

import com.mojang.serialization.DataResult;
import dannypx.foe.common.helper.ItemStackHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NbtObject {

    protected final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    public static final String ID = "id";
    public static final String CATCHER = "catcher";
    public static final String UUID = "uuid";
    public static final String COUNTER = "counter";
    public static final String TYPE = "type";
    public static final String RARITY = "rarity";

    public static final int RARITY_LINE = 1;
    public static final int RARITY_SIBLING = 1;

    protected final NbtCompound nbtCompound;
    protected final ItemStack itemStack;

    protected NbtObject() {
        this.nbtCompound = new NbtCompound();
        this.itemStack = ItemStack.EMPTY;
    }

    protected NbtObject(@NotNull NbtCompound nbtCompound, ItemStack itemStack) {
        this.nbtCompound = nbtCompound;
        this.itemStack = itemStack.copy();
    }

    public UUID getID() {
        return this.nbtCompound.getUuid(ID);
    }

    public UUID getPlayerUUID() {
        if(this.nbtCompound.contains(CATCHER)) {
            return this.nbtCompound.getUuid(CATCHER);
        } else if (this.nbtCompound.contains(UUID)) {
            return this.nbtCompound.getUuid(UUID);
        }
        return null;
    }

    //region Generic
    public Text getName() {
        return this.itemStack.getName();
    }

    public int getCount() {
        if(this.nbtCompound.contains(COUNTER)) {
            return this.nbtCompound.getInt(COUNTER);
        }
        return this.itemStack.getCount();
    }

    public boolean isOwn() {
        if(minecraftClient.player != null && getPlayerUUID() != null) {
            return minecraftClient.player.getUuid().equals(getPlayerUUID());
        }
        return false;
    }

    public String getType() {
        if(this.nbtCompound.contains(TYPE)) {
            return this.nbtCompound.getString(TYPE);
        }
        return null;
    }

    public String getRarity() {
        if(this.nbtCompound.contains(RARITY)) {
            return this.nbtCompound.getString(RARITY);
        }
        return null;
    }

    public Text getRarityText() {
        if(this.itemStack.get(DataComponentTypes.LORE) != null) {
            List<Text> textList = this.getLore();
            return textList.get(RARITY_LINE).getSiblings().get(RARITY_SIBLING);
        }
        return Text.empty();
    }

    public List<Text> getLore() {
        if(this.itemStack.get(DataComponentTypes.LORE) != null) {
            return Objects.requireNonNull(this.itemStack.get(DataComponentTypes.LORE)).lines();
        }
        return List.of();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    protected List<NbtObject> getItemStackList(String key) {
        if(this.nbtCompound.contains(key)) {
            DataResult<List<ItemStack>> result =
                    ItemStack.CODEC.listOf().parse(NbtOps.INSTANCE, this.nbtCompound.get(key));
            List<ItemStack> itemStackList = result.result().orElse(List.of());

            return itemStackList.stream().map(item -> {
                Pair<Boolean, NbtObject> validatedItem = ValidateItem.isType(item);
                return validatedItem.v2();
            }).filter(Objects::nonNull).toList();
        }
        return List.of();
    }
    //endregion

    public static NbtObject of(@NotNull NbtCompound nbtCompound, @NotNull ItemStack itemStack) {
        return new NbtObject(nbtCompound, itemStack);
    }

    public static NbtObject empty() {
        return new NbtObject(ItemStackHelper.getNbt(ItemStack.EMPTY), ItemStack.EMPTY);
    }
}
