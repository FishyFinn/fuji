package io.github.sakurawald.module.mixin.chat.history;

import io.github.sakurawald.module.initializer.chat.history.ChatHistoryInitializer;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerManager.class, priority = 1000 - 1)
public class PlayerManagerMixin {

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void sendChatHistoryToNewJoinedPlayer(ClientConnection connection, @NotNull ServerPlayerEntity serverPlayer, ConnectedClientData commonListenerCookie, CallbackInfo ci) {
        ChatHistoryInitializer.getChatHistory().forEach(serverPlayer::sendMessage);
    }

}
