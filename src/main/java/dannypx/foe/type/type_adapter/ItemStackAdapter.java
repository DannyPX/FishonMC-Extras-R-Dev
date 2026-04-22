package dannypx.foe.type.type_adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dannypx.foe.helper.ItemStackHelper;
import java.io.IOException;
import net.minecraft.world.item.ItemStack;

public class ItemStackAdapter extends TypeAdapter<ItemStack> {
    @Override
    public void write(JsonWriter writer, ItemStack value) throws IOException {
        if(value == null || value == ItemStack.EMPTY) {
            return;
        }
        writer.value(ItemStackHelper.itemStackToJson(value));
    }

    @Override
    public ItemStack read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String json = reader.nextString();
        return ItemStackHelper.jsonToItemStack(json);
    }
}
