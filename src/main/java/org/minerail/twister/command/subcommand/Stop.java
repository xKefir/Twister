package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.game.Game;

public class Stop implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("stop")
                .requires(ctx -> ctx.getSender().hasPermission("tw.admin"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
        if (Game.gameStarted) {
            Game.stop("game");
            broadcast(sender);
            return 1;
        } else if (Game.lobbyIsOpen) {
            Game.stop("lobby");
            broadcast(sender);
            return 1;
        } else {
            sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_STOP_EVENT_IS_NOT_ACTIVE,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));
            return 1;
        }
    }
    private void broadcast(CommandSender sender) {
        sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_STOP_TO_SENDER,
                Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));
        Bukkit.broadcast(MessageProvider.get(MessageKey.MESSAGES_COMMAND_STOP_BROADCAST,
                Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));

    }
}
