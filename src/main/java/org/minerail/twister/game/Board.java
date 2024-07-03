package org.minerail.twister.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Board {
    private final Location loc1;
    private final Location loc2;
    private final int x1;
    private final int x2;
    private final int z1;
    private final int z2;
    private final int y;
    private final int area;
    private final int fieldSize;
    private final List<Material> materials;
    private final Random random = new Random();

    public Board(Location loc1, Location loc2, int fieldSize, Set<String> materials) {
        this.loc1 = alignToGrid(loc1, fieldSize);
        this.loc2 = alignToGrid(loc2, fieldSize);
        this.x1 = Math.min(this.loc1.getBlockX(), this.loc2.getBlockX());
        this.x2 = Math.max(this.loc1.getBlockX(), this.loc2.getBlockX());
        this.z1 = Math.min(this.loc1.getBlockZ(), this.loc2.getBlockZ());
        this.z2 = Math.max(this.loc1.getBlockZ(), this.loc2.getBlockZ());
        this.y = loc1.getBlockY();
        this.area = calculateFieldArea();
        this.fieldSize = fieldSize;
        this.materials = convertToMaterialList(materials);
    }

    private Location alignToGrid(Location loc, int fieldSize) {
        int x = (loc.getBlockX() / fieldSize) * fieldSize;
        int z = (loc.getBlockZ() / fieldSize) * fieldSize;
        return new Location(loc.getWorld(), x, loc.getBlockY(), z);
    }

    private int calculateFieldArea() {
        int width = (x2 - x1) + 1;
        int depth = (z2 - z1) + 1;
        return width * depth;
    }

    public int getFieldArea() {
        return this.area;
    }

    public void fillAreasWithRandomMaterial() {
        Material randomMaterial = getRandomMaterial();
        fillAreasWithMaterial(randomMaterial);
    }

    public void fillAreasWithMaterial(Material material) {
        World world = loc1.getWorld();
        if (world == null) return;

        for (int x = x1; x <= x2; x += fieldSize) {
            for (int z = z1; z <= z2; z += fieldSize) {
                fillArea(world, x, y, z, material);
            }
        }
    }

    private void fillArea(World world, int startX, int y, int startZ, Material material) {
        for (int x = startX; x < startX + fieldSize; x++) {
            for (int z = startZ; z < startZ + fieldSize; z++) {
                if (isWithinBounds(x, z)) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(material);
                }
            }
        }
    }

    private boolean isWithinBounds(int x, int z) {
        return x >= x1 && x <= x2 && z >= z1 && z <= z2;
    }

    private Material getRandomMaterial() {
        return materials.get(random.nextInt(materials.size()));
    }

    private List<Material> convertToMaterialList(Set<String> materialKeys) {
        List<Material> materialList = new ArrayList<>();
        for (String key : materialKeys) {
            Material material = Material.getMaterial(key);
            if (material != null && material.isBlock()) {
                materialList.add(material);
            }
        }
        return materialList;
    }

    public void removeAllBlocksExcept(Material materialToKeep) {
        World world = loc1.getWorld();
        if (world == null) return;

        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                Block block = world.getBlockAt(x, y, z);
                if (block.getType() != materialToKeep) {
                    block.setType(Material.AIR);
                }
            }
        }
    }
}
