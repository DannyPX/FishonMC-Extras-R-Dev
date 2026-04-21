package dannypx.foe.type;

import net.minecraft.client.option.KeyBinding;

public class AdvancedKeyBinding extends KeyBinding {
    public AdvancedKeyBinding(String id, int code, KeyBinding.Category category) {
        super(id, code, category);
    }

    public void onPressed(Runnable runTrue) {
        while (this.wasPressed()) {
            runTrue.run();
        }
    }
}
