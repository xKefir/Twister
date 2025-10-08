package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.minerail.twister.Twister;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.core.Game;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.MessageDeliverUtil;

public class Stop implements SubCommand {
    private GameController controller = Twister.get().getGameController();
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("stop")
                .requires(ctx -> ctx.getSender().hasPermission("tw.admin"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }

    public int execute(CommandSender sender) {
        if (controller.getCurrentLobbyState() != GameController.LobbyState.NONE || controller.getCurrentLobbyState() != GameController.LobbyState.OPEN) {
            controller.getGameInstance().stop();
            broadcast(sender);
            return 1;
        } else if (controller.getCurrentLobbyState() == GameController.LobbyState.OPEN) {
            controller.stopLobby();
            broadcast(sender);
            return 1;
        } else {
            MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_STOP_EVENT_IS_NOT_ACTIVE);
            return 1;
        }
    }

    private void broadcast(CommandSender sender) {
        MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_STOP_TO_SENDER);
        MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_COMMAND_STOP_BROADCAST);
    }
}
