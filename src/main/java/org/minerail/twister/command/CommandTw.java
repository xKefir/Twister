package org.minerail.twister.command;

import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.command.subcommand.*;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.MessageDeliverUtil;

import java.util.List;

public class CommandTw {

    private GameController controller =  Twister.get().getGameController();

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
//               .then(new Top().get())
               .then(new Kick().get())
               .build(), null, List.of("tw")
       );
    }
    public int execute(CommandSender sender) {
        Player p = Twister.get().getServer().getPlayer(sender.getName());

        if (controller.getCurrentLobbyState() == GameController.LobbyState.CLOSED) {
            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_GAME_ALREADY_STARTED);
            return 1;
        }

        if (controller.getCurrentLobbyState() == GameController.LobbyState.NONE) {
            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_EVENT_NOT_STARTED);
            return 1;
        }

        if (controller.getPlayerHandler().addPlayer(p)) {
            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_JOINED);
        } else {
            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_WITHOUT_ARGS_PLAYER_IS_ALREADY_IN_GAME);
        }
        return 1;
    }

}

