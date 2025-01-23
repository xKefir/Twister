package org.minerail.twister.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.config.Config;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.util.TextFormatUtil;

public class TypeSelector {

    private Gui gui;

    public TypeSelector(Player p) {
        createGui();
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
        for (String type : Blocks.getAllTypes()) {
            GuiItem item = ItemBuilder.from(Blocks.getMaterialFromSettings(type))
                    .name(Blocks.getItemNameFromSettings(type))
                    .lore(Blocks.getItemLoreFromSettings(type))
                    .asGuiItem(event -> {
                        new SizeSelector(type, (Player) event.getWhoClicked());
                    });
            setItem(item);
        }
    }

    private void setItem(GuiItem item) {
        gui.addItem(item);
    }
}
