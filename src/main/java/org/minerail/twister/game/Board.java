package org.minerail.twister.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class Board {
    private static final Random RANDOM = new Random();
    private static List<Material> staticMaterials = new ArrayList<>();

    private final Location loc1;
    private final Location loc2;
    private final int x1;
    private final int x2;
    private final int z1;
    private final int z2;
    private final int y;
    private final int fieldSize;
    private final List<Material> materials;

    public Board(Location loc1, Location loc2, int fieldSize, Set<String> materialKeys) {
        new CoordinateAdjuster(loc1, loc2);
        this.loc1 = CoordinateAdjuster.getLoc1();
        this.loc2 = CoordinateAdjuster.getLoc2();

        this.x1 = Math.min(this.loc1.getBlockX(), this.loc2.getBlockX());
        this.x2 = Math.max(this.loc1.getBlockX(), this.loc2.getBlockX());
        this.z1 = Math.min(this.loc1.getBlockZ(), this.loc2.getBlockZ());
        this.z2 = Math.max(this.loc1.getBlockZ(), this.loc2.getBlockZ());
        this.y = this.loc1.getBlockY();
        this.fieldSize = fieldSize;
        this.materials = convertToMaterialList(materialKeys);

        staticMaterials = new ArrayList<>(this.materials);
    }

    private List<Material> convertToMaterialList(Set<String> materialKeys) {
        List<Material> materialList = new ArrayList<>();
        for (String key : materialKeys) {
            Material material = Material.getMaterial(key);
            if (material != null) {
                materialList.add(material);
            }
        }
        return materialList;
    }

    public void fillAreasWithMaterials() {
        World world = loc1.getWorld();
        if (world == null) return;

        for (int x = x1; x <= x2; x += fieldSize) {
            for (int z = z1; z <= z2; z += fieldSize) {
                Material randomMaterial = getRandomMaterial();
                fillArea(world, x, y, z, randomMaterial);
            }
        }
    }

    private void fillArea(World world, int startX, int startY, int startZ, Material material) {
        int endX = Math.min(startX + fieldSize, x2 + 1);
        int endZ = Math.min(startZ + fieldSize, z2 + 1);

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                Block block = world.getBlockAt(x, startY, z);
                block.setType(material);
            }
        }
    }

    public static Material getRandomMaterial() {
        if (staticMaterials.isEmpty()) {
            return Material.AIR;
        }
        return staticMaterials.get(RANDOM.nextInt(staticMaterials.size()));
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

    public void clearArena() {
        World world = loc1.getWorld();
        if (world == null) return;

        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                Block block = world.getBlockAt(x, y, z);
                block.setType(Material.AIR);
            }
        }
    }
    public void clearVariables() {
        staticMaterials.clear();
        this.materials.clear();
    }
}
