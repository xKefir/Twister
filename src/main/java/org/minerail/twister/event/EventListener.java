package org.minerail.twister.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.minerail.twister.Twister;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.file.playerdata.PlayerData;
import org.minerail.twister.game.core.GameController;
import org.minerail.twister.util.LogUtil;
import org.minerail.twister.util.MessageDeliverUtil;
import org.minerail.twister.util.StatsUtil;

public class EventListener implements Listener {
    private ConfigFile config = Twister.get().getConfigFile();
    private GameController controller = Twister.get().getGameController();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (controller.getPlayerHandler().getPlayersList().contains(e.getPlayer())) {

            controller.getPlayerHandler().removePlayer(e.getPlayer());
            StatsUtil.changeStats(e.getPlayer(), "quit");
            MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_COMMAND_LEAVE_BROADCAST,
                    Placeholder.component("player", Component.text(e.getPlayer().getName())),
                    Placeholder.component("remainplayers", Component.text(controller.getPlayerHandler().getPlayersList().size())));
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (controller.getCurrentLobbyState() == GameController.LobbyState.CLOSED)
            if (controller.getPlayerHandler().getPlayersList().contains(e.getPlayer()) && e.getPlayer().getLocation().getY() <= Twister.get().getGameController().getGameInstance().board.getY()) {
                controller.getPlayerHandler().eliminatePlayer(e.getPlayer());
            }

    }
    @EventHandler
    public void onGameStateChange(GameStateChangeEvent e) {
        switch(e.getState()) {
            case WAITING -> LogUtil.debug("GameState: WAITING");
            case BEFORE_ROUND -> LogUtil.debug("GameState: BEFORE_ROUND");
            case COUNTDOWN -> LogUtil.debug("GameState: COUNTDOWN");
            case ROUND_RUNNING -> LogUtil.debug("GameState: ROUND_RUNNING");
            case ROUND_END -> LogUtil.debug("GameState: ROUND_END");
            case FINISHED -> LogUtil.debug("GameState: FINISHED");
        }
    }


    @EventHandler
    public void onPlayerJoinToGame(PlayerJoinedToGame e) {
        PlayerData.get(e.getPlayer()).deserializeAndSavePlayerLastLocation(e.getPlayer().getLocation());
        PlayerData.get(e.getPlayer()).savePlayerInventory(e.getPlayer().getInventory().getContents(), e.getPlayer().getExp(), e.getPlayer().getLevel());
        e.getPlayer().getInventory().clear();
        e.getPlayer().setExperienceLevelAndProgress(0);
        LogUtil.debug("Stored player inventory: " + e.getPlayer().getName());
    }
}
