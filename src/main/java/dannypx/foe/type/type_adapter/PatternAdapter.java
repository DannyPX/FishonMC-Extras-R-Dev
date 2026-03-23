package dannypx.foe.type.type_adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.regex.Pattern;

public class PatternAdapter extends TypeAdapter<Pattern> {
    @Override
    public void write(JsonWriter writer, Pattern value) throws IOException {
        if(value == null) {
            return;
        }
        writer.value(value.pattern());
    }

    @Override
    public Pattern read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String json = reader.nextString();
        return Pattern.compile(json);
    }
}
