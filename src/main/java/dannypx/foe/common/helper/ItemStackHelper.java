package dannypx.foe.common.helper;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.function.UnaryOperator;

public class ItemStackHelper {
    private static final GsonBuilder gson = new GsonBuilder();

    public static NbtCompound getNbt(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        return component != null ? component.copyNbt() : new NbtCompound();
    }

    public static ItemStack jsonToItemStack(String json) {
        return ItemStack.CODEC
                .decode(JsonOps.INSTANCE, gson.create().fromJson(json, JsonElement.class))
                .mapOrElse((Pair::getFirst), (pairError -> Items.STICK.getDefaultStack()));
    }

    public static String itemStackToJson(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            return gson.setPrettyPrinting().create().toJson(ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, itemStack).getOrThrow());
        }
        return "{}";
    }

    public static String itemStackListToJson(List<ItemStack> itemStacks) {
        List<ItemStack> stacksToSerialize = itemStacks.stream().filter(stack -> !stack.isEmpty()).toList();
        return gson.setPrettyPrinting().create().toJson(ItemStack.CODEC.listOf().encodeStart(JsonOps.INSTANCE, stacksToSerialize).getOrThrow());
    }

    public static <T> DefaultedList<T> deepCopy(
            DefaultedList<T> original,
            T defaultValue,
            UnaryOperator<T> copier
    ) {
        DefaultedList<T> copy =
                DefaultedList.ofSize(original.size(), defaultValue);

        for (int i = 0; i < original.size(); i++) {
            copy.set(i, copier.apply(original.get(i)));
        }
        return copy;
    }
}
