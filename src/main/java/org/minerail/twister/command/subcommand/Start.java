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
import org.minerail.twister.Twister;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.core.Game;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.gui.TypeSelector;
import org.minerail.twister.util.MessageDeliverUtil;


import static io.papermc.paper.command.brigadier.Commands.argument;

public class Start implements SubCommand {

    private static int num = 0;
    private static String executor;
    private final GameController controller = Twister.getGameController();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("start")
                .requires(ctx -> ctx.getSender().hasPermission("tw.admin"))
                .executes(ctx ->
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
                        })
                            .executes(ctx -> {
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
        if (Twister.getConfigFile().getBoolean(ConfigKey.SETTINGS_GUI_MODULE_ENABLED) && num != 1) {
            new TypeSelector(Bukkit.getPlayer(sender.getName()));
            executor = sender.getName();
            return 1;
        }
        if (i != -1 && num != 1) {
            firstTime(sender, i, type);
            return 1;
        } else if (num == 1) {
            secondTime(sender);
            return 1;
        } else {
            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_ERRORS_EVENT_CANNOT_START);
            return 1;
        }
    }

    private void firstTime(CommandSender sender, int fieldSize, String type) {
        if (fieldSize <= Twister.getConfigFile().getInt(ConfigKey.SETTINGS_GAME_MAX_FIELD_SIZE)) {
            Game.runLobby(fieldSize, type);
            num = 1;

            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_START_FIRST_TIME_TO_SENDER,
                    Placeholder.component("poolsize", Component.text(fieldSize)),
                    Placeholder.component("type", Component.text(type)));

            MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_COMMAND_START_FIRST_TIME_BROADCAST);
            executor = sender.getName();
        } else {
            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_START_FIELD_SIZE_TOO_LARGE,
                    Placeholder.component("poolsize", Component.text(Twister.getConfigFile().getInt(ConfigKey.SETTINGS_GAME_MAX_FIELD_SIZE))));
        }
    }

    private void secondTime(CommandSender sender) {
        if (sender.getName().equals(executor)) {
            if (controller.prepareToStartGame()) {
                num++;
                MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_START_SECOND_TIME_TO_SENDER);
                MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_COMMAND_START_SECOND_TIME_BROADCAST);
                num = 0;
            } else {
                MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_COMMAND_START_SECOND_TIME_TO_SENDER_ERROR);
            }
        } else {
            MessageDeliverUtil.sendWithPrefix(sender, MessageKey.MESSAGES_ERRORS_EVENT_CANNOT_START);
        }
    }
}

