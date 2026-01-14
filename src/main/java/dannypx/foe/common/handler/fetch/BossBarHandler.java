package dannypx.foe.common.handler.fetch;

import com.google.gson.*;
import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import dannypx.foe.mixin.accessor.BossBarHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.*;

public class BossBarHandler {
    private static BossBarHandler INSTANCE = new BossBarHandler();

    public static BossBarHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new BossBarHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private MutableText location = Text.empty();
    private MutableText weather = Text.empty();
    private MutableText time = Text.empty();
    private MutableText temperature = Text.empty();
    private MutableText subLocation = Text.empty();
    private String prevBossbar = "";

    public MutableText getLocation() {
        return location;
    }

    public MutableText getWeather() {
        return weather;
    }

    public MutableText getTime() {
        return time;
    }

    public MutableText getTemperature() {
        return temperature;
    }

    public MutableText getSubLocation() {
        return subLocation;
    }
    //endregion

    //region Methods
    public void tick() {
        this.fetchFromBossBar();
    }

    private void fetchFromBossBar() {
        Map<UUID, ClientBossBar> bossBars = ((BossBarHudAccessor) (minecraftClient.inGameHud.getBossBarHud())).getBossBars();
        if(!bossBars.isEmpty()) {
            bossBars.forEach(((uuid, clientBossBar) -> {
                if(clientBossBar.getName().getString().contains("\uF039") && !Objects.equals(prevBossbar, clientBossBar.getName().getString())) {
                    prevBossbar = clientBossBar.getName().getString();
                    JsonObject jsonObject = JsonParser.parseString(TextHelper.textToJson(clientBossBar.getName())).getAsJsonObject();

                    if(jsonObject.get("extra") != null) {
                        JsonObject locationObject = jsonObject.get("extra").getAsJsonArray().get(0).getAsJsonObject()
                                .get("extra").getAsJsonArray().get(0).getAsJsonObject();
                        location = Text.literal(locationObject.get("text").getAsString().substring(4))
                                .withColor(TextColor.parse(locationObject.get("color").getAsString()).getOrThrow().getRgb());

                        JsonObject weatherObject = jsonObject.get("extra").getAsJsonArray().get(2).getAsJsonObject()
                                .get("extra").getAsJsonArray().get(0).getAsJsonObject();
                        weather = Text.literal(weatherObject.get("text").getAsString())
                                .withColor(TextColor.parse(weatherObject.get("color").getAsString()).getOrThrow().getRgb());

                        if(weatherObject.get("extra") != null) {
                            JsonObject timeObject = weatherObject.get("extra").getAsJsonArray().get(0).getAsJsonObject();
                            time = Text.literal(timeObject.get("text").getAsString().trim())
                                    .withColor(TextColor.parse(timeObject.get("color").getAsString()).getOrThrow().getRgb());

                            if(timeObject.get("extra") != null) {
                                JsonObject temperatureObject = timeObject.get("extra").getAsJsonArray().get(0).getAsJsonObject();
                                temperature = Text.literal(temperatureObject.get("text").getAsString().trim())
                                        .withColor(TextColor.parse(temperatureObject.get("color").getAsString()).getOrThrow().getRgb());
                            }
                        }
                    }
                } else if(clientBossBar.getName().getString().contains("\uA201\uEEE1\uA208")) {
                    JsonObject jsonObject = JsonParser.parseString(TextHelper.textToJson(clientBossBar.getName())).getAsJsonObject();

                    if(jsonObject.get("extra") != null) {
                        JsonObject locationObject = jsonObject.get("extra").getAsJsonArray().get(0).getAsJsonObject();
                        subLocation = Text.literal(locationObject.get("text").getAsString())
                                .withColor(TextColor.parse(locationObject.get("color").getAsString()).getOrThrow().getRgb());
                    }
                } else {
                    subLocation = Text.empty();
                }
            }));
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, Tooltip>> _getFields() {
        return Map.of(
                "location", Pair.of(getLocation(), null),
                "weather", Pair.of(getWeather(), null),
                "time", Pair.of(getTime(), null),
                "temperature", Pair.of(getTemperature(), null),
                "subLocation", Pair.of(getSubLocation(), null)
        );
    }
    //endregion
}
