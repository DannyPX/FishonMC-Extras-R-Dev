package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.logic.PlaceholderHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.common.type.custom_text.CustomTextValue;
import dannypx.foe.common.type.custom_text.StringValue;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.regex.Pattern;

public class ClientPlayerHandler extends Handler {
    private static ClientPlayerHandler INSTANCE = new ClientPlayerHandler();

    public static ClientPlayerHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientPlayerHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private Text name = Text.empty();
    private int experienceLevel = 0;
    private float experienceProgress = 0f;

    public Text getName() {
        return name;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public float getExperienceProgress() {
        return experienceProgress;
    }

    public Pair<Boolean, CustomTextValue> getClientPlayer(String[] params) {
        if(params.length > 0
                && minecraftClient.player != null
        ) {
            Pattern fieldPattern = Pattern.compile("^(name|level|level_progress|pos|yaw|pitch|direction)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length >= 1
            ) {
                return switch(params[0]) {
                    case "name" -> PlaceholderHandler.getTextValue(new StringValue(getName().getString()));
                    case "level" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(getExperienceLevel())));
                    case "level_progress" -> PlaceholderHandler.getTextValue(new StringValue(TextHelper.floatToString(getExperienceProgress() * 100, 2)));
                    case "pos" -> switch(params[1]) {
                        case "x" -> PlaceholderHandler.getTextValue(new StringValue(TextHelper.floatToString((float) minecraftClient.player.getPos().x, 0)));
                        case "y" -> PlaceholderHandler.getTextValue(new StringValue(TextHelper.floatToString((float) minecraftClient.player.getPos().y, 0)));
                        case "z" -> PlaceholderHandler.getTextValue(new StringValue(TextHelper.floatToString((float) minecraftClient.player.getPos().z, 0)));
                        default -> PlaceholderHandler.noResult();
                    };
                    case "yaw" -> PlaceholderHandler.getTextValue(new StringValue(TextHelper.floatToString(MathHelper.wrapDegrees(minecraftClient.player.getYaw()), 1)));
                    case "pitch" -> PlaceholderHandler.getTextValue(new StringValue(TextHelper.floatToString(minecraftClient.player.getPitch(), 1)));
                    case "direction" -> PlaceholderHandler.getTextValue(new StringValue(TextHelper.capitalize(minecraftClient.player.getHorizontalFacing().getName())));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        if(clientPlayerEntity != null) {
            fetchFromClientPlayer(clientPlayerEntity);
        }
    }

    private void fetchFromClientPlayer(ClientPlayerEntity player) {
        this.name = player.getName();
        this.experienceLevel = player.experienceLevel;
        this.experienceProgress = player.experienceProgress;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "name", Pair.of(getName().copy(), Text.empty()),
                "experienceLevel", Pair.of(TextHelper.literal(getExperienceLevel()), Text.empty()),
                "experienceProgress", Pair.of(TextHelper.literal(getExperienceProgress()), Text.empty())
        );
    }
    //endregion
}
