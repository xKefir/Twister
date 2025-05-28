package org.minerail.twister.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.minerail.twister.Twister;
import org.minerail.twister.file.config.ConfigFile;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.util.LogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class WorldGuardHook {
    private static WorldGuardHook instance;
    private final ConfigFile config = Twister.getConfigFile();

    private WorldGuardHook() {}

    public static WorldGuardHook getInstance() {
        if (instance == null) {
            instance = new WorldGuardHook();
        }
        return instance;
    }

    private boolean isModuleEnabled() {
        return config.getBoolean(ConfigKey.SETTINGS_HOOK_WORLDGUARD_MODULE_ENABLED);
    }

    public boolean isInRegion(Player player, String... regionNames) {
        if (!isModuleEnabled()) {
            return false;
        }

        try {
            ApplicableRegionSet regions = getApplicableRegions(player);
            if (regions == null) {
                return false;
            }

            Set<String> targetRegions = Set.of(regionNames);
            for (ProtectedRegion region : regions) {
                if (targetRegions.contains(region.getId())) {
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            return false;
        }

        return false;
    }

    public boolean isInRegion(Player player, Set<String> regionNames) {
        return isInRegion(player, regionNames.toArray(new String[0]));
    }

    private ApplicableRegionSet getApplicableRegions(Player player) {
        RegionManager regionManager = getRegionManager(player);
        if (regionManager != null) {
            BukkitPlayer bukkitPlayer = getBukkitPlayer(player);
            return regionManager.getApplicableRegions(
                    bukkitPlayer.getLocation().toVector().toBlockPoint()
            );
        }
        return null;
    }

    public List<Player> getPlayersInRegion(String... regionNames) {
        List<Player> playersInRegion = new ArrayList<>();

        if (!isModuleEnabled()) {
            return playersInRegion; // Pusta lista jeśli moduł wyłączony
        }

        Set<String> targetRegions = Set.of(regionNames);

        // Iteruj przez wszystkich graczy online
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                if (isPlayerInRegions(player, targetRegions)) {
                    playersInRegion.add(player);
                }
            } catch (Exception e) {
                // Pomiń gracza przy błędzie, nie crashuj całej metody
                continue;
            }
        }

        return playersInRegion;
    }

    private boolean isPlayerInRegions(Player player, Set<String> regionNames) {
        ApplicableRegionSet regions = getApplicableRegions(player);
        if (regions == null) {
            return false;
        }

        for (ProtectedRegion region : regions) {
            if (regionNames.contains(region.getId())) {
                return true;
            }
        }
        return false;
    }

    public List<Player> getPlayersInRegion(Collection<String> regionNames) {
        return getPlayersInRegion(regionNames.toArray(new String[0]));
    }

    private RegionManager getRegionManager(Player player) {
        return WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(getBukkitPlayer(player).getWorld());
    }

    private BukkitPlayer getBukkitPlayer(Player player) {
        return BukkitAdapter.adapt(player);
    }
}