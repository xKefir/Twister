package org.minerail.twister.command;


import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.minerail.twister.Twister;
import org.minerail.twister.command.subcommand.*;

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
               .build(), null, List.of("tw")
       );
    }
    private int execute(CommandSender sender) {
        return 1;
    }
}

