package org.minerail.twister.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.file.message.MessageKey;
import org.minerail.twister.hook.WorldGuardHook;

import java.util.List;
import java.util.Set;

public class MessageDeliverUtil {

    // Basic methods
    public static void send(CommandSender target, MessageKey key, TagResolver... resolvers) {
        Component msg = TextFormatUtil.fromMessages(key, resolvers);
        target.sendMessage(msg);
    }

    public static void sendList(CommandSender target, MessageKey key, TagResolver... resolvers) {
        List<Component> lines = TextFormatUtil.fromMessagesList(key, resolvers);
        for (Component line : lines) {
            target.sendMessage(line);
        }
    }

    public static void sendActionBar(Player player, MessageKey key, TagResolver... resolvers) {
        Component msg = TextFormatUtil.fromMessages(key, resolvers);
        player.sendActionBar(msg);
    }

    public static void sendBroadcast(MessageKey key, TagResolver... resolvers) {
        Component msg = TextFormatUtil.fromMessages(key, resolvers);
        Bukkit.broadcast(msg);
    }

    // Methods using prefix
    public static void sendWithPrefix(CommandSender target, MessageKey key, TagResolver... resolvers) {
        Component msg = TextFormatUtil.withPrefix(key, resolvers);
        target.sendMessage(msg);
    }
    public static void sendWithPrefix(Player target, MessageKey key, TagResolver... resolvers) {
        Component msg = TextFormatUtil.withPrefix(key, resolvers);
        target.sendMessage(msg);
    }

    public static void sendListWithPrefix(CommandSender target, MessageKey key, TagResolver... resolvers) {
        List<Component> lines = TextFormatUtil.withPrefixList(key, resolvers);
        for (Component line : lines) {
            target.sendMessage(line);
        }
    }

    public static void sendActionBarWithPrefix(Player player, MessageKey key, TagResolver... resolvers) {
        Component msg = TextFormatUtil.withPrefix(key, resolvers);
        player.sendActionBar(msg);
    }

    public static void sendBroadcastWithPrefix(MessageKey key, TagResolver... resolvers) {
        Component msg = TextFormatUtil.withPrefix(key, resolvers);
        Bukkit.broadcast(msg);
    }

    public static void sendToListOfPlayersWithPrefix(Set<Player> players, MessageKey key, TagResolver... resolvers) {
        if (!WorldGuardHook.getInstance().getPlayersInRegion(Twister.get().getConfigFile().getString(ConfigKey.SETTINGS_HOOK_WORLDGUARD_GAME_REGION)).isEmpty()) {
            for (Player p : WorldGuardHook.getInstance().getPlayersInRegion(Twister.get().getConfigFile().getString(ConfigKey.SETTINGS_HOOK_WORLDGUARD_GAME_REGION))) {
                sendWithPrefix(p, key, resolvers);
            }
        } else {
            for (Player p : players) {
                sendWithPrefix(p, key, resolvers);
            }
        }
    }
}