package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface SubCommand {
    LiteralArgumentBuilder<CommandSourceStack> get();
}
