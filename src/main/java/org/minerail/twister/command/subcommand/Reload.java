package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

public class Reload implements SubCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("reload")
                .executes(ctx -> execute(ctx.getSource().getSender()))
                .requires(ctx ->
                        ctx.getSender().hasPermission("tw.use")
                );
    }


    public int execute(CommandSender sender) {
        return 0;
    }
}
