package io.github.sakurawald.module.initializer.command_meta.chain;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.command.executor.CommandExecutor;
import io.github.sakurawald.core.command.structure.ExtendedCommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChainInitializer extends ModuleInitializer {
    private static final Pattern CHAIN_COMMAND_PARSER = Pattern.compile("(.+?)\\s+(chain .+)");

    @CommandNode("chain")
    @CommandRequirement(level = 4)
    private static int chain(GreedyString rest) {

        String $rest = rest.getValue();

        Matcher matcher = CHAIN_COMMAND_PARSER.matcher($rest);
        if (matcher.find()) {
            String first = matcher.group(1);
            String second = matcher.group(2);
            int value = CommandExecutor.executeAsConsole(ExtendedCommandSource.ofServer(), first);
            // break chain, if command `fail`.
            if (value >= 0) {
                CommandExecutor.executeAsConsole(ExtendedCommandSource.ofServer(), second);
            }
        } else {
            CommandExecutor.executeAsConsole(ExtendedCommandSource.ofServer(), $rest);
        }

        return CommandHelper.Return.SUCCESS;
    }
}
