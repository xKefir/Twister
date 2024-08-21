package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.Config.Config;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.file.Config.ConfigKey;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.game.Game;


import static io.papermc.paper.command.brigadier.Commands.argument;

public class Start implements SubCommand {

    public static int num = 0;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("start").executes(ctx ->
                {
                    try {
                        return execute(ctx.getSource().getSender(), -1, null);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            )
                .then(argument("size", IntegerArgumentType.integer())
                    .then(argument("type", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            Blocks.getAllTypes().forEach(builder::suggest);
                            return builder.buildFuture();
                        }
                        ).executes(ctx ->
                                    {
                                        try {
                                            return execute(ctx.getSource().getSender(),
                                                    ctx.getArgument("size", Integer.class),
                                                    ctx.getArgument("type", String.class));
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            )));
    }

    public int execute(CommandSender sender, int i, String type) throws InterruptedException {
        if (i != -1 && num != 1) {
            if (i <= Config.getInt(ConfigKey.SETTINGS_GAME_MAX_FIELD_SIZE)) {
                Game.runlLobby(i, type);
                num = 1;
                sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_START_FIRST_TIME_TO_SENDER,
                        Placeholder.component("poolsize", Component.text(i)),
                        Placeholder.component("type", Component.text(type)),
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                Bukkit.broadcast(MessageProvider.get(MessageKey.MESSAGES_COMMAND_START_FIRT_TIME_BROADCAST,
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                return 1;
            } else {
                sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_START_FIELD_SIZE_TOO_LARGE,
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)),
                        Placeholder.component("poolsize", Component.text(Config.getInt(ConfigKey.SETTINGS_GAME_MAX_FIELD_SIZE)))));
            }
            return 1;
        } else if (num == 1) {
            if (Game.players.size() >= Config.getInt(ConfigKey.SETTINGS_GAME_MIN_PLAYERS)) {
                Game.runGame();
                num++;
                sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_START_SECOND_TIME_TO_SENDER,
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                Bukkit.broadcast(MessageProvider.get(MessageKey.MESSAGES_COMMAND_START_SECOND_TIME_BROADCAST,
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                num = 0;
                return 1;
            } else {
                sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_START_SECOND_TIME_TO_SENDER_ERROR,
                        Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                );
                return 1;
            }
        } else {
            sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_ERRORS_EVENT_CANNOT_START,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
            );
            return 1;
        }
    }


}

