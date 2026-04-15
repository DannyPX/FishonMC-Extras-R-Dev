package dannypx.foe.screens;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dannypx.foe.FishOnMCExtras;
import dannypx.foe.handler.fetch.ChatHandler;
import dannypx.foe.handler.logic.LoggerHandler;
import dannypx.foe.handler.store.CustomChatTriggerDataHandler;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.screens.interfaces.ScreenConstants;
import dannypx.foe.screens.widget.ButtonListWidget;
import dannypx.foe.type.tuple.Triplet;
import dannypx.foe.type.type_adapter.PatternAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CustomChatTriggerMakerScreen extends Screen implements ScreenConstants {
    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final Screen parentScreen;

    private ButtonListWidget buttonList;
    private Map<String, ButtonListWidget.ButtonEntry> buttonEntryMap = new HashMap<>();
    private String selectedChatTriggerId;
    private CustomChatTriggerDataHandler.CustomChatTrigger selectedChatTrigger;


    private Text header;
    private final int widgetHeight = 20;

    private TextFieldWidget nameTextField;
    private CheckboxWidget useChatTriggerCheckBox;

    private final int sideWidth = 100;
    private TextFieldWidget regexTextField;
    private TextFieldWidget notificationToTriggerTextField;
    //endregion

    //region Methods
    public CustomChatTriggerMakerScreen(Screen parent) {
        super(Text.literal("Custom Chat Trigger Maker Screen"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.renderWidgets();
        this.resetFields();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBox(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);

        this.renderText(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY, delta);
        this.buttonList.render(context, mouseX, mouseY, delta);
    }

    private void renderTooltip(DrawContext context, int mouseX, int mouseY, float delta) {
        if(regexTextField.isMouseOver(mouseX, mouseY)) {
            context.drawTooltip(textRenderer, List.of(
                    Text.literal("Regex").formatted(Formatting.GRAY)
            ), mouseX, mouseY);
        }

        if(notificationToTriggerTextField.isMouseOver(mouseX, mouseY)) {
            context.drawTooltip(textRenderer, List.of(
                    Text.literal("Optional").formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                    Text.empty(),
                    Text.literal("- Notification Name").formatted(Formatting.GRAY)
            ), mouseX, mouseY);
        }
    }

    private void renderText(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(textRenderer,
                this.header,
                (BUTTON_WIDTH + PADDING * 2) + (minecraftClient.getWindow().getScaledWidth() - (BUTTON_WIDTH + PADDING * 2)) / 2,
                PADDING + widgetHeight / 2 - textRenderer.fontHeight / 2,
                0xFFFFFF
        );

        context.drawText(textRenderer,
                Text.literal("Name"),
                (BUTTON_WIDTH + PADDING * 2) + PADDING,
                PADDING + widgetHeight / 2 - textRenderer.fontHeight / 2 + (widgetHeight + PADDING),
                0xFFFFFF,
                true
        );

        context.drawText(textRenderer,
                Text.literal("Regex Filter"),
                (BUTTON_WIDTH + PADDING * 2) + PADDING,
                PADDING + widgetHeight / 2 - textRenderer.fontHeight / 2 + (widgetHeight + PADDING) * 2,
                0xFFFFFF,
                true
        );

        context.drawText(textRenderer,
                Text.literal("Trigger Notif."),
                (BUTTON_WIDTH + PADDING * 2) + PADDING,
                PADDING + widgetHeight / 2 - textRenderer.fontHeight / 2 + (widgetHeight + PADDING) * 3,
                0xFFFFFF,
                true
        );
    }

    private void renderBox(DrawContext context, int mouseX, int mouseY, float delta)
    {
        context.fill(
                (BUTTON_WIDTH + PADDING * 2), 0,
                minecraftClient.getWindow().getScaledWidth(),
                minecraftClient.getWindow().getScaledHeight() - (BUTTON_HEIGHT + PADDING_HALF) - 3,
                0x99000000);
        context.drawHorizontalLine((BUTTON_WIDTH + PADDING * 2), minecraftClient.getWindow().getScaledWidth(), minecraftClient.getWindow().getScaledHeight() - (BUTTON_HEIGHT + PADDING_HALF) - 3, 0xFF747474);
        context.drawVerticalLine((BUTTON_WIDTH + PADDING * 2), 0, minecraftClient.getWindow().getScaledHeight() - (BUTTON_HEIGHT + PADDING_HALF) - 3, 0xFF747474);
    }

    private void renderWidgets() {
        List<ClickableWidget> widgets = new ArrayList<>();

        widgets.add(this.saveBackButton());
        widgets.add(this.backButton());

        widgets.add(getButtonList());

        widgets.add(getNewButtonElementButton());
        widgets.add(getDeleteButtonElementButton());
        widgets.add(getImportButton());
        widgets.add(getExportButton());

        widgets.add(getNameTextField());
        widgets.add(getUseChatTriggerCheckBox());
        widgets.add(getRegexTextField());
        widgets.add(getNotificationToTriggerTextField());

        widgets.forEach(this::addDrawableChild);
    }

    private ClickableWidget getNameTextField() {
        nameTextField = new TextFieldWidget(
                textRenderer,
                (BUTTON_WIDTH + PADDING * 2) + PADDING + sideWidth,
                PADDING + widgetHeight + PADDING,
                minecraftClient.getWindow().getScaledWidth() - (BUTTON_WIDTH + PADDING * 2) - PADDING * 2 - (sideWidth + PADDING) - sideWidth,
                widgetHeight,
                Text.empty()
        );
        nameTextField.setMaxLength(Integer.MAX_VALUE);

        nameTextField.setChangedListener(s -> {
            if(selectedChatTriggerId != null) {
                nameTextField.setPlaceholder(Text.literal(s));
            }
        });

        return nameTextField;
    }

    private ClickableWidget getUseChatTriggerCheckBox() {
        useChatTriggerCheckBox = CheckboxWidget.builder(
                        Text.literal("Use Trigger"),
                        textRenderer
                )
                .pos(minecraftClient.getWindow().getScaledWidth() - PADDING - sideWidth
                        , PADDING + widgetHeight + PADDING)
                .checked(true)
                .callback((checkbox, checked) -> {})
                .build();
        return useChatTriggerCheckBox;
    }

    private ClickableWidget getRegexTextField() {
        regexTextField = new TextFieldWidget(
                textRenderer,
                (BUTTON_WIDTH + PADDING * 2) + PADDING + sideWidth,
                PADDING + (widgetHeight + PADDING) * 2,
                minecraftClient.getWindow().getScaledWidth() - (BUTTON_WIDTH + PADDING * 2) - PADDING * 2 - sideWidth,
                widgetHeight,
                Text.empty()
        );
        regexTextField.setMaxLength(Integer.MAX_VALUE);

        regexTextField.setChangedListener(s -> {
            if(selectedChatTriggerId != null) {
                regexTextField.setPlaceholder(Text.literal(s));
            }
        });

        return regexTextField;
    }

    private ClickableWidget getNotificationToTriggerTextField() {
        notificationToTriggerTextField = new TextFieldWidget(
                textRenderer,
                (BUTTON_WIDTH + PADDING * 2) + PADDING + sideWidth,
                PADDING + (widgetHeight + PADDING) * 3,
                minecraftClient.getWindow().getScaledWidth() - (BUTTON_WIDTH + PADDING * 2) - PADDING * 2 - sideWidth,
                widgetHeight,
                Text.empty()
        );
        notificationToTriggerTextField.setMaxLength(Integer.MAX_VALUE);

        notificationToTriggerTextField.setChangedListener(s -> {
            if(selectedChatTriggerId != null) {
                notificationToTriggerTextField.setPlaceholder(Text.literal(s));
            }
        });

        return notificationToTriggerTextField;
    }

    private ClickableWidget getNewButtonElementButton() {
        return ButtonWidget.builder(
                        Text.literal("Create Chat Trigger"),
                        (button) -> {
                            String id = "Custom Chat Trigger #" + UUID.randomUUID();

                            CustomChatTriggerDataHandler.instance().createNewCustomChatTrigger(id);

                            ButtonListWidget.ButtonEntry buttonEntry = createChatTriggerEntry(id);

                            buttonList.addEntry(buttonEntry);
                            buttonEntryMap.put(id, buttonEntry);
                        })
                .size(BUTTON_WIDTH / 2 - PADDING, BUTTON_HEIGHT)
                .position(PADDING_HALF, minecraftClient.getWindow().getScaledHeight() - PADDING_HALF - BUTTON_HEIGHT)
                .build();
    }

    private ClickableWidget getDeleteButtonElementButton() {
        return ButtonWidget.builder(
                        Text.literal("Delete Selected"),
                        (button) -> {
                            if(selectedChatTriggerId != null) {
                                CustomChatTriggerDataHandler.instance().deleteCustomChatTrigger(selectedChatTriggerId);
                                ChatHandler.instance().initChatTrigger();

                                ButtonListWidget.ButtonEntry entry = buttonEntryMap.get(selectedChatTriggerId);

                                buttonList.removeEntry(entry);
                                buttonEntryMap.remove(selectedChatTriggerId);

                                selectedChatTriggerId = null;
                                resetFields();
                            }
                        })
                .size(BUTTON_WIDTH / 2 - PADDING_HALF, BUTTON_HEIGHT)
                .position(PADDING + (BUTTON_WIDTH / 2 - PADDING_HALF), minecraftClient.getWindow().getScaledHeight() - PADDING_HALF - BUTTON_HEIGHT)
                .build();
    }

    private ClickableWidget getImportButton() {
        return ButtonWidget.builder(
                        Text.literal("Import"),
                        (button) -> {
                            String rawData = minecraftClient.keyboard.getClipboard();
                            try {
                                String json = TextHelper.decompress(Base64.getDecoder().decode(rawData));

                                Gson gson = new GsonBuilder().registerTypeAdapter(Pattern.class, new PatternAdapter()).create();
                                Triplet<String, CustomChatTriggerDataHandler.CustomChatTrigger, Integer> data = gson.fromJson(json, TypeToken.getParameterized(Triplet.class, String.class, CustomChatTriggerDataHandler.CustomChatTrigger.class, Integer.class).getType());

                                if(data.value3() > FishOnMCExtras.CHAT_TRIGGER_VERSION) {
                                    SystemToast.add(minecraftClient.getToastManager(),
                                            SystemToast.Type.PERIODIC_NOTIFICATION,
                                            Text.literal("Fish On Extras Rebirth"),
                                            Text.literal("Could not Import. Imported Chat Trigger is made on a newer version"));
                                    return;
                                }

                                if(CustomChatTriggerDataHandler.instance().getCustomChatTriggerData().chatTriggerList.containsKey(data.value1())) {
                                    CustomChatTriggerDataHandler.CustomChatTrigger trigger = data.value2();
                                    trigger.name = data.value1() + " (Duplicate)";

                                    data = Triplet.of(data.value1() + " (Duplicate)", trigger, data.value3());
                                }

                                String id = data.value1();

                                CustomChatTriggerDataHandler.instance().createNewCustomChatTrigger(data.value1(), data.value2());
                                ChatHandler.instance().initChatTrigger();

                                ButtonListWidget.ButtonEntry buttonEntry = createChatTriggerEntry(id);

                                buttonList.addEntry(buttonEntry);
                                buttonEntryMap.put(id, buttonEntry);

                                SystemToast.add(minecraftClient.getToastManager(),
                                        SystemToast.Type.PERIODIC_NOTIFICATION,
                                        Text.literal("Fish On Extras Rebirth"),
                                        Text.literal("Imported Chat Trigger"));
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
                        Text.literal("Export Selected"),
                        (button) -> {
                            if(selectedChatTriggerId != null) {
                                try {
                                    Triplet<String, CustomChatTriggerDataHandler.CustomChatTrigger, Integer> dataButton = Triplet.of(
                                            selectedChatTriggerId,
                                            selectedChatTrigger,
                                            FishOnMCExtras.CHAT_TRIGGER_VERSION
                                    );

                                    String rawData = Base64.getEncoder().encodeToString(
                                            TextHelper.compress(new GsonBuilder().registerTypeAdapter(Pattern.class, new PatternAdapter()).create().toJson(dataButton))
                                    );

                                    String dataToCopy = "**Custom Chat Trigger: **" + selectedChatTriggerId + "\n" +
                                            "```\n" +
                                            rawData + "\n" +
                                            "```\n" +
                                            "-# Using Chat Trigger version: " + "`v" + FishOnMCExtras.CHAT_TRIGGER_VERSION + "`";

                                    minecraftClient.keyboard.setClipboard(dataToCopy);

                                    SystemToast.add(minecraftClient.getToastManager(),
                                            SystemToast.Type.PERIODIC_NOTIFICATION,
                                            Text.literal("Fish On Extras Rebirth"),
                                            Text.literal("Exported Button on your clipboard"));
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
                "Custom Chat Triggers"
        );

        CustomChatTriggerDataHandler.instance().getCustomChatTriggerData().chatTriggerList.forEach((name, chatTrigger) -> {
            ButtonListWidget.ButtonEntry buttonEntry = createChatTriggerEntry(chatTrigger.name);

            buttonList.addEntry(buttonEntry);
            buttonEntryMap.put(chatTrigger.name, buttonEntry);
        });

        return buttonList;
    }

    private ButtonWidget saveBackButton() {
        return ButtonWidget.builder(Text.literal("Save and Return"), button -> {
            if(selectedChatTriggerId != null) {
                if(nameTextField.getText().isBlank()) {
                    SystemToast.add(minecraftClient.getToastManager(),
                            SystemToast.Type.PERIODIC_NOTIFICATION,
                            Text.literal("Fish On Extras Rebirth"),
                            Text.literal("Chat Trigger name is empty"));

                    return;
                }

                if(!Objects.equals(selectedChatTriggerId, nameTextField.getText())
                        && CustomChatTriggerDataHandler.instance().getCustomChatTriggerData().chatTriggerList.containsKey(nameTextField.getText())
                ) {
                    SystemToast.add(minecraftClient.getToastManager(),
                            SystemToast.Type.PERIODIC_NOTIFICATION,
                            Text.literal("Fish On Extras Rebirth"),
                            Text.literal("Chat Trigger name already exist"));

                    return;
                }

                try {
                    Pattern.compile(regexTextField.getText());
                } catch (PatternSyntaxException e) {
                    SystemToast.add(minecraftClient.getToastManager(),
                            SystemToast.Type.PERIODIC_NOTIFICATION,
                            Text.literal("Fish On Extras Rebirth"),
                            Text.literal("Regex cannot be compiled"));

                    LoggerHandler.error(e);

                    return;
                }

                CustomChatTriggerDataHandler.instance().updateChatTrigger(selectedChatTriggerId, nameTextField.getText(), regexTextField.getText(), notificationToTriggerTextField.getText(), useChatTriggerCheckBox.isChecked());

                ChatHandler.instance().initChatTrigger();
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

    private ButtonListWidget.ButtonEntry createChatTriggerEntry(String id) {
        return new ButtonListWidget.ButtonEntry(
                ButtonWidget.builder(
                        Text.literal(id),
                        button -> {
                            selectedChatTrigger = CustomChatTriggerDataHandler.instance().getCustomChatTriggerData().chatTriggerList.get(id);

                            if(selectedChatTrigger != null) {
                                selectedChatTriggerId = id;
                                this.setFields();
                            }
                        }
                ).width(BUTTON_WIDTH).build()
        );
    }

    private void setFields() {
        this.header = Text.literal(selectedChatTriggerId);
        nameTextField.setText(selectedChatTriggerId);
        nameTextField.setPlaceholder(Text.literal(selectedChatTriggerId));

        if(selectedChatTrigger != null) {
            if(selectedChatTrigger.useChatTrigger != useChatTriggerCheckBox.isChecked()) {
                useChatTriggerCheckBox.onPress();
            }

            regexTextField.setText(selectedChatTrigger.regex);
            regexTextField.setPlaceholder(Text.literal(selectedChatTrigger.regex));

            notificationToTriggerTextField.setText(selectedChatTrigger.notificationToTrigger);
            notificationToTriggerTextField.setPlaceholder(Text.literal(selectedChatTrigger.notificationToTrigger));
        }
    }

    private void resetFields() {
        this.header = Text.literal("No Chat Trigger Selected");

        nameTextField.setText("");
        nameTextField.setPlaceholder(Text.literal(""));

        if(useChatTriggerCheckBox.isChecked()) {
            useChatTriggerCheckBox.onPress();
        }

        regexTextField.setText("");
        regexTextField.setPlaceholder(Text.literal(""));

        notificationToTriggerTextField.setText("");
        notificationToTriggerTextField.setPlaceholder(Text.literal(""));

        selectedChatTrigger = null;
        selectedChatTriggerId = null;
    }

    @Override
    public void close() {
        this.minecraftClient.setScreen(this.parentScreen);
    }
    //endregion
}
