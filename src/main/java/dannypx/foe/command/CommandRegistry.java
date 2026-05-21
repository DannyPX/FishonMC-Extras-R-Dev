package dannypx.foe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dannypx.foe.FishOnMCExtras;
import dannypx.foe.config.Configs;
import dannypx.foe.handler.fetch.ChatHandler;
import dannypx.foe.handler.fetch.StatsScreenHandler;
import dannypx.foe.handler.logic.TimerHandler;
import dannypx.foe.handler.store.*;
import dannypx.foe.helper.ComponentHelper;
import dannypx.foe.screens.MainScreen;
import dannypx.foe.type.custom_value.BooleanValue;
import dannypx.foe.type.custom_value.EmptyValue;
import dannypx.foe.type.custom_value.NumberValue;
import dannypx.foe.type.custom_value.TrackerValue;
import dannypx.foe.type.tracker.TrackerAction;
import dannypx.foe.type.tracker.TrackerType;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.List;

public class CommandRegistry {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerCommands(dispatcher));
    }

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                command("foe")
                .then(command("config").executes(Command.Foe::openConfig))
                .then(command("main").executes(Command.Foe::openMainScreen))
                .then(command("stats")
                        .then(command("import").executes(Command.Stats::importStats))
                        .then(command("cancel").executes(Command.Stats::cancelStats))
                        .then(command("reset").executes(Command.Stats::resetStats))
                )
                .then(command("crew")
                        .then(command("import").executes(Command.Crew::importCrew))
                        .then(command("cancel").executes(Command.Crew::cancelCrew))
                )
                .then(command("reset_to_defaults")
                        .then(command("button").executes(Command.Reset::resetButton))
                        .then(command("chat_trigger").executes(Command.Reset::resetChatTrigger))
                        .then(command("event_trigger").executes(Command.Reset::resetEventTrigger))
                        .then(command("notification").executes(Command.Reset::resetNotification))
                        .then(command("chat_notification").executes(Command.Reset::resetChatNotification))
                        .then(command("timer").executes(Command.Reset::resetTimer))
                        .then(command("hud").executes(Command.Reset::resetHud))
                        .then(command("tracker").executes(Command.Reset::resetTracker))
                )
                .then(command("fix_defaults")
                        .then(command("chat_trigger").executes(Command.Fix::fixChatTrigger))
                        .then(command("event_trigger").executes(Command.Fix::fixEventTrigger))
                        .then(command("notification").executes(Command.Fix::fixNotification))
                        .then(command("chat_notification").executes(Command.Fix::fixChatNotification))
                        .then(command("timer").executes(Command.Fix::fixTimer))
                        .then(command("hud").executes(Command.Fix::fixHud))
                        .then(command("tracker").executes(Command.Fix::fixTracker))
                )
                .then(command("toggle")
                        .then(command("render")
                                .then(command("armor").executes(Command.Toggle::toggleArmor))
                                .then(command("pet_names").executes(Command.Toggle::togglePetNames))
                                .then(command("name_plates").executes(Command.Toggle::toggleNamePlates))
                                .then(command("fishingHook_model").executes(Command.Toggle::toggleFishingHookModel))
                                .then(command("bait_on_fishing_hook").executes(Command.Toggle::toggleBaitOnFishingHook))
                        )
                )
                .then(command("tracker")
                        .then(command("set")
                                .then(ClientCommandManager.argument("tracker", StringArgumentType.string()).then(
                                        ClientCommandManager.argument("value", StringArgumentType.string())
                                                .executes(Command.Tracker::setValue)
                                ))
                        )
                        .then(command("toggle")
                                .then(ClientCommandManager.argument("tracker", StringArgumentType.string())
                                        .executes(Command.Tracker::toggleValue)
                                )
                        )
                        .then(command("add")
                                .then(ClientCommandManager.argument("tracker", StringArgumentType.string()).then(
                                        ClientCommandManager.argument("value", IntegerArgumentType.integer(0))
                                                .executes(Command.Tracker::addValue)
                                ))
                        )
                        .then(command("subtract")
                                .then(ClientCommandManager.argument("tracker", StringArgumentType.string()).then(
                                        ClientCommandManager.argument("value", IntegerArgumentType.integer(0))
                                                .executes(Command.Tracker::subtractValue)
                                ))
                        )
                )
                .executes(Command.Foe::openMainScreen)
        );
    }

    private static class Command {
        static class Foe {
            public static int openConfig(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(() -> ConfigApiJava.INSTANCE.openScreen(FishOnMCExtras.MOD_ID));
            }

            public static int openMainScreen(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(() -> Minecraft.getInstance().setScreen(new MainScreen(Minecraft.getInstance().screen)));
            }
        }

        static class Stats {
            public static int importStats(CommandContext<FabricClientCommandSource> context) {
                StatsScreenHandler.instance().setImportStats(true);
                return executeCommand(() -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.connection.sendCommand("stats");
                    }
                });
            }

            public static int cancelStats(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(() -> ProfileDataHandler.instance().updateImportStats(true));
            }

            public static int resetStats(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(() -> StatsDataHandler.instance().resetStats());
            }
        }

        static class Crew {
            public static int importCrew(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(() -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.connection.sendCommand("c info");
                    }
                });
            }

            public static int cancelCrew(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(() -> ProfileDataHandler.instance().updateImportCrew(true));
            }
        }

        static class Reset {
            public static int resetButton(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset buttons to default config").withStyle(ChatFormatting.GREEN), () -> CustomButtonDataHandler.instance().resetButtons());
            }

            public static int resetChatTrigger(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset chat triggers to default config").withStyle(ChatFormatting.GREEN), () -> {
                    CustomChatTriggerDataHandler.instance().resetChatTriggers();
                    ChatHandler.instance().initChatTrigger();
                });
            }

            public static int resetEventTrigger(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset event triggers to default config").withStyle(ChatFormatting.GREEN), () -> {
                    CustomEventTriggerDataHandler.instance().resetEventTrigger();
                });
            }

            public static int resetNotification(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset notifications to default config").withStyle(ChatFormatting.GREEN), () ->
                        CustomNotificationDataHandler.instance().resetNotifications());
            }

            public static int resetChatNotification(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset chat notifications to default config").withStyle(ChatFormatting.GREEN), () ->
                        CustomChatNotificationDataHandler.instance().resetChatNotifications());
            }

            public static int resetTimer(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset timers to default config").withStyle(ChatFormatting.GREEN), () -> {
                    CustomTimerDataHandler.instance().resetTimers();
                    TimerHandler.instance().initTimers();
                });
            }

            public static int resetHud(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset HUDs to default config").withStyle(ChatFormatting.GREEN), () -> CustomHudDataHandler.instance().resetHuds());
            }

            public static int resetTracker(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset trackers to default config").withStyle(ChatFormatting.GREEN), () -> CustomTrackerDataHandler.instance().resetTrackers());
            }
        }

        static class Fix {
            public static int fixChatTrigger(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Fixed default chat triggers").withStyle(ChatFormatting.GREEN), () -> {
                    CustomChatTriggerDataHandler.instance().fixDefault();
                    ChatHandler.instance().initChatTrigger();
                });
            }

            public static int fixEventTrigger(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Fixed default event triggers").withStyle(ChatFormatting.GREEN), () -> {
                    CustomEventTriggerDataHandler.instance().fixDefault();
                });
            }

            public static int fixNotification(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Fixed default notifications").withStyle(ChatFormatting.GREEN), () ->
                        CustomNotificationDataHandler.instance().fixDefault());
            }

            public static int fixChatNotification(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Fixed default chat notifications").withStyle(ChatFormatting.GREEN), () ->
                        CustomChatNotificationDataHandler.instance().fixDefault());
            }

            public static int fixTimer(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Fixed default timers").withStyle(ChatFormatting.GREEN), () -> {
                    CustomTimerDataHandler.instance().fixDefault();
                    TimerHandler.instance().initTimers();
                });
            }

            public static int fixHud(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Fixed default HUDs").withStyle(ChatFormatting.GREEN), () -> CustomHudDataHandler.instance().fixDefault());
            }

            public static int fixTracker(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Fixed default trackers").withStyle(ChatFormatting.GREEN), () -> CustomTrackerDataHandler.instance().fixDefault());
            }
        }

        static class Toggle {
            public static int toggleArmor(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Toggled Armor"), () -> {
                    Configs.rendererConfig.hideArmor.accept(!Configs.rendererConfig.hideArmor.get());
                    Configs.rendererConfig.save();
                });
            }

            public static int togglePetNames(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Toggled Pet Names"), () -> {
                    Configs.rendererConfig.showPetName.accept(!Configs.rendererConfig.showPetName.get());
                    Configs.rendererConfig.save();
                });
            }

            public static int toggleNamePlates(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Toggled Name Plates"), () -> {
                    Configs.rendererConfig.showNamePlate.accept(!Configs.rendererConfig.showNamePlate.get());
                    Configs.rendererConfig.save();
                });
            }

            public static int toggleFishingHookModel(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Toggled Fishing Hook Model"), () -> {
                    Configs.rendererConfig.showNewFishingHook.accept(!Configs.rendererConfig.showNewFishingHook.get());
                    Configs.rendererConfig.save();
                });
            }

            public static int toggleBaitOnFishingHook(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Toggled Bait on Fishing Hook"), () -> {
                    Configs.rendererConfig.showBaitOnFishingHook.accept(!Configs.rendererConfig.showBaitOnFishingHook.get());
                    Configs.rendererConfig.save();
                });
            }
        }

        static class Tracker {
            public static int setValue(CommandContext<FabricClientCommandSource> context) {
                String tracker = StringArgumentType.getString(context, "tracker");
                String value = StringArgumentType.getString(context, "value");

                if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.containsKey(tracker)) {
                    if("true".equals(value) || "false".equals(value)) {
                        if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.get(tracker).getTrackerType() == TrackerType.BOOLEAN) {
                            boolean parsed = Boolean.parseBoolean(value);
                            TrackerValue trackerValue = BooleanValue.of(parsed);
                            return updateValue(context, TrackerAction.SET, tracker, trackerValue);
                        } else {
                            return executeCommand(context, Component.literal("Value must be a number").withStyle(ChatFormatting.RED), () -> {});
                        }
                    }

                    try {
                        if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.get(tracker).getTrackerType() == TrackerType.INTEGER) {
                            int parsed = Integer.parseInt(value);
                            TrackerValue trackerValue = NumberValue.of(parsed);
                            return updateValue(context, TrackerAction.SET, tracker, trackerValue);
                        } else {
                            return executeCommand(context, Component.literal("Value must be a boolean").withStyle(ChatFormatting.RED), () -> {});
                        }

                    } catch (Exception ignored) {}

                    return executeCommand(context, Component.literal("Could not parse value").withStyle(ChatFormatting.RED), () -> {});
                } else {
                    return executeCommand(context, Component.literal("Could not find tracker").withStyle(ChatFormatting.RED), () -> {});
                }
            }

            public static int toggleValue(CommandContext<FabricClientCommandSource> context) {
                String tracker = StringArgumentType.getString(context, "tracker");

                if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.containsKey(tracker)) {
                    if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.get(tracker).getTrackerType() == TrackerType.BOOLEAN) {
                        return updateValue(context, TrackerAction.TOGGLE, tracker, EmptyValue.getDefault());
                    } else {
                        return executeCommand(context, Component.literal("Tracker is not a boolean").withStyle(ChatFormatting.RED), () -> {});
                    }
                } else {
                    return executeCommand(context, Component.literal("Could not find tracker").withStyle(ChatFormatting.RED), () -> {});
                }
            }

            public static int addValue(CommandContext<FabricClientCommandSource> context) {
                String tracker = StringArgumentType.getString(context, "tracker");
                int value = IntegerArgumentType.getInteger(context, "value");

                if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.containsKey(tracker)) {
                    if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.get(tracker).getTrackerType() == TrackerType.INTEGER) {
                        TrackerValue trackerValue = NumberValue.of(value);
                        return updateValue(context, TrackerAction.ADD, tracker, trackerValue);
                    } else {
                        return executeCommand(context, Component.literal("Tracker must be a integer").withStyle(ChatFormatting.RED), () -> {});
                    }
                } else {
                    return executeCommand(context, Component.literal("Could not find tracker").withStyle(ChatFormatting.RED), () -> {});
                }
            }

            public static int subtractValue(CommandContext<FabricClientCommandSource> context) {
                String tracker = StringArgumentType.getString(context, "tracker");
                int value = IntegerArgumentType.getInteger(context, "value");

                if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.containsKey(tracker)) {
                    if(CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.get(tracker).getTrackerType() == TrackerType.INTEGER) {
                        TrackerValue trackerValue = NumberValue.of(value);
                        return updateValue(context, TrackerAction.SUBTRACT, tracker, trackerValue);
                    } else {
                        return executeCommand(context, Component.literal("Tracker must be a integer").withStyle(ChatFormatting.RED), () -> {});
                    }
                } else {
                    return executeCommand(context, Component.literal("Could not find tracker").withStyle(ChatFormatting.RED), () -> {});
                }
            }

            private static int updateValue(CommandContext<FabricClientCommandSource> context, TrackerAction action, String tracker, TrackerValue value) {
                return switch (value) {
                    case BooleanValue booleanValue -> switch (action) {
                        case SET -> executeCommand(context, Component.literal("Set " + tracker + " to " + booleanValue.value()), () -> {
                            CustomTrackerDataHandler.instance().updateTracker(tracker, action, booleanValue);
                        });
                        default -> executeCommand(context, Component.literal("Error").withStyle(ChatFormatting.RED), () -> {});
                    };
                    case NumberValue numberValue -> switch (action) {
                        case SET -> executeCommand(context, Component.literal("Set " + tracker + " to " + numberValue.value()), () -> {
                            CustomTrackerDataHandler.instance().updateTracker(tracker, action, numberValue);
                        });
                        case ADD -> executeCommand(context, Component.literal("Add " + numberValue.value() + " to " + tracker), () -> {
                            CustomTrackerDataHandler.instance().updateTracker(tracker, action, numberValue);
                        });
                        case SUBTRACT -> executeCommand(context, Component.literal("Subtract " + numberValue.value() + " to " + tracker), () -> {
                            CustomTrackerDataHandler.instance().updateTracker(tracker, action, numberValue);
                        });
                        default -> executeCommand(context, Component.literal("Error").withStyle(ChatFormatting.RED), () -> {});
                    };
                    case EmptyValue ignored -> switch (action) {
                        case TOGGLE -> executeCommand(context, Component.literal("Toggle " + tracker), () -> {
                            CustomTrackerDataHandler.instance().updateTracker(tracker, action, EmptyValue.getDefault());
                        });
                        default -> executeCommand(context, Component.literal("Error").withStyle(ChatFormatting.RED), () -> {});
                    };
                    default -> executeCommand(context, Component.literal("Error").withStyle(ChatFormatting.RED), () -> {});
                };
            }
        }
    }

    //region Command Builder
    private static LiteralArgumentBuilder<FabricClientCommandSource> command(String command) {
        return ClientCommandManager.literal(command);
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context, List<Component> feedback, ExecuteCallback executeCallback) {
        return executeCommand(context, ComponentHelper.concat(feedback.toArray(new Component[]{})), executeCallback);
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context, String feedback, ExecuteCallback executeCallback) {
        return executeCommand(context, Component.literal(feedback), executeCallback);
    }

    private static int executeCommand(ExecuteCallback executeCallback) {
        Minecraft.getInstance().schedule(executeCallback::execute);
        return 1;
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context, Component feedback, ExecuteCallback executeCallback) {
        Minecraft.getInstance().schedule(executeCallback::execute);
        return sendFeedback(context, feedback);
    }

    private static int sendFeedback(CommandContext<FabricClientCommandSource> context, Component feedback) {
        context.getSource().sendFeedback(
                ComponentHelper.concat(
                        Component.literal("FoER ").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD),
                        Component.literal("» ").withStyle(ChatFormatting.DARK_GRAY),
                        feedback
                )

        );
        return 1;
    }

    private interface ExecuteCallback {
        void execute();
    }
    //endregion
}
