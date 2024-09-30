package io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.core.command.argument.structure.Argument;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.wrapper.SubjectName;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class SubjectNameArgumentTypeAdapter extends BaseArgumentTypeAdapter {
    @Override
    public boolean match(Type type) {
        return SubjectName.class.equals(type);
    }

    @Override
    protected ArgumentType<?> makeArgumentType() {
        return StringArgumentType.string();
    }

    @Override
    public Object makeArgumentObject(CommandContext<ServerCommandSource> context, Argument argument) {
        return new SubjectName(StringArgumentType.getString(context, argument.getArgumentName()));
    }

    @Override
    public RequiredArgumentBuilder<ServerCommandSource, ?> makeRequiredArgumentBuilder(String argumentName) {
        return super.makeRequiredArgumentBuilder(argumentName).suggests((ctx, builder)-> {
            Managers.getAttachmentManager().listSubjectName().forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
