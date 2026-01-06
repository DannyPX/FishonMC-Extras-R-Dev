package dannypx.foe.common.constants;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum Rarity {
    COMMON("common", Text.literal("\uF033")),
    RARE("rare", Text.literal("\uF034")),
    EPIC("epic", Text.literal("\uF035")),
    LEGENDARY("legendary", Text.literal("\uF036")),
    MYTHICAL("mythical", Text.literal("\uF037")),
    SPECIAL("special", Text.literal("\uF092")),
    DEFAULT("default", Text.empty());

    public final String ID;
    public final Text TAG;

    Rarity(String id, Text tag) {
        this.ID = id;
        this.TAG = tag;
    }

    public static Rarity valueOfId(String id) {
        for (Rarity c : values()) {
            if (c.ID.equals(id.toLowerCase())) {
                return c;
            }
        }
        return DEFAULT;
    }
}
