package org.minerail.twister;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.minerail.twister.command.CommandTw;
import org.minerail.twister.event.EventListener;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.leaderboard.Leaderboard;
import org.minerail.twister.file.message.MessageProviderLoader;
import org.minerail.twister.file.playerdata.PlayerData;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.hook.PlaceholderApiHook;
import org.minerail.twister.task.PlayerDataSaveTask;

public final class Twister extends JavaPlugin {
    private static Twister instance;
    private Leaderboard leaderboard;
    private static MessageProviderLoader messages;
    private static ConfigFile config;
    private static GameController controller;

    public static Twister get() {return instance;}

    public static MessageProviderLoader getMessages() {
        return messages;
    }
    public static ConfigFile getConfigFile() {
        return config;
    }
    public static GameController getGameController() {
        return controller;
    }

    @Override
    public void onEnable() {
        instance = this;

        config = new ConfigFile();
        messages = new MessageProviderLoader();
        leaderboard = new Leaderboard(getDataFolder());
        controller = new GameController();
        PlayerData.initLeaderboardIntegration(leaderboard);

        getServer().getPluginManager().registerEvents(new EventListener(), this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, e ->
                new CommandTw().register(e.registrar())
        );
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderApiHook(this).register();
        }
        reloadAll();
        savePlayerData();
    }

    @Override
    public void onDisable() {
        PlayerData.saveAll();
        if (leaderboard != null) {
            leaderboard.save();
        }
    }

    public static void reloadAll() {
        config.reload();
        Blocks.create();
        PlayerData.saveAll();
        messages.reload();
    }

    private void savePlayerData() {
        PlayerDataSaveTask saveTask = new PlayerDataSaveTask();
        saveTask.start(72000L);
    }
}
