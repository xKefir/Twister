package org.minerail.twister.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.Twister;
import org.minerail.twister.command.subcommand.Start;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.game.core.Game;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.MessageDeliverUtil;
import org.minerail.twister.util.TextFormatUtil;

public class SizeSelector {
    private final GameController controller = Twister.get().getGameController();
    private Gui gui;
    private String type;

    public SizeSelector(String type,Player p) {
        createGui();
        this.type = type;
        createItems();
        gui.setDefaultClickAction(event -> {event.setCancelled(true);});
        gui.open(p);
    }

    private void createGui() {
        gui = Gui.gui()
                .title(TextFormatUtil.format(Twister.get().getConfigFile().getString(ConfigKey.SETTINGS_GUI_TITLE)))
                .rows(Twister.get().getConfigFile().getInt(ConfigKey.SETTINGS_GUI_SIZE))
                .create();
    }

    private void createItems() {
        for (int i = 1; i <= Twister.get().getConfigFile().getInt(ConfigKey.SETTINGS_GAME_MAX_FIELD_SIZE); i++) {
            int size = i;
            GuiItem item = ItemBuilder.from(new ItemStack(Material.valueOf(Twister.get().getConfigFile().getString(ConfigKey.SETTINGS_GUI_SIZESELECTOR_ITEMS_TYPE)), i))
                    .name(TextFormatUtil.format("&e&l" + i))
                    .amount(Twister.get().getConfigFile().getBoolean(ConfigKey.SETTINGS_GUI_SIZESELECTOR_ITEMS_SHOW_AMOUNTS) ? i : 1)
                    .asGuiItem(event -> {
                        controller.setupLobby(size, type);
                        Start.num++;
                        Start.executor = event.getWhoClicked().getName();

                        controller.getPlayerHandler().addPlayer((Player) event.getWhoClicked());
                        gui.close(event.getWhoClicked());
                        MessageDeliverUtil.sendWithPrefix(event.getWhoClicked(), MessageKey.MESSAGES_COMMAND_START_FIRST_TIME_TO_SENDER,
                                Placeholder.component("poolsize", Component.text(size)),
                                Placeholder.component("type", Component.text(type)));

                        MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_COMMAND_START_FIRST_TIME_BROADCAST);

                    });
            setItem(item);
        }
    }

    private void setItem(GuiItem item) {
        gui.addItem(item);
    }
}
