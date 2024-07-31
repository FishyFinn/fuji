package io.github.sakurawald.module.initializer.tpa;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.common.job.MentionPlayersJob;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tpa.structure.TpaRequest;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.Getter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("LombokGetterMayBeUsed")

public class TpaInitializer extends ModuleInitializer {

    @Getter
    private final ArrayList<TpaRequest> requests = new ArrayList<>();

    @Command("tpa")
    private int $tpa(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doRequest(player, target, false);
    }

    @Command("tpahere")
    private int $tpahere(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doRequest(player, target, true);
    }

    @Command("tpaaccept")
    private int $tpaaccept(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doResponse(player, target, ResponseStatus.ACCEPT);
    }

    @Command("tpadeny")
    private int $tpadeny(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doResponse(player, target, ResponseStatus.DENY);
    }

    @Command("tpacancel")
    private int $tpacancel(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return doResponse(player, target, ResponseStatus.CANCEL);
    }

    private int doResponse(ServerPlayerEntity source, ServerPlayerEntity target, ResponseStatus status) {
        /* resolve relative request */
        Optional<TpaRequest> requestOptional = requests.stream()
                .filter(request ->
                        status == ResponseStatus.CANCEL ?
                                (request.getSender().equals(source) && request.getReceiver().equals(target))
                                : (request.getSender().equals(target) && request.getReceiver().equals(source)))
                .findFirst();
        if (requestOptional.isEmpty()) {
            MessageHelper.sendActionBar(source, "tpa.no_relative_ticket");
            return CommandHelper.Return.FAIL;
        }

        TpaRequest request = requestOptional.get();
        if (status == ResponseStatus.ACCEPT) {
            request.getSender().sendActionBar(request.asSenderComponent$Accepted());
            request.getReceiver().sendMessage(request.asReceiverComponent$Accepted());

            ServerPlayerEntity who = request.getTeleportWho();
            ServerPlayerEntity to = request.getTeleportTo();
            MentionPlayersJob.scheduleJob(request.isTpahere() ? to : who);
            who.teleport((ServerWorld) to.getWorld(), to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
        } else if (status == ResponseStatus.DENY) {
            request.getSender().sendActionBar(request.asSenderComponent$Denied());
            request.getReceiver().sendMessage(request.asReceiverComponent$Denied());
        } else if (status == ResponseStatus.CANCEL) {
            request.getSender().sendMessage(request.asSenderComponent$Cancelled());
            request.getReceiver().sendMessage(request.asReceiverComponent$Cancelled());
        }

        request.cancelTimeout();
        requests.remove(request);
        return CommandHelper.Return.SUCCESS;
    }

    private int doRequest(ServerPlayerEntity source, ServerPlayerEntity target, boolean tpahere) {
        /* add request */
        TpaRequest request = new TpaRequest(source, target, tpahere);

        /* has similar request ? */
        if (request.getSender().equals(request.getReceiver())) {
            MessageHelper.sendActionBar(request.getSender(), "tpa.request_to_self");

            return CommandHelper.Return.FAIL;
        }

        if (requests.stream().anyMatch(request::similarTo)) {
            MessageHelper.sendActionBar(request.getSender(), "tpa.similar_request_exists");
            return CommandHelper.Return.FAIL;
        }

        requests.add(request);
        request.startTimeout();

        /* feedback */
        request.getReceiver().sendMessage(request.asReceiverComponent$Sent());
        MentionPlayersJob.scheduleJob(request.getReceiver());
        request.getSender().sendMessage(request.asSenderComponent$Sent());
        return CommandHelper.Return.SUCCESS;
    }

    private enum ResponseStatus {ACCEPT, DENY, CANCEL}
}
