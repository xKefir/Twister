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
import org.minerail.twister.game.core.Game;
import org.minerail.twister.util.GameUtil;
import org.minerail.twister.util.LogUtil;
import org.minerail.twister.util.MessageDeliverUtil;
import org.minerail.twister.util.StatsUtil;

public class EventListener implements Listener {
    private ConfigFile config = Twister.getConfigFile();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (GameUtil.isPlayerInGame(e.getPlayer())) {

            GameUtil.removePlayer(e.getPlayer());
            StatsUtil.changeStats(e.getPlayer(), "quit");

            MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_COMMAND_LEAVE_BROADCAST,
                    Placeholder.component("player", Component.text(e.getPlayer().getName())),
                    Placeholder.component("remainplayers", Component.text()));
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

            GameUtil.removePlayer(e.getPlayer());

            StatsUtil.changeStats(e.getPlayer(), "lose");

            MessageDeliverUtil.sendWithPrefix(e.getPlayer(), MessageKey.MESSAGES_GAME_PLAYER_LOSE_TO_PLAYER);
            MessageDeliverUtil.sendBroadcastWithPrefix(MessageKey.MESSAGES_GAME_PLAYER_LOSE_BROADCAST,
                    Placeholder.component("player", Component.text(e.getPlayer().getName())),
                    Placeholder.component("remainplayers", Component.text(Game.players.size())));

    }
    @EventHandler
    public void onGameStateChange(GameStateChangeEvent e) {
        switch(e.getState()) {
            case RUNNING -> LogUtil.debug("GameState: RUNNING");
            case WAITING -> LogUtil.debug("GameState: WAITING");
            case LOBBY_OPEN -> LogUtil.debug("GameState: LOBBY_OPEN");
            case COUNTDOWN -> LogUtil.debug("GameState: COUNTDOWN");
            case ROUND_RUNNING -> LogUtil.debug("GameState: ROUND_RUNNING");
            case ROUND_END -> LogUtil.debug("GameState: ROUND_END");
            case FINISHED -> LogUtil.debug("GameState: FINISHED");
        }
    }


}
