package org.minerail.twister.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.minerail.twister.command.subcommand.Start;
import org.minerail.twister.file.config.Config;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.file.message.MessageProvider;
import org.minerail.twister.game.Game;
import org.minerail.twister.util.PlayerUtil;
import org.minerail.twister.util.TextFormatUtil;

public class SizeSelector {

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
                .title(TextFormatUtil.format(Config.getString(ConfigKey.SETTINGS_GUI_TITLE)))
                .rows(Config.getInt(ConfigKey.SETTINGS_GUI_SIZE))
                .create();
    }

    private void createItems() {
        for (int i = 1; i <= Config.getInt(ConfigKey.SETTINGS_GAME_MAX_FIELD_SIZE); i++) {
            int size = i;
            GuiItem item = ItemBuilder.from(new ItemStack(Material.valueOf(Config.getString(ConfigKey.SETTINGS_GUI_SIZESELECTOR_ITEMS_TYPE)), i))
                    .name(TextFormatUtil.format("&e&l" + i))
                    .amount(Config.getBoolean(ConfigKey.SETTINGS_GUI_SIZESELECTOR_ITEMS_SHOW_AMOUNTS) ? i : 1)
                    .asGuiItem(event -> {
                        Game.runLobby(size, type);
                        Game.lobbyIsOpen = true;
                        Start.num++;
                        Start.executor = event.getWhoClicked().getName();
                        PlayerUtil.playerJoinToGame((Player) event.getWhoClicked());
                        gui.close(event.getWhoClicked());
                        event.getWhoClicked().sendMessage(MessageProvider.get(MessageKey.MESSAGES_COMMAND_START_FIRST_TIME_TO_SENDER,
                                Placeholder.component("poolsize", Component.text(size)),
                                Placeholder.component("type", Component.text(type)),
                                Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                        );
                        Bukkit.broadcast(MessageProvider.get(MessageKey.MESSAGES_COMMAND_START_FIRST_TIME_BROADCAST,
                                Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING)))
                        );
                    });
            setItem(item);
        }
    }

    private void setItem(GuiItem item) {
        gui.addItem(item);
    }
}
