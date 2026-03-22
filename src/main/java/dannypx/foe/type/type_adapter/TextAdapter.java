package dannypx.foe.type.type_adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dannypx.foe.helper.TextHelper;
import net.minecraft.text.Text;

import java.io.IOException;

public class TextAdapter extends TypeAdapter<Text> {
    @Override
    public void write(JsonWriter writer, Text value) throws IOException {
        if(value == null || value.equals(Text.empty())) {
            return;
        }
        writer.value(TextHelper.textToJsonPretty(value));
    }

    @Override
    public Text read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String json = reader.nextString();
        return TextHelper.jsonToText(json);
    }
}
