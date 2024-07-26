package io.github.sakurawald.module.mixin.teleport_warmup;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.structure.BossBarTicket;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.common.structure.TeleportTicket;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.EntityHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 500)
public abstract class ServerPlayerMixin {

    @Unique
    public @Nullable TeleportTicket getTeleportTicket(@NotNull ServerPlayerEntity player) {
        for (BossBarTicket ticket : Managers.getBossBarManager().getTickets()) {
            if (ticket instanceof TeleportTicket teleportTicket) {
                if (player.equals(teleportTicket.getPlayer())) {
                    return teleportTicket;
                }
            }
        }

        return null;
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void $teleport(@NotNull ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, @NotNull CallbackInfo ci) {

        if (!Configs.configHandler.model().modules.teleport_warmup.dimension.list.contains(IdentifierHelper.ofString(targetWorld))) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // If we try to spawn a fake-player in the end or nether, the fake-player will initially spawn in overworld
        // and teleport to the target world. This will cause the teleport warmup to be triggered.
        if (EntityHelper.isNonRealPlayer(player)) return;

        TeleportTicket ticket = getTeleportTicket(player);
        if (ticket == null) {
            ticket = TeleportTicket.of(
                    player
                    , Position.of(player)
                    , new Position(targetWorld, x, y, z, yaw, pitch));
            Managers.getBossBarManager().addTicket(ticket);
            ci.cancel();
        } else {
            if (!ticket.isReady()) {
                MessageHelper.sendActionBar(player, "teleport_warmup.another_teleportation_in_progress");
                ci.cancel();
            }
        }

        // yeah, let's do teleport now.
    }

    @Inject(method = "damage", at = @At("RETURN"))
    public void $damage(DamageSource damageSource, float amount, @NotNull CallbackInfoReturnable<Boolean> cir) {
        // If damage was actually applied...
        if (cir.getReturnValue()) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            if (EntityHelper.isNonRealPlayer(player)) return;

            TeleportTicket ticket = getTeleportTicket(player);
            if (ticket != null) {
                ticket.setAborted(true);
            }
        }
    }
}
