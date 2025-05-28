package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.minerail.twister.Twister;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.util.MessageDeliverUtil;

public class Reload implements SubCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("tw.admin"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }


    public int execute(CommandSender sender) {
        try {
            MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_RELOAD_SUCCESS);
            Twister.reloadAll();
            return 1;
        } catch (Exception e) {
            MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_RELOAD_ERROR);
            return 1;
        }
    }
}
