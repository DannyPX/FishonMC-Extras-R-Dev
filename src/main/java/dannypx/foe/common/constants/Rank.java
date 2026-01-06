package dannypx.foe.common.constants;

import net.minecraft.text.Text;

import java.util.Optional;
import java.util.stream.Stream;

public enum Rank {
    ANGLER("angler", Text.literal("\uF032")),
    SAILOR("sailor", Text.literal("\uF031")),
    MARINER("mariner", Text.literal("\uF030")),
    CAPTAIN("captain", Text.literal("\uF029")),
    ADMIRAL("admiral", Text.literal("\uF028")),
    STAFF("staff", Text.literal("\uF024")),
    DESIGNER("designer", Text.literal("\uF026")),
    BUILDER("builder", Text.literal("\uF027")),
    MANAGER("manager", Text.literal("\uF023")),
    ADMIN("admin", Text.literal("\uF022")),
    OWNER("owner", Text.literal("\uF021")),
    COMMUNITY_MANAGER("communitymanager", Text.literal("\uF088")),
    FOE("foe", Text.literal("\uE00B")),
    DEFAULT("default", Text.empty());

    public final String ID;
    public final Text TAG;

    Rank(String id, Text tag) {
        this.ID = id;
        this.TAG = tag;
    }

    public static Rank valueOfId(String id) {
        for (Rank c : values()) {
            if (c.ID.equals(id.toLowerCase())) {
                return c;
            }
        }
        return DEFAULT;
    }

    public static Rank valueOfTagString(String value) {
        Optional<Rank> result = Stream.of(Rank.values()).filter(rank -> value.contains(rank.TAG.getString())).findFirst();
        return result.orElse(Rank.DEFAULT);
    }

    @Override
    public String toString() {
        return this.TAG.getString() + " " + this.ID;
    }
}
