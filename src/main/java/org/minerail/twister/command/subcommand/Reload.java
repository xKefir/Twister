package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;

public class Reload implements SubCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("tw.use"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }


    public int execute(CommandSender sender) {
        try {
            sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_RELOAD_SUCCESS,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))
            ));
            Twister.reloadAll();
            return 1;
        } catch (Exception e) {
            sender.sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_RELOAD_ERROR,
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))
            ));
            return 1;
        }
    }
}
