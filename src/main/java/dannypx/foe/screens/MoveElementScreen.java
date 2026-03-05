package dannypx.foe.screens;

import dannypx.foe.common.handler.store.CustomHudDataHandler;
import dannypx.foe.common.type.Alignment;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.config.Configs;
import dannypx.foe.screens.element.Element;
import dannypx.foe.screens.element.hud.*;
import dannypx.foe.screens.widget.MovableBoxWidget;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class MoveElementScreen extends DefaultModScreen {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    List<Pair<String, Element>> customHudElements = new ArrayList<>();
    //endregion

    //region Methods
    public MoveElementScreen(Screen parent) {
        super(parent, Text.literal("Move Elements Screen"), true);
    }

    @Override
    protected void init() {
        super.init();
        this.assembleCustomHudElements();
        this.renderWidgets();
    }

    private void assembleCustomHudElements() {
        customHudElements.clear();
        CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList.forEach((key, hud) -> {
            customHudElements.add(Pair.of(key, new CustomHudElement(minecraftClient, hud, Text.literal(key))));
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        widgets.add(new MovableBoxWidget(minecraftClient,
                new ProfileElement(minecraftClient, true),
                Alignment.getTopCorners(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.profileElementXPosition.accept(xPercent);
                        Configs.hudConfig.profileElementYPosition.accept(yPercent);
                        Configs.hudConfig.profileElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showProfileElement.translationKey());
                    }
                }
        ));

        widgets.add(new MovableBoxWidget(minecraftClient,
                new LocationElement(minecraftClient, true),
                Alignment.getTopCorners(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.locationElementXPosition.accept(xPercent);
                        Configs.hudConfig.locationElementYPosition.accept(yPercent);
                        Configs.hudConfig.locationElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showLocationElement.translationKey());
                    }
                }
        ));

        widgets.add(new MovableBoxWidget(minecraftClient,
                new HotbarElement(minecraftClient, true),
                Alignment.getBottom(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.hotbarElementXPosition.accept(xPercent);
                        Configs.hudConfig.hotbarElementYPosition.accept(yPercent);
                        Configs.hudConfig.hotbarElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showHotbarElement.translationKey());
                    }
                }
        ));

        widgets.add(new MovableBoxWidget(minecraftClient,
                new PetElement(minecraftClient, true),
                Alignment.getTopCorners(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.petElementXPosition.accept(xPercent);
                        Configs.hudConfig.petElementYPosition.accept(yPercent);
                        Configs.hudConfig.petElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showPetElement.translationKey());
                    }
                }
        ));

        widgets.add(new MovableBoxWidget(minecraftClient,
                new NotifierElement(minecraftClient, true),
                Alignment.getCorners(),
                new MovableBoxWidget.Callback() {
                    @Override
                    public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                        Configs.hudConfig.notifierElementXPosition.accept(xPercent);
                        Configs.hudConfig.notifierElementYPosition.accept(yPercent);
                        Configs.hudConfig.notifierElementAlignment.accept(alignment);
                        Configs.hudConfig.save();
                    }

                    @Override
                    public void onConfig() {
                        ConfigApiJava.INSTANCE.openScreen(Configs.hudConfig.showNotifierElement.translationKey());
                    }
                }
        ));

        customHudElements.forEach(element -> {
            widgets.add(new MovableBoxWidget(minecraftClient,
                    element.value2(),
                    Alignment.getCorners(),
                    new MovableBoxWidget.Callback() {
                        @Override
                        public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                            CustomHudDataHandler.instance().updateHud(element.value1(), xPercent, yPercent, alignment);
                        }

                        @Override
                        public void onConfig() {
                            minecraftClient.setScreen(new CustomHudMakerScreen(minecraftClient.currentScreen));
                        }
                    }
            ));
        });

        if(Configs.debugConfig.debugMode.get()) {
            widgets.add(new MovableBoxWidget(minecraftClient,
                    new _DebugField(minecraftClient, true),
                    Alignment.getCorners(),
                    new MovableBoxWidget.Callback() {
                        @Override
                        public void onRelease(int xPercent, int yPercent, Alignment alignment) {
                            Configs.debugConfig.debugFieldXPosition.accept(xPercent);
                            Configs.debugConfig.debugFieldYPosition.accept(yPercent);
                            Configs.debugConfig.debugFieldAlignment.accept(alignment);
                            Configs.debugConfig.save();
                        }

                        @Override
                        public void onConfig() {
                            ConfigApiJava.INSTANCE.openScreen(Configs.debugConfig.debugFieldElement.translationKey());
                        }
                    }
            ));
        }



        widgets.forEach(this::addDrawableChild);
    }
    //endregion
}
