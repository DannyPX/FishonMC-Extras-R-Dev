package dannypx.foe.screens;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.handler.logic.LoggerHandler;
import dannypx.foe.common.handler.store.CustomHudDataHandler;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.type.tuple.Triplet;
import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.screens.widget.ButtonListWidget;
import dannypx.foe.screens.widget.EditFieldListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.util.*;

public class CustomHudMakerScreen extends Screen implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final Screen parentScreen;

    private ButtonListWidget hudList;
    private EditFieldListWidget editFieldListWidget;
    private Map<String, ButtonListWidget.ButtonEntry> buttonEntryMap = new HashMap<>();
    private String selectedHud;
    //endregion

    //region Methods
    public CustomHudMakerScreen(Screen parent) {
        super(Text.literal("Custom HUD Maker Screen"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.renderWidgets();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.hudList.render(context, mouseX, mouseY, delta);
        this.editFieldListWidget.render(context, mouseX, mouseY, delta);
    }

    private void renderWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        widgets.add(this.saveBackButton());
        widgets.add(this.backButton());
        widgets.add(this.addLine());

        widgets.add(getEditHudWidget());
        widgets.add(getHudList());

        widgets.add(getNewHudElementButton());
        widgets.add(getDeleteHudElementButton());
        widgets.add(getImportButton());
        widgets.add(getExportButton());

        widgets.forEach(this::addDrawableChild);
    }

    private ClickableWidget getEditHudWidget() {
        editFieldListWidget = new EditFieldListWidget(
                (BUTTON_WIDTH + PADDING * 2),
                0,
                width - (BUTTON_WIDTH + PADDING * 2),
                height - (BUTTON_HEIGHT + PADDING_HALF) - 3,
                Text.literal("No Hud Selected")
        );

        return editFieldListWidget;
    }

    private ClickableWidget getNewHudElementButton() {
        return ButtonWidget.builder(
                        Text.literal("Create HUD"),
                        (button) -> {
                            String id = "Custom Hud #" + UUID.randomUUID();

                            CustomHudDataHandler.instance().createNewCustomHud(id);

                            ButtonListWidget.ButtonEntry buttonEntry = createHudEntry(id);

                            hudList.addEntry(buttonEntry);
                            buttonEntryMap.put(id, buttonEntry);
                        })
                .size(BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT)
                .position(PADDING_HALF, minecraftClient.getWindow().getScaledHeight() - PADDING_HALF - BUTTON_HEIGHT)
                .build();
    }

    private ClickableWidget getDeleteHudElementButton() {
        return ButtonWidget.builder(
                        Text.literal("Delete Selected HUD"),
                        (button) -> {
                            if(editFieldListWidget.hasSelectedOption) {
                                CustomHudDataHandler.instance().deleteCustomHud(selectedHud);
                                editFieldListWidget.reset();
                                ButtonListWidget.ButtonEntry entry = buttonEntryMap.get(selectedHud);

                                hudList.removeEntry(entry);
                                buttonEntryMap.remove(selectedHud);

                                selectedHud = null;
                            }
                        })
                .size(BUTTON_WIDTH / 2 - PADDING_HALF, BUTTON_HEIGHT)
                .position(PADDING + (BUTTON_WIDTH / 2 - PADDING_HALF), minecraftClient.getWindow().getScaledHeight() - PADDING_HALF - BUTTON_HEIGHT)
                .build();
    }

    private ClickableWidget getImportButton() {
        return ButtonWidget.builder(
                        Text.literal("Import HUD"),
                        (button) -> {
                            String rawData = minecraftClient.keyboard.getClipboard();
                            try {
                                String json = TextHelper.decompress(Base64.getDecoder().decode(rawData));

                                Gson gson = new GsonBuilder().create();
                                Triplet<String, CustomHudDataHandler.CustomHud, Integer> data = gson.fromJson(json, TypeToken.getParameterized(Triplet.class, String.class, CustomHudDataHandler.CustomHud.class, Integer.class).getType());

                                if(data.value3() > FishOnMCExtras.HUD_VERSION) {
                                    SystemToast.add(minecraftClient.getToastManager(),
                                            SystemToast.Type.PERIODIC_NOTIFICATION,
                                            Text.literal("Fish On Extras Rebirth"),
                                            Text.literal("Could not Import. Imported HUD is made on a newer version"));

                                    return;
                                }

                                if(CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList.containsKey(data.value1())) {
                                    data = Triplet.of(data.value1() + " (Duplicate)", data.value2(), data.value3());
                                }

                                String id = data.value1();

                                CustomHudDataHandler.instance().createNewCustomHud(id, data.value2());

                                ButtonListWidget.ButtonEntry buttonEntry = createHudEntry(id);

                                hudList.addEntry(buttonEntry);
                                buttonEntryMap.put(id, buttonEntry);

                                SystemToast.add(minecraftClient.getToastManager(),
                                        SystemToast.Type.PERIODIC_NOTIFICATION,
                                        Text.literal("Fish On Extras Rebirth"),
                                        Text.literal("Imported HUD"));

                            } catch (Exception e) {
                                LoggerHandler.error(e);

                                SystemToast.add(minecraftClient.getToastManager(),
                                        SystemToast.Type.PERIODIC_NOTIFICATION,
                                        Text.literal("Fish On Extras Rebirth"),
                                        Text.literal("Could not Import. Data invalid"));
                            }
                        })
                .size(BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT)
                .position(PADDING_HALF, minecraftClient.getWindow().getScaledHeight() - PADDING_HALF - BUTTON_HEIGHT * 2 - PADDING_HALF)
                .tooltip(Tooltip.of(Text.literal("Imports from the code on your clipboard")))
                .build();
    }

    private ClickableWidget getExportButton() {
        return ButtonWidget.builder(
                        Text.literal("Export Selected HUD"),
                        (button) -> {
                            if(editFieldListWidget.hasSelectedOption) {
                                try {
                                    Triplet<String, CustomHudDataHandler.CustomHud, Integer> dataHud = Triplet.of(
                                            editFieldListWidget.currentSelectedHud,
                                            CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList.get(editFieldListWidget.currentSelectedHud),
                                            FishOnMCExtras.HUD_VERSION
                                    );

                                    String rawData = Base64.getEncoder().encodeToString(
                                            TextHelper.compress(new GsonBuilder().create().toJson(dataHud))
                                    );

                                    minecraftClient.keyboard.setClipboard(rawData);

                                    SystemToast.add(minecraftClient.getToastManager(),
                                            SystemToast.Type.PERIODIC_NOTIFICATION,
                                            Text.literal("Fish On Extras Rebirth"),
                                            Text.literal("Exported HUD on your clipboard"));
                                } catch (Exception e) {
                                    LoggerHandler.error(e);

                                    SystemToast.add(minecraftClient.getToastManager(),
                                            SystemToast.Type.PERIODIC_NOTIFICATION,
                                            Text.literal("Fish On Extras Rebirth"),
                                            Text.literal("An error has occurred"));
                                }
                            }

                        })
                .size(BUTTON_WIDTH / 2 - PADDING_HALF, BUTTON_HEIGHT)
                .position(PADDING + (BUTTON_WIDTH / 2 - PADDING_HALF), minecraftClient.getWindow().getScaledHeight() - PADDING_HALF - BUTTON_HEIGHT * 2 - PADDING_HALF)
                .tooltip(Tooltip.of(Text.literal("Save first before exporting")))
                .build();
    }

    private ClickableWidget getHudList() {
        hudList = new ButtonListWidget(
                client,
                (BUTTON_WIDTH + PADDING * 2),
                height - ScreenConstants.BUTTON_HEIGHT * 2 - PADDING - PADDING_HALF,
                0,
                BUTTON_HEIGHT + PADDING_HALF,
                BUTTON_HEIGHT,
                "Custom HUDs"
        );

        CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList.forEach((id, ignored) -> {
            ButtonListWidget.ButtonEntry buttonEntry = createHudEntry(id);

            hudList.addEntry(buttonEntry);
            buttonEntryMap.put(id, buttonEntry);
        });

        return hudList;
    }

    private ButtonListWidget.ButtonEntry createHudEntry(String id) {
        return new ButtonListWidget.ButtonEntry(
                ButtonWidget.builder(
                        Text.literal(id),
                        button -> {
                            selectedHud = id;
                            editFieldListWidget.selectHud(
                                    id,
                                    CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList.get(id));
                        }
                ).width(BUTTON_WIDTH).build()
        );
    }

    private ButtonWidget saveBackButton() {
        return ButtonWidget.builder(Text.literal("Save and Return"), button -> {
                    if(editFieldListWidget.hasSelectedOption) {
                        if(editFieldListWidget.newName.isBlank()) {
                            SystemToast.add(minecraftClient.getToastManager(),
                                    SystemToast.Type.PERIODIC_NOTIFICATION,
                                    Text.literal("Fish On Extras Rebirth"),
                                    Text.literal("HUD name is empty"));

                            return;
                        }

                        if(
                                !Objects.equals(editFieldListWidget.currentSelectedHud, editFieldListWidget.newName)
                                && CustomHudDataHandler.instance().getCustomHudData().customHudRawDataList.containsKey(editFieldListWidget.newName)
                        ) {
                            SystemToast.add(minecraftClient.getToastManager(),
                                    SystemToast.Type.PERIODIC_NOTIFICATION,
                                    Text.literal("Fish On Extras Rebirth"),
                                    Text.literal("HUD name already exist"));

                            return;
                        }

                        CustomHudDataHandler.instance().updateHud(
                                editFieldListWidget.currentSelectedHud,
                                editFieldListWidget.newName,
                                editFieldListWidget.scale,
                                editFieldListWidget.showBackground,
                                editFieldListWidget.showElement,
                                editFieldListWidget.getEntries()
                                        .stream()
                                        .map(lineEntry -> Triplet.of(lineEntry.lineString, lineEntry.isCentre, lineEntry.isSmall))
                                        .toList());
                    }
                    this.close();
                })
                .position(width - PADDING_HALF - BUTTON_WIDTH / 2, height - PADDING_HALF - BUTTON_HEIGHT)
                .size(BUTTON_WIDTH / 2, BUTTON_HEIGHT)
                .build();
    }

    private ButtonWidget backButton() {
        return ButtonWidget.builder(Text.literal("Return"), button ->
                    this.close())
                .position(width - (PADDING_HALF + BUTTON_WIDTH / 2) * 2, height - PADDING_HALF - BUTTON_HEIGHT)
                .size(BUTTON_WIDTH / 2, BUTTON_HEIGHT)
                .build();
    }

    private ButtonWidget addLine() {
        return ButtonWidget.builder(Text.literal("Add Line"), button -> {
                    if(editFieldListWidget.hasSelectedOption) {
                        editFieldListWidget.addNewEntry();
                    }
                })
                .position((BUTTON_WIDTH + PADDING * 2) + PADDING_HALF, height - PADDING_HALF - BUTTON_HEIGHT)
                .size(BUTTON_WIDTH / 2, BUTTON_HEIGHT)
                .build();
    }

    @Override
    public void close() {
        this.minecraftClient.setScreen(this.parentScreen);
    }
    //endregion
}
