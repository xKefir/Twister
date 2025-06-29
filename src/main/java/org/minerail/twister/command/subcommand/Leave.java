package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.file.playerdata.PlayerData;
import org.minerail.twister.game.core.Game;
import org.minerail.twister.util.GameUtil;
import org.minerail.twister.util.MessageDeliverUtil;

public class Leave implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("leave")
                .requires(ctx -> ctx.getSender().hasPermission("tw.use"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
        Player p = Twister.get().getServer().getPlayer(sender.getName());
        if (GameUtil.isPlayerInGame(p)) {
            GameUtil.removePlayer(p);
            MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_LEAVE_SUCCESS);

            MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_COMMAND_LEAVE_BROADCAST,
                    Placeholder.component("player", Component.text(sender.getName())),
                    Placeholder.component("remainplayers", Component.text(Game.players.size())));

            p.teleport(PlayerData.get(p).getPlayerJoinLocationFromData());
            return 1;
        } else {
            MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_LEAVE_PLAYER_IS_NOT_IN_GAME);
        }
        return 1;
    }
}
