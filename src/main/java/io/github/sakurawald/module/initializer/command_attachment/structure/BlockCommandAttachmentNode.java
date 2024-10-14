package io.github.sakurawald.module.initializer.command_attachment.structure;

import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.ExecuteAsType;
import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.InteractType;

public class BlockCommandAttachmentNode extends CommandAttachmentNode {

    String created_in;

    public BlockCommandAttachmentNode(String created_in, String command, InteractType interactType, ExecuteAsType executeAsType, int maxUseTimes, int useTimes) {
        super(CommandAttackmentType.BLOCK, command, interactType, executeAsType, maxUseTimes, useTimes);
        this.created_in = created_in;
    }
}
