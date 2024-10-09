package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.file.Message.MessageProviderLoader;
import org.minerail.twister.util.PlayerUtil;

import java.util.List;

public class Help implements SubCommand {
    private String replacer;
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("help")
                .requires(ctx -> ctx.getSender().hasPermission("tw.use"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }
    public int execute(CommandSender sender) {
        List<Component> messages;
        if (PlayerUtil.w == null) {
            replacer = MessageProviderLoader.getString(MessageKey.MESSAGES_CONSTANTS_LAST_WINNER.getPath());
        } else {
            replacer = PlayerUtil.w.getName();
        }
            messages = MessageProvider.getList(MessageKey.MESSAGES_COMMAND_HELP,
                    Placeholder.component("lastwinner", Component.text(replacer)),
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)));


        Component combinedMessage = Component.empty();
        for (Component message : messages) {
            combinedMessage = combinedMessage.append(message).append(Component.text("\n"));
        }
        sender.sendMessage(combinedMessage);
        return 1;
    }
}
