package org.minerail.twister.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.minerail.twister.Twister;
import org.minerail.twister.file.Config.Config;
import org.minerail.twister.file.Config.ConfigKey;
import org.minerail.twister.file.Message.MessageKey;
import org.minerail.twister.file.Message.MessageProvider;
import org.minerail.twister.file.PlayerData;
import org.minerail.twister.game.Game;
import org.minerail.twister.util.LocationUtil;
import org.minerail.twister.util.PlayerUtil;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (PlayerUtil.playerIsInGame(e.getPlayer())) {
            PlayerUtil.playerRemoveFromGame(e.getPlayer());
            PlayerData.get(e.getPlayer()).addTotalPlayed(-1);
            PlayerData.get(e.getPlayer()).addLoses(-1);
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().getLocation().getY() <= Config.getDouble(ConfigKey.ARENA_POS1_Y) - 1 && Game.gameStarted
                && PlayerUtil.playerIsInGame(e.getPlayer())) {
            PlayerUtil.playerRemoveFromGame(e.getPlayer());
            Twister.get().getServer().broadcast(MessageProvider.get(MessageKey.MESSAGES_GAME_PLAYER_LOSE_BROADCAST,
                    Placeholder.component("player", Component.text(e.getPlayer().getName())),
                    Placeholder.component("remainplayers", Component.text(Game.players.size())),
                    Placeholder.component("prefix", MessageProvider.get(MessageKey.MESSAGES_PREFIX_STRING))
                    ));
            e.getPlayer().teleport(LocationUtil.serializeLocation(
                    Config.getDouble(ConfigKey.ARENA_LOSE_POS_X),
                    Config.getDouble(ConfigKey.ARENA_LOSE_POS_Y),
                    Config.getDouble(ConfigKey.ARENA_LOSE_POS_Z),
                    Config.getString(ConfigKey.ARENA_WORLD)
            ));
        }
    }
}
