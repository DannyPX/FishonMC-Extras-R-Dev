package dannypx.foe.type.type_adapter;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import dannypx.foe.handler.store.CustomTimerDataHandler;

import java.io.IOException;

public class CustomTimerAdapter extends TypeAdapter<CustomTimerDataHandler.CustomTimer> {
    @Override
    public void write(JsonWriter writer, CustomTimerDataHandler.CustomTimer value) throws IOException {
        if(value == null) {
            return;
        }
        writer.value(new GsonBuilder().create().toJson(value));
    }

    @Override
    public CustomTimerDataHandler.CustomTimer read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String json = reader.nextString();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        if(jsonObject.get("isPeriod").getAsBoolean()) {
            return new GsonBuilder().create().fromJson(json, CustomTimerDataHandler.CustomTimerPeriod.class);
        } else {
            return new GsonBuilder().create().fromJson(json, CustomTimerDataHandler.CustomTimer.class);
        }
    }
}
