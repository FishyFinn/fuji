package io.github.sakurawald.module.initializer.extinguish;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;


public class ExtinguishModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("extinguish").executes(this::$extinguish));
    }

    @SuppressWarnings("SameReturnValue")
    private int $extinguish(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        player.setRemainingFireTicks(0);
        return Command.SINGLE_SUCCESS;
    }

}
