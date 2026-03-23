package dannypx.foe.screens;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.handler.store.CustomNotificationDataHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.type.tuple.Triplet;
import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.screens.widget.ButtonListWidget;
import dannypx.foe.screens.widget.EditCustomNotificationWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Pattern;

public class CustomNotificationMakerScreen extends Screen implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final Screen parentScreen;

    private ButtonListWidget buttonList;
    private EditCustomNotificationWidget editCustomNotificationWidget;
    private Map<String, ButtonListWidget.ButtonEntry> buttonEntryMap = new HashMap<>();
    private String selectedNotification;

    Pattern ICON_PATTERN = Pattern.compile("^(?:([a-z0-9_]+:[a-z0-9_]+)(?:\\[([^]]*)\\])?)?$");
    //endregion

    //region Methods
    public CustomNotificationMakerScreen(Screen parent) {
        super(Text.literal("Custom Notification Maker Screen"));
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
        this.buttonList.render(context, mouseX, mouseY, delta);
        this.editCustomNotificationWidget.render(context, mouseX, mouseY, delta);
    }

    private void renderWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        widgets.add(this.saveBackButton());
        widgets.add(this.backButton());
        widgets.add(this.addLine());

        widgets.add(getEditNotificationWidget());
        widgets.add(getButtonList());

        widgets.add(getNewNotificationElementButton());
        widgets.add(getDeleteNotificationElementButton());
        widgets.add(getImportButton());
        widgets.add(getExportButton());

        widgets.forEach(this::addDrawableChild);
    }

    private ClickableWidget getEditNotificationWidget() {
        editCustomNotificationWidget = new EditCustomNotificationWidget(
                (BUTTON_WIDTH + PADDING * 2),
                0,
                width - (BUTTON_WIDTH + PADDING * 2),
                height - (BUTTON_HEIGHT + PADDING_HALF) - 3,
                Text.literal("No Notification Selected")
        );

        return editCustomNotificationWidget;
    }

    private ClickableWidget getNewNotificationElementButton() {
        return ButtonWidget.builder(
                        Text.literal("Create Notification"),
                        (button) -> {
                            String id = "Custom Notification #" + UUID.randomUUID();

                            CustomNotificationDataHandler.instance().createNewCustomNotification(id);

                            ButtonListWidget.ButtonEntry buttonEntry = createNotificationEntry(id);

                            buttonList.addEntry(buttonEntry);
                            buttonEntryMap.put(id, buttonEntry);
                        })
                .size(BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT)
                .position(PADDING_HALF, minecraftClient.getWindow().getScaledHeight() - PADDING_HALF - BUTTON_HEIGHT)
                .build();
    }

    private ClickableWidget getDeleteNotificationElementButton() {
        return ButtonWidget.builder(
                        Text.literal("Delete Selected Notification"),
                        (button) -> {
                            if(editCustomNotificationWidget.hasSelectedOption) {
                                CustomNotificationDataHandler.instance().deleteCustomNotification(selectedNotification);
                                editCustomNotificationWidget.reset();
                                ButtonListWidget.ButtonEntry entry = buttonEntryMap.get(selectedNotification);

                                buttonList.removeEntry(entry);
                                buttonEntryMap.remove(selectedNotification);

                                selectedNotification = null;
                            }
                        })
                .size(BUTTON_WIDTH / 2 - PADDING_HALF, BUTTON_HEIGHT)
                .position(PADDING + (BUTTON_WIDTH / 2 - PADDING_HALF), minecraftClient.getWindow().getScaledHeight() - PADDING_HALF - BUTTON_HEIGHT)
                .build();
    }

    private ClickableWidget getImportButton() {
        return ButtonWidget.builder(
                        Text.literal("Import Notification"),
                        (button) -> {
                            String rawData = minecraftClient.keyboard.getClipboard();
                            try {
                                String json = TextHelper.decompress(Base64.getDecoder().decode(rawData));

                                Gson gson = new GsonBuilder().create();
                                Triplet<String, CustomNotificationDataHandler.CustomNotification, Integer> data = gson.fromJson(json, TypeToken.getParameterized(Triplet.class, String.class, CustomNotificationDataHandler.CustomNotification.class, Integer.class).getType());

                                if(data.value3() > FishOnMCExtras.NOTIFICATION_VERSION) {
                                    SystemToast.add(minecraftClient.getToastManager(),
                                            SystemToast.Type.PERIODIC_NOTIFICATION,
                                            Text.literal("Fish On Extras Rebirth"),
                                            Text.literal("Could not Import. Imported Notification is made on a newer version"));

                                    return;
                                }

                                if(CustomNotificationDataHandler.instance().getCustomNotificationData().notificationList.containsKey(data.value1())) {
                                    data = Triplet.of(data.value1() + " (Duplicate)", data.value2(), data.value3());
                                }

                                String id = data.value1();

                                CustomNotificationDataHandler.instance().createNewCustomNotification(id, data.value2());

                                ButtonListWidget.ButtonEntry buttonEntry = createNotificationEntry(id);

                                buttonList.addEntry(buttonEntry);
                                buttonEntryMap.put(id, buttonEntry);

                                SystemToast.add(minecraftClient.getToastManager(),
                                        SystemToast.Type.PERIODIC_NOTIFICATION,
                                        Text.literal("Fish On Extras Rebirth"),
                                        Text.literal("Imported Notification"));

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
                            if(editCustomNotificationWidget.hasSelectedOption) {
                                try {
                                    Triplet<String, CustomNotificationDataHandler.CustomNotification, Integer> dataNotification = Triplet.of(
                                            editCustomNotificationWidget.currentSelectedNotification,
                                            CustomNotificationDataHandler.instance().getCustomNotificationData().notificationList.get(editCustomNotificationWidget.currentSelectedNotification),
                                            FishOnMCExtras.NOTIFICATION_VERSION
                                    );
                                    String rawData = Base64.getEncoder().encodeToString(
                                            TextHelper.compress(new GsonBuilder().create().toJson(dataNotification))
                                    );

                                    minecraftClient.keyboard.setClipboard(rawData);

                                    SystemToast.add(minecraftClient.getToastManager(),
                                            SystemToast.Type.PERIODIC_NOTIFICATION,
                                            Text.literal("Fish On Extras Rebirth"),
                                            Text.literal("Exported Notification on your clipboard"));
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

    private ClickableWidget getButtonList() {
        buttonList = new ButtonListWidget(
                client,
                (BUTTON_WIDTH + PADDING * 2),
                height - ScreenConstants.BUTTON_HEIGHT * 2 - PADDING - PADDING_HALF,
                0,
                BUTTON_HEIGHT + PADDING_HALF,
                BUTTON_HEIGHT,
                "Custom Notifications"
        );

        CustomNotificationDataHandler.instance().getCustomNotificationData().notificationList.forEach((id, ignored) -> {
            ButtonListWidget.ButtonEntry buttonEntry = createNotificationEntry(id);

            buttonList.addEntry(buttonEntry);
            buttonEntryMap.put(id, buttonEntry);
        });

        return buttonList;
    }

    private ButtonListWidget.ButtonEntry createNotificationEntry(String id) {
        return new ButtonListWidget.ButtonEntry(
                ButtonWidget.builder(
                        Text.literal(id),
                        button -> {
                            selectedNotification = id;
                            editCustomNotificationWidget.selectNotification(
                                    id,
                                    CustomNotificationDataHandler.instance().getCustomNotificationData().notificationList.get(id));
                        }
                ).width(BUTTON_WIDTH).build()
        );
    }

    private ButtonWidget saveBackButton() {
        return ButtonWidget.builder(Text.literal("Save and Return"), button -> {
                    if(editCustomNotificationWidget.hasSelectedOption) {
                        if(editCustomNotificationWidget.newName.isBlank()) {
                            SystemToast.add(minecraftClient.getToastManager(),
                                    SystemToast.Type.PERIODIC_NOTIFICATION,
                                    Text.literal("Fish On Extras Rebirth"),
                                    Text.literal("Notification name is empty"));

                            return;
                        }

                        if(
                                !Objects.equals(editCustomNotificationWidget.currentSelectedNotification, editCustomNotificationWidget.newName)
                                && CustomNotificationDataHandler.instance().getCustomNotificationData().notificationList.containsKey(editCustomNotificationWidget.newName)
                        ) {
                            SystemToast.add(minecraftClient.getToastManager(),
                                    SystemToast.Type.PERIODIC_NOTIFICATION,
                                    Text.literal("Fish On Extras Rebirth"),
                                    Text.literal("Notification name already exist"));

                            return;
                        }

                        if(!ICON_PATTERN.matcher(editCustomNotificationWidget.icon).matches()) {
                            SystemToast.add(minecraftClient.getToastManager(),
                                    SystemToast.Type.PERIODIC_NOTIFICATION,
                                    Text.literal("Fish On Extras Rebirth"),
                                    Text.literal("Icon is wrong format"));

                            return;
                        }

                        CustomNotificationDataHandler.instance().updateNotification(
                                editCustomNotificationWidget.currentSelectedNotification,
                                editCustomNotificationWidget.newName,
                                editCustomNotificationWidget.icon,
                                editCustomNotificationWidget.getEntries()
                                        .stream()
                                        .map(lineEntry -> lineEntry.lineString)
                                        .toList()
                        );
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
                    if(editCustomNotificationWidget.hasSelectedOption) {
                        editCustomNotificationWidget.addNewEntry();
                    }
                })
                .position(PADDING_HALF + (BUTTON_WIDTH + PADDING * 2), height - PADDING_HALF - BUTTON_HEIGHT)
                .size(BUTTON_WIDTH / 2, BUTTON_HEIGHT)
                .tooltip(Tooltip.of(Text.literal("Add line to the bottom")))
                .build();
    }

    @Override
    public void close() {
        this.minecraftClient.setScreen(this.parentScreen);
    }
    //endregion
}
