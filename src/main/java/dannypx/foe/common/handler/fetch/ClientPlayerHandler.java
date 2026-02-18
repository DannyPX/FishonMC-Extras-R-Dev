package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

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
