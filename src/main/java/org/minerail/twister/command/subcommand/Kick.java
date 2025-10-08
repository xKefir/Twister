package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.core.Game;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.MessageDeliverUtil;

import static io.papermc.paper.command.brigadier.Commands.argument;

public class Kick implements SubCommand {
    private GameController controller =  Twister.get().getGameController();
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("kick")
                .requires(ctx -> ctx.getSender().hasPermission("tw.admin"))
                .then(argument("target", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            controller.getPlayerHandler().getPlayersList().forEach(player -> builder.suggest(player.getName()));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            return execute(ctx.getSource().getSender(),
                                    ctx.getArgument("target", String.class));
                        }
                        ));
    }



    public int execute(CommandSender sender, String target) {
        if (controller.getPlayerHandler().getPlayersList().contains(Twister.get().getServer().getPlayer(sender.getName()))) {
            Player target1 = Twister.get().getServer().getPlayer(target);
            controller.getPlayerHandler().eliminatePlayer(target1);
            MessageDeliverUtil.sendWithPrefix(target1, MessageKey.MESSAGES_COMMAND_KICK_TO_TARGETED_PLAYER);

            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_KICK_TO_SENDER,
                    Placeholder.component("player", Component.text(target)));
            return 1;
        } else {
            MessageDeliverUtil.sendWithPrefix(sender,MessageKey.MESSAGES_COMMAND_KICK_PLAYER_IS_NOT_IN_GAME_TO_SENDER,
                    Placeholder.component("player", Component.text(target)));
            return 1;
        }
    }
}
