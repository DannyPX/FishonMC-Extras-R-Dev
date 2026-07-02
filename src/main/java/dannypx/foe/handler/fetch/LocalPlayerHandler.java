package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public class LocalPlayerHandler extends Handler {
    private static LocalPlayerHandler INSTANCE = new LocalPlayerHandler();

    public static LocalPlayerHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalPlayerHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private Component name = Component.empty();
    private int experienceLevel = 0;
    private float experienceProgress = 0f;

    public Component getName() {
        return name;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public float getExperienceProgress() {
        return experienceProgress;
    }

    public Pair<Boolean, PlaceholderValue> getClientPlayer(String[] params) {
        if(params.length > 0
                && minecraft.player != null
        ) {
            Pattern fieldPattern = Pattern.compile("^(name|level|level_progress|pos|yaw|pitch|direction|fps)$");

            if(fieldPattern.matcher(params[0]).matches()
            ) {
                return switch(params[0]) {
                    case "name" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(getName().getString()));
                    case "level" -> PlaceholderHandler.getPlaceholderValue(StringValue.valueOf(getExperienceLevel()));
                    case "level_progress" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getExperienceProgress() * 100, 2)));
                    case "pos" -> {
                        if(params.length > 1) {
                            switch(params[1]) {
                                case "x" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString((float) minecraft.player.position().x, 0)));
                                case "y" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString((float) minecraft.player.position().y, 0)));
                                case "z" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString((float) minecraft.player.position().z, 0)));
                                default -> PlaceholderHandler.noResult();
                            }
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "yaw" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(Mth.wrapDegrees(minecraft.player.getYRot()), 1)));
                    case "pitch" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(minecraft.player.getXRot(), 1)));
                    case "direction" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.capitalize(minecraft.player.getDirection().name())));
                    case "fps" -> PlaceholderHandler.getPlaceholderValue(StringValue.valueOf(minecraft.getFps()));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        LocalPlayer localPlayer = minecraft.player;
        if(localPlayer != null) {
            fetchFromLocalPlayer(localPlayer);
        }
    }

    private void fetchFromLocalPlayer(LocalPlayer localPlayer) {
        this.name = localPlayer.getName();
        this.experienceLevel = localPlayer.experienceLevel;
        this.experienceProgress = localPlayer.experienceProgress;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "name", Pair.of(getName().copy(), Component.empty()),
                "experienceLevel", Pair.of(TextHelper.literal(getExperienceLevel()), Component.empty()),
                "experienceProgress", Pair.of(TextHelper.literal(getExperienceProgress()), Component.empty())
        );
    }
    //endregion
}
