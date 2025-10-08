package org.minerail.twister.game.board;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class Board {
    private final Location minCorner;
    private final Location maxCorner;
    private final World world;
    private final int y;
    private final int fieldSize;
    private final List<Material> materials;
    public boolean areaCleared = false;
    private List<Material> materialList = new ArrayList<>();

    public Board(Location minCorner, Location maxCorner, int y, int fieldSize, Set<String> materials) {
        this.world = minCorner.getWorld();
        this.y = minCorner.getBlockY();
        this.fieldSize = fieldSize;
        this.materials = convertToMaterialList(materials);
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
    }

    private final Random RANDOM = new Random();

    private List<Material> convertToMaterialList(Set<String> materialKeys) {
        materialList.clear();
        for (String key : materialKeys) {
            Material material = Material.getMaterial(key);
            if (material != null) {
                materialList.add(material);
            }
        }
        return materialList;
    }

    public void fillAreasWithMaterials() {
        if (this.world == null) return;

        int minX = minCorner.getBlockX();
        int maxX = maxCorner.getBlockX();
        int minZ = minCorner.getBlockZ();
        int maxZ = maxCorner.getBlockZ();

        for (int x = minX; x <= maxX; x += fieldSize) {
            for (int z = minZ; z <= maxZ; z += fieldSize) {
                Material randomMaterial = getRandomMaterial();
                fillArea(x, z, randomMaterial);
            }
        }
    }

    private void fillArea(int startX, int startZ, Material material) {
        int endX = Math.min(startX + fieldSize, maxCorner.getBlockX() + 1);
        int endZ = Math.min(startZ + fieldSize, maxCorner.getBlockZ() + 1);

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                Block block = world.getBlockAt(x, y, z);
                block.setType(material);
            }
        }
    }

    public Material getRandomMaterial() {
        if (materials.isEmpty()) {
            return Material.AIR;
        }
        return materials.get(RANDOM.nextInt(materials.size()));
    }

    public void removeAllBlocksExcept(Material materialToKeep) {
        if (world == null) return;

        iterateBlocks(block -> {
            if (block.getType() != materialToKeep) {
                block.setType(Material.AIR);
            }
        });
        areaCleared = true;
    }

    public void clearArena() {
        if (world == null) return;
        iterateBlocks(block -> block.setType(Material.AIR));
    }

    private void iterateBlocks(BlockAction action) {
        int minX = minCorner.getBlockX();
        int maxX = maxCorner.getBlockX();
        int minZ = minCorner.getBlockZ();
        int maxZ = maxCorner.getBlockZ();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = world.getBlockAt(x, y, z);
                action.execute(block);
            }
        }
    }
    public void cleanupVariables() {
        materials.clear();
    }

    public Location getMinCorner() {
        return minCorner.clone();
    }

    public Location getMaxCorner() {
        return maxCorner.clone();
    }

    public World getWorld() {
        return world;
    }

    public int getY() {
        return y;
    }


    @FunctionalInterface
    private interface BlockAction {
        void execute(Block block);
    }

}
