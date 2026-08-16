package org.studyingalpha.loss_menu.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.studyingalpha.loss_menu.config.ProgressionManager;

public class WorldModeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("worldmode")
                        .requires(source -> source.hasPermission(2)) // 仅OP可用
                        .then(Commands.argument("mode", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    builder.suggest("times_change");
                                    builder.suggest("vanilla");
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(WorldModeCommand::execute)
                                )
                        )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        String mode = StringArgumentType.getString(context, "mode");
        boolean value = BoolArgumentType.getBool(context, "value");

        if (!mode.equals("times_change") && !mode.equals("vanilla")) {
            context.getSource().sendFailure(
                    Component.translatable("loss_menu.command.worldmode.unknown_mode", mode)
            );
            return 0;
        }

        ProgressionManager.setUnlocked(mode, value);
        context.getSource().sendSuccess(
                () -> Component.translatable("loss_menu.command.worldmode.success", mode, value),
                true
        );
        return 1;
    }
}