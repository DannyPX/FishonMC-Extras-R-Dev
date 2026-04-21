package dannypx.foe.handler.fetch;

import com.mojang.authlib.GameProfile;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.mixin.accessor.MinecraftClientAccessor;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.network.ClientPlayerProfileResolver;
import net.minecraft.server.GameProfileResolver;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameProfileHandler extends Handler {
    private static GameProfileHandler INSTANCE = new GameProfileHandler();

    public static GameProfileHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new GameProfileHandler();
        }
        return INSTANCE;
    }

    public GameProfileHandler() {
        gameProfileResolver = new ClientPlayerProfileResolver(minecraftClient, ((MinecraftClientAccessor) minecraftClient).getApiServices().profileResolver());
    }

    //region Fields
    private final Map<UUID, String> nameCache = new HashMap<>();
    private final Map<UUID, Boolean> fetchingUUIDs = new HashMap<>();
    private final GameProfileResolver gameProfileResolver;
    //endregion

    //region Methods
    public String getUsername(UUID uuid) {
        String cached = nameCache.get(uuid);
        if (cached != null) {
            return cached;
        }

        if (!fetchingUUIDs.containsKey(uuid)) {
            LoggerHandler._debug("Fetching: " + uuid.toString());

            fetchingUUIDs.put(uuid, true);

            gameProfileResolver.getProfileById(uuid).ifPresentOrElse(profile ->
                    {
                        nameCache.put(uuid, profile.name());
                        fetchingUUIDs.remove(uuid);
                    },
                    () -> {}
            );
        }

        return null;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
