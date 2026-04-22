package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.mixin.accessor.MinecraftAccessor;
import dannypx.foe.type.tuple.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.player.LocalPlayerResolver;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.players.ProfileResolver;

public class ProfileHandler extends Handler {
    private static ProfileHandler INSTANCE = new ProfileHandler();

    public static ProfileHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new ProfileHandler();
        }
        return INSTANCE;
    }

    public ProfileHandler() {
        profileResolver = new LocalPlayerResolver(minecraft, ((MinecraftAccessor) minecraft).getServices().profileResolver());
    }

    //region Fields
    private final Map<UUID, String> nameCache = new HashMap<>();
    private final Map<UUID, Boolean> fetchingUUIDs = new HashMap<>();
    private final ProfileResolver profileResolver;
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

            profileResolver.fetchById(uuid).ifPresentOrElse(profile ->
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
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "key", Pair.of(Component.literal("value"), Component.empty())
        );
    }
    //endregion
}
