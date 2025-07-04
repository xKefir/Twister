package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.core.Game;
import org.minerail.twister.util.GameUtil;
import org.minerail.twister.util.MessageDeliverUtil;

public class Join implements SubCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("join")
                .requires(ctx -> ctx.getSender().hasPermission("tw.use"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
        Player p = Twister.get().getServer().getPlayer(sender.getName());
        if (!GameUtil.isPlayerInGame(p)) {
            if (Game.canJoin) {
                GameUtil.addPlayer(p);
                MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_JOINED);
                return 1;
            } else if (Game.gameStarted) {
                MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_GAME_ALREADY_STARTED);
                return 1;
            } else {
                MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_EVENT_NOT_STARTED);
                return 1;
            }
        } else {
            MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_PLAYER_IS_ALREADY_IN_GAME);
            return 1;
        }
    }
}
