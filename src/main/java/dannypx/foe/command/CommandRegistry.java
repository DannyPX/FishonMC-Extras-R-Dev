package dannypx.foe.command;

import com.mojang.brigadier.CommandDispatcher;
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
                        .then(command("notification").executes(Command.Reset::resetNotification))
                        .then(command("timer").executes(Command.Reset::resetTimer))
                        .then(command("hud").executes(Command.Reset::resetHud))
                )
                .then(command("toggle")
                        .then(command("render")
                                .then(command("armor").executes(Command.Toggle::toggleArmor))
                                .then(command("pet_names").executes(Command.Toggle::togglePetNames))
                                .then(command("fishingHook_model").executes(Command.Toggle::toggleFishingHookModel))
                                .then(command("bait_on_fishing_hook").executes(Command.Toggle::toggleBaitOnFishingHook))
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

            public static int resetNotification(CommandContext<FabricClientCommandSource> context) {
                return executeCommand(context, Component.literal("Reset notifications to default config").withStyle(ChatFormatting.GREEN), () -> CustomNotificationDataHandler.instance().resetNotifications());
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
