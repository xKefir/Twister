package org.minerail.twister.command;


import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.command.subcommand.*;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.core.Game;
import org.minerail.twister.util.GameUtil;
import org.minerail.twister.util.TextFormatUtil;

import java.util.List;

public class CommandTw {


    @SuppressWarnings("Invalid api usage.")
    public void register(Commands cmds) {
       cmds.register(Twister.get().getPluginMeta(),
       Commands.literal("twister").executes(ctx -> execute(ctx.getSource().getSender())
               )
               .requires(ctx ->
                       ctx.getSender().hasPermission("tw.use")
               )
               .then(new Help().get())
               .then(new Join().get())
               .then(new Leave().get())
               .then(new Reload().get())
               .then(new Start().get())
               .then(new Stop().get())
               .then(new Teleport().get())
               .then(new Top().get())
               .then(new Kick().get())
               .build(), null, List.of("tw")
       );
    }
    private int execute(CommandSender sender) {
        Player p = Twister.get().getServer().getPlayer(sender.getName());
        if (!GameUtil.isPlayerInGame(p)) {
            if (Game.canJoin) {
                GameUtil.addPlayer(p);
                sender.sendMessage(TextFormatUtil.fromMessages(MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_JOINED,
                        Placeholder.component("prefix", TextFormatUtil.fromMessages(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                return 1;
            } else if (Game.gameStarted) {
                sender.sendMessage(TextFormatUtil.fromMessages(MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_GAME_ALREADY_STARTED,
                        Placeholder.component("prefix", TextFormatUtil.fromMessages(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                return 1;
            } else {
                sender.sendMessage(TextFormatUtil.fromMessages(MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_EVENT_NOT_STARTED,
                        Placeholder.component("prefix", TextFormatUtil.fromMessages(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                return 1;

            }
        } else {
            p.sendMessage(TextFormatUtil.fromMessages(MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_PLAYER_IS_ALREADY_IN_GAME,
                    Placeholder.component("prefix", TextFormatUtil.fromMessages(MessageKey.MESSAGES_PREFIX_STRING))));
            return 1;
        }

    }
}

