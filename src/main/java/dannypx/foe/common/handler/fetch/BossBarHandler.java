package dannypx.foe.common.handler.fetch;

import com.google.gson.*;
import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.logic.PlaceholderHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.custom_text.CustomTextValue;
import dannypx.foe.common.type.custom_text.StringValue;
import dannypx.foe.common.type.custom_text.TextValue;
import dannypx.foe.mixin.accessor.BossBarHudAccessor;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.*;
import java.util.regex.Pattern;

public class BossBarHandler extends Handler {
    private static BossBarHandler INSTANCE = new BossBarHandler();

    public static BossBarHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new BossBarHandler();
        }
        return INSTANCE;
    }

    //region Fields
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

    public Pair<Boolean, CustomTextValue> getBossBar(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(location|weather|time|temperature|sub_location)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "location" -> PlaceholderHandler.getTextValue(new TextValue(getLocation()));
                    case "weather" -> PlaceholderHandler.getTextValue(new TextValue(getWeather()));
                    case "time" -> PlaceholderHandler.getTextValue(new TextValue(getTime()));
                    case "temperature" -> PlaceholderHandler.getTextValue(new TextValue(getTemperature()));
                    case "sub_location" -> PlaceholderHandler.getTextValue(new TextValue(getSubLocation()), true);
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
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
                    String json = TextHelper.textToJson(clientBossBar.getName());
                    JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

                    if(jsonObject.get("extra") != null) {
                        JsonObject locationObject = jsonObject.get("extra").getAsJsonArray().get(0).getAsJsonObject()
                                .get("extra").getAsJsonArray().get(0).getAsJsonObject();
                        String locationString = locationObject.get("text").getAsString().substring(4).trim();
                        if(locationString.contains("(")) {
                            locationString = locationString.substring(0, locationString.indexOf("(") - 1);
                        }

                        location = Text.literal(locationString)
                                .withColor(TextColor.parse(locationObject.get("color").getAsString()).getOrThrow().getRgb());

                        JsonObject weatherObject = jsonObject.get("extra").getAsJsonArray().get(2).getAsJsonObject()
                                .get("extra").getAsJsonArray().get(0).getAsJsonObject();
                        weather = Text.literal(weatherObject.get("text").getAsString())
                                .withColor(TextColor.parse(weatherObject.get("color").getAsString()).getOrThrow().getRgb());

                        if(weather.getString().trim().length() != 1) {
                            weather = Text.literal(weatherObject.get("text").getAsString().substring(0, 1))
                                    .withColor(TextColor.parse(weatherObject.get("color").getAsString()).getOrThrow().getRgb());

                            time = Text.literal(weatherObject.get("text").getAsString().substring(1).trim())
                                    .withColor(TextColor.parse(weatherObject.get("color").getAsString()).getOrThrow().getRgb());

                            if(weatherObject.get("extra") != null) {
                                JsonObject temperatureObject = weatherObject.get("extra").getAsJsonArray().get(0).getAsJsonObject();
                                temperature = Text.literal(temperatureObject.get("text").getAsString().trim())
                                        .withColor(TextColor.parse(temperatureObject.get("color").getAsString()).getOrThrow().getRgb());
                            }
                            return;
                        }

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
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "location", Pair.of(getLocation(), Text.empty()),
                "weather", Pair.of(getWeather(), Text.empty()),
                "time", Pair.of(getTime(), Text.empty()),
                "temperature", Pair.of(getTemperature(), Text.empty()),
                "subLocation", Pair.of(getSubLocation(), Text.empty())
        );
    }
    //endregion
}
