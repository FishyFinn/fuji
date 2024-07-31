package io.github.sakurawald.module.mixin._internal.low_level.command;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import io.github.sakurawald.command.interfaces.CommandContextBuilderAccessor;
import io.github.sakurawald.util.LogUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CommandContextBuilder.class)
public abstract class CommandContextBuilderMixin<S> implements CommandContextBuilderAccessor<S> {

    @Accessor("arguments")
    public abstract Map<String, ParsedArgument<S, ?>> getArguments();

    @Unique
    public CommandContextBuilder<S> withArguments(Map<String, ParsedArgument<S, ?>> arguments) {
        getArguments().putAll(arguments);
        CommandContextBuilder that = (CommandContextBuilder) (Object) this;
        return that;
    }
}
