package dannypx.foe.common.type;

import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;

import java.util.List;

public enum Alignment implements EnumTranslatable {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;

    public static List<Alignment> getHorizontal() {
        return List.of(LEFT, RIGHT);
    }
}
