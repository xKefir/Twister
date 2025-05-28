package org.minerail.twister.game.board;

import org.bukkit.Location;
import org.bukkit.World;
import org.minerail.twister.file.config.ConfigKey;
import org.minerail.twister.util.LocationUtil;

import java.util.*;

public class BoardBuilder {
    private final Location maxCorner = LocationUtil.createLocation(ConfigKey.ARENA_POS1_X, ConfigKey.ARENA_POS1_Y, ConfigKey.ARENA_POS1_Z);
    private final Location minCorner = LocationUtil.createLocation(ConfigKey.ARENA_POS2_X, ConfigKey.ARENA_POS2_Y, ConfigKey.ARENA_POS2_Z);
    private int y;
    private int fieldSize;
    private Set<String> materials = new HashSet<>();

    public BoardBuilder setCorners() {
        // logika min/max jak wcześniej
        int minX = Math.min(minCorner.getBlockX(), maxCorner.getBlockX());
        int maxX = Math.max(minCorner.getBlockX(), maxCorner.getBlockX());
        int minZ = Math.min(minCorner.getBlockZ(), maxCorner.getBlockZ());
        int maxZ = Math.max(minCorner.getBlockZ(), maxCorner.getBlockZ());
        World world = minCorner.getWorld();

        this.y = minCorner.getBlockY();
        return this;
    }

    public BoardBuilder setFieldSize(int size) {
        this.fieldSize = size;
        return this;
    }

    public BoardBuilder setMaterials(Collection<String> materials) {
        this.materials = new HashSet<>(materials);
        return this;
    }

    public Board build() {
        if (minCorner == null || maxCorner == null) {
            throw new IllegalStateException("Corners must be set");
        }
        if (fieldSize <= 0) {
            throw new IllegalStateException("Field size must be positive");
        }
        if (materials.isEmpty()) {
            throw new IllegalStateException("Materials list cannot be empty");
        }
        return new Board(minCorner, maxCorner, y, fieldSize, materials);
    }
}

