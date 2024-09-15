package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.file.PlayerData;
import org.minerail.twister.game.Game;
import org.minerail.twister.util.PlayerUtil;

public class Leave implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("leave")
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
        Player p = Twister.get().getServer().getPlayer(sender.getName());
        if (PlayerUtil.playerIsInGame(p)) {
            PlayerUtil.playerRemoveFromGame(p);
            sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_LEAVE_SUCCESS,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));

            Twister.get().getServer().broadcast(MessageProvider.get(MessageKey.MESSAGES_COMMAND_LEAVE_BROADCAST,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)),
                    Placeholder.component("player", Component.text(sender.getName())),
                    Placeholder.component("remainplayers", Component.text(Game.players.size()))));
            p.teleport(PlayerData.get(p).getPlayerJoinLocationFromData());
            return 1;
        } else {
            p.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_LEAVE_PLAYER_IS_NOT_IN_GAME,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));
        }
        return 1;
    }
}
