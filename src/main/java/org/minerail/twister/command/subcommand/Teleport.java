package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.util.GameUtil;
import org.minerail.twister.util.MessageDeliverUtil;
import org.minerail.twister.util.TextFormatUtil;

public class Teleport implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("tp")
                .requires(ctx -> ctx.getSender().hasPermission("tw.admin"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
        GameUtil.teleportToArena((Player) sender);
        MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_TP_TO_SENDER);
        return 1;
    }
}
