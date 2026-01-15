package dannypx.foe.screens;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.type.Alignment;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.LocationElement;
import dannypx.foe.screens.element.ProfileElement;
import dannypx.foe.screens.widget.MovableBoxWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MoveElementScreen extends Screen {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final Screen parent;
    //endregion

    //region Methods
    public MoveElementScreen(Screen parent) {
        super(Text.literal("Screen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.renderWidgets();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        widgets.add(new MovableBoxWidget(minecraftClient,
                new ProfileElement(minecraftClient, true),
                Alignment.getHorizontal(),
                Configs.hudConfig.showProfileElement.translationKey(),
                (xPercent, yPercent, alignment) -> {
                    Configs.hudConfig.profileElementXPosition.accept(xPercent);
                    Configs.hudConfig.profileElementYPosition.accept(yPercent);
                    Configs.hudConfig.profileElementAlignment.accept(alignment);
                    Configs.hudConfig.save();
        }));

        widgets.add(new MovableBoxWidget(minecraftClient,
                new LocationElement(minecraftClient, true),
                Alignment.getHorizontal(),
                Configs.hudConfig.showLocationElement.translationKey(),
                (xPercent, yPercent, alignment) -> {
                    Configs.hudConfig.locationElementXPosition.accept(xPercent);
                    Configs.hudConfig.locationElementYPosition.accept(yPercent);
                    Configs.hudConfig.locationElementAlignment.accept(alignment);
                    Configs.hudConfig.save();
                }));

        widgets.forEach(this::addDrawableChild);
    }

    @Override
    public void close() {
        this.minecraftClient.setScreen(parent);
    }

    //endregion
}
