package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.minerail.twister.Twister;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.util.TextFormatUtil;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.argument;

public class Top /*implements SubCommand*/ {
//    @Override
//    public LiteralArgumentBuilder<CommandSourceStack> get() {
//        return Commands.literal("top")
//                .requires(ctx -> ctx.getSender().hasPermission("tw.use"))
//                .then(argument("type", StringArgumentType.string())
//                        .suggests((context, builder) -> {
//                            Leaderboard.SUPPORTED_KEYS.forEach(builder::suggest);
//                            return builder.buildFuture();
//                        }).executes(ctx -> execute(ctx.getSource().getSender(), ctx.getArgument("type", String.class) )
//
//                ));
//
//    }
//
//    public int execute(CommandSender sender, String category) {
//        List<Component> messages;
//
//        messages = TextFormatUtil.formatTopList(new LeaderboardHandler(new Leaderboard(Twister.get().getDataFolder())).getTopEntries(category));
//        Component combinedMessage = Component.empty();
//        for (Component message : messages) {
//            combinedMessage = combinedMessage.append(message).append(Component.text("\n"));
//        }
//        sender.sendMessage(TextFormatUtil.fromMessages(MessageKey.MESSAGES_COMMAND_TOP_HEADER,
//                Placeholder.component("top", Component.text(category))));
//        sender.sendMessage(combinedMessage);
//        return 1;
//    }
}

