package org.minerail.twister;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.minerail.twister.command.CommandTw;
import org.minerail.twister.event.EventListener;
import org.minerail.twister.file.Blocks;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.file.message.MessageProviderLoader;
import org.minerail.twister.file.playerdata.PlayerData;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.hook.PlaceholderApiHook;
import org.minerail.twister.task.PlayerDataSaveTask;
import org.minerail.twister.util.LogUtil;

public final class Twister extends JavaPlugin {
    private static Twister instance;
    public static Twister get() {return instance;}
    /*private Leaderboard leaderboard;*/
    private  ConfigFile config;
    private  MessageProviderLoader messages;
    private  GameController controller;


    public ConfigFile getConfigFile() {
        return config;
    }
    public MessageProviderLoader getMessages() {
        return messages;
    }
    public GameController getGameController() {
        return controller;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = new ConfigFile();
        messages = new MessageProviderLoader();
        /*leaderboard = new Leaderboard(getDataFolder());*/
        /*PlayerData.initLeaderboardIntegration(leaderboard);*/
        reloadAll();
        controller = new GameController();
        controller.afterRunServer();
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, e ->
                new CommandTw().register(e.registrar())
        );
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderApiHook(this).register();
        }
        checkDebugger();
        savePlayerData();
    }

    private void checkDebugger() {
        LogUtil.setDebug(config.getBoolean(ConfigKey.SETTINGS_PLUGIN_DEBUG));
    }

    @Override
    public void onDisable() {
        PlayerData.saveAll();
//        if (leaderboard != null) {
//            leaderboard.save();
//        }
    }

    public void reloadAll() {
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
