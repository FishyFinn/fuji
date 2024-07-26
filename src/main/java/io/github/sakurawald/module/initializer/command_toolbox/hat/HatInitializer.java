package io.github.sakurawald.module.initializer.command_toolbox.hat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;


public class HatInitializer extends ModuleInitializer {


    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("hat").executes(this::$hat));
    }

    @SuppressWarnings("SameReturnValue")
    private int $hat(@NotNull CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            ItemStack mainHandItem = player.getMainHandStack();
            ItemStack headSlotItem = player.getEquippedStack(EquipmentSlot.HEAD);

            player.equipStack(EquipmentSlot.HEAD, mainHandItem);
            player.setStackInHand(Hand.MAIN_HAND, headSlotItem);
            MessageHelper.sendMessage(player, "hat.success");
            return CommandHelper.Return.SUCCESS;
        });
    }

}
