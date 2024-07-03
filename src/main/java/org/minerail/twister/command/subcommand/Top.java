package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

public class Top implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("top")
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
        return 0;
    }
}
