package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.game.Game;
import org.minerail.twister.util.PlayerUtil;

public class Join implements SubCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("join")
                .requires(ctx -> ctx.getSender().hasPermission("tw.use"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
        Player p = Twister.get().getServer().getPlayer(sender.getName());
        if (!PlayerUtil.playerIsInGame(p)) {
            if (Game.canJoin) {
                PlayerUtil.playerJoinToGame(p);
                sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_JOINED,
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                return 1;
            } else if (Game.gameStarted) {
                sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_GAME_ALREADY_STARTED,
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                return 1;
            } else {
                sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_EVENT_NOT_STARTED,
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                return 1;

            }
        } else {
            p.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_PLAYER_IS_ALREADY_IN_GAME,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));
            return 1;
        }
    }
}
