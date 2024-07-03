package org.minerail.twister;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.minerail.twister.command.CommandTw;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.Config;
import org.minerail.twister.event.EventListener;


public final class Twister extends JavaPlugin {

    public static Plugin get() {
        return Bukkit.getPluginManager().getPlugin("Twister");
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        Config.create();
        Config.reload();
        Blocks.create();
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, e ->
                new CommandTw().register(e.registrar())
        );
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
