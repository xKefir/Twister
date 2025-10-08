package org.minerail.twister.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.MessageDeliverUtil;

import java.util.List;

public class Help implements SubCommand {
    private String replacer;
    private GameController controller =  Twister.get().getGameController();
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("help")
                .requires(ctx -> ctx.getSender().hasPermission("tw.use"))
                .executes(ctx -> execute(ctx.getSource().getSender()));
    }
    public int execute(CommandSender sender) {
//        if (GameUtil.getWinningPlayer() == null) {
//            replacer = Twister.getMessages().getString(MessageKey.MESSAGES_CONSTANTS_LAST_WINNER.getPath());
//        } else {
//            replacer = GameUtil.getWinningPlayer().getName();
//        }
        MessageDeliverUtil.sendListWithPrefix(sender, MessageKey.MESSAGES_COMMAND_HELP,
                Placeholder.component("lastwinner", Component.text(replacer)));
        return 1;
    }
}
