package dannypx.foe.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dannypx.foe.FishOnMCExtras;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.screens.MainScreen;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class CommandRegistry {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerCommands(dispatcher));
    }

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(command("foe")
                .then(command("config").executes(Command::openConfig))
                .then(command("main").executes(Command::openMainScreen))
                .executes(Command::openMainScreen)
        );
    }

    private static class Command {
        public static int openConfig(CommandContext<FabricClientCommandSource> context) {
            return executeCommand(() -> ConfigApiJava.INSTANCE.openScreen(FishOnMCExtras.MOD_ID));
        }

        public static int openMainScreen(CommandContext<FabricClientCommandSource> context) {
            return executeCommand(() -> MinecraftClient.getInstance().setScreen(new MainScreen(MinecraftClient.getInstance().currentScreen)));
        }
    }

    //region Command Builder
    private static LiteralArgumentBuilder<FabricClientCommandSource> command(String command) {
        return ClientCommandManager.literal(command);
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context, List<Text> feedback, ExecuteCallback executeCallback) {
        return executeCommand(context, TextHelper.concat(feedback.toArray(new Text[]{})), executeCallback);
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context, String feedback, ExecuteCallback executeCallback) {
        return executeCommand(context, Text.literal(feedback), executeCallback);
    }

    private static int executeCommand(ExecuteCallback executeCallback) {
        MinecraftClient.getInstance().send(executeCallback::execute);
        return 1;
    }

    private static int executeCommand(CommandContext<FabricClientCommandSource> context, Text feedback, ExecuteCallback executeCallback) {
        MinecraftClient.getInstance().send(executeCallback::execute);
        return sendFeedback(context, feedback);
    }

    private static int sendFeedback(CommandContext<FabricClientCommandSource> context, Text feedback) {
        context.getSource().sendFeedback(
                TextHelper.concat(
                        Text.literal("FoE ").formatted(Formatting.DARK_GREEN, Formatting.BOLD),
                        Text.literal("» ").formatted(Formatting.DARK_GRAY),
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
