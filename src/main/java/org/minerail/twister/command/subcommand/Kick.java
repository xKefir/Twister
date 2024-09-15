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
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.game.Game;
import org.minerail.twister.util.PlayerUtil;

import static io.papermc.paper.command.brigadier.Commands.argument;

public class Kick implements SubCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("kick")
                .requires(ctx -> ctx.getSender().hasPermission("tw.admin"))
                .then(argument("target", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            Game.players.forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            return execute(ctx.getSource().getSender(),
                                    ctx.getArgument("target", String.class));
                        }
                        ));
    }



    public int execute(CommandSender sender, String target) {
        if (PlayerUtil.playerIsInGame(Twister.get().getServer().getPlayer(target))) {
            Player target1 = Twister.get().getServer().getPlayer(target);
            PlayerUtil.playerRemoveFromGame(target1);
            target1.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_KICK_TO_TARGETED_PLAYER,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))));
            sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_KICK_TO_SENDER,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)),
                    Placeholder.component("player", Component.text(target))));
            return 1;
        } else {
            sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_KICK_PLAYER_IS_NOT_IN_GAME_TO_SENDER,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)),
                    Placeholder.component("player", Component.text(target))));
            return 1;
        }
    }
}
