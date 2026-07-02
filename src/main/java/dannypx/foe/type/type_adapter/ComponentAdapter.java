package dannypx.foe.type.type_adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dannypx.foe.helper.TextHelper;
import java.io.IOException;
import net.minecraft.network.chat.Component;

public class ComponentAdapter extends TypeAdapter<Component> {
    @Override
    public void write(JsonWriter writer, Component value) throws IOException {
        if(value == null || value.equals(Component.empty())) {
            return;
        }
        writer.value(TextHelper.componentToJsonPretty(value));
    }

    @Override
    public Component read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String json = reader.nextString();
        return TextHelper.jsonToComponent(json);
    }
}
