package org.minerail.twister;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.minerail.twister.command.CommandTw;
import org.minerail.twister.event.EventListener;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.Config.Config;
import org.minerail.twister.file.Message.MessageProviderLoader;
import org.minerail.twister.file.PlayerData;


public final class Twister extends JavaPlugin {

    public static Plugin get() {
        return Bukkit.getPluginManager().getPlugin("Twister");
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        reloadAll();
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, e ->
                new CommandTw().register(e.registrar())
        );
        savePlayerData();
    }
    private void savePlayerData() {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                PlayerData.saveAll();
            }
        };
        task.runTaskLaterAsynchronously(this, 72000L);
    }
    public static void reloadAll() {
        Config.reload();
        Blocks.create();
        MessageProviderLoader.reload();
    }

    @Override
    public void onDisable() {
        PlayerData.saveAll();
    }
}
