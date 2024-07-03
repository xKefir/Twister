package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.game.Game;


import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.argument;

public class Start implements SubCommand {

    private static int num = 0;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("start").executes(ctx ->
                        execute(ctx.getSource().getSender(), 0, null)
            )
                .then(argument("size", IntegerArgumentType.integer())
                        .suggests(((context, builder) -> {
                            List<Integer> arg = List.of(1,2,3,4,5,6,7,8,9,10);
                            arg.forEach(builder::suggest);
                            return builder.buildFuture();
                        }))
                    .then(argument("type", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            Blocks.getAllTypes().forEach(builder::suggest);
                            return builder.buildFuture();
                        }
                        ).executes(ctx ->
                                    execute(ctx.getSource().getSender(),
                                            ctx.getArgument("size", Integer.class),
                                            ctx.getArgument("type", String.class))
                            )));
    }

    public int execute(CommandSender sender, int i, String type) {
        if (num == 1) {
            num++;
            Game.runGame();
        } else if (num == 0) {
            num++;
            Game.runlLobby(i, type);
        }
        return 1;
    }


}

