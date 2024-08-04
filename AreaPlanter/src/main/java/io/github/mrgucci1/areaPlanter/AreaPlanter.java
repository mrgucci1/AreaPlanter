package io.github.mrgucci1.areaPlanter;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class AreaPlanter extends JavaPlugin implements Listener {

    public AreaPlanter() {

    }
    private int plantingRadius;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();

        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand(); // Or getItemInHand()

        if (isValidFarmland(clickedBlock) && isValidSeed(heldItem)) {
            int seedsUsed = plantArea(clickedBlock, heldItem.getType(), player);
            consumeSeed(player, seedsUsed);
        }
    }

    private int plantArea(Block centerBlock, Material seedType, Player player) {
        int seedsPlanted = 0;
        int seedsAvailable = player.getInventory().getItemInMainHand().getAmount();

        // Calculate the range for the area
        int minX = -plantingRadius;
        int maxX = plantingRadius;
        int minZ = -plantingRadius;
        int maxZ = plantingRadius;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block blockBelow = centerBlock.getRelative(x, 0, z);
                Block blockToPlant = centerBlock.getRelative(x, 1, z);
                if (isValidFarmland(blockBelow) && seedsPlanted < seedsAvailable && blockToPlant.getType() == Material.AIR) {
                    blockToPlant.setType(getCropsFromSeed(seedType));
                    seedsPlanted++;
                }
            }
        }
        return seedsPlanted;
    }

    private boolean isValidFarmland(Block block) {
        return block.getType() == Material.FARMLAND;
    }

    private boolean isValidSeed(ItemStack item) {
        Material itemType = item.getType();
        return itemType == Material.WHEAT_SEEDS
                || itemType == Material.BEETROOT_SEEDS
                || itemType == Material.POTATO
                || itemType == Material.CARROT
                || itemType == Material.PUMPKIN_SEEDS
                || itemType == Material.MELON_SEEDS;
    }

    private Material getCropsFromSeed(Material seedType) {
        switch (seedType) {
            case WHEAT_SEEDS:    return Material.WHEAT;
            case BEETROOT_SEEDS: return Material.BEETROOTS;
            case POTATO:         return Material.POTATOES;
            case CARROT:         return Material.CARROTS;
            case PUMPKIN_SEEDS:  return Material.PUMPKIN_STEM; // Initial stage
            case MELON_SEEDS:    return Material.MELON_STEM;   // Initial stage
            default:             return Material.AIR; // Handle invalid cases
        }
    }

    private void consumeSeed(Player player, int seedsUsed) {
        ItemStack heldItem = player.getInventory().getItemInMainHand(); // Or getItemInHand()

        if (heldItem.getAmount() <= seedsUsed) {
            // Player doesn't have enough seeds, so clear the item from their hand
            player.getInventory().setItemInMainHand(null);
        } else {
            // Remove the used seeds from the stack
            heldItem.setAmount(heldItem.getAmount() - seedsUsed);
        }
    }

    public void loadConfig(){
        saveDefaultConfig();
        this.plantingRadius = getConfig().getInt("planting-radius", 3);
    }

    public void setPlantingRadius(int plantingRadius) {
        getConfig().set("planting-radius", plantingRadius);
        saveConfig();
        this.plantingRadius = plantingRadius;
    }

    @Override
    public void onEnable() {
        getLogger().info("\n"+
                " _____             _____ _         _              _____        _____         _   _       _ \n" +
                "|  _  |___ ___ ___|  _  | |___ ___| |_ ___ ___   |     |___   |   __|___ ___| |_| |___ _| |\n" +
                "|     |  _| -_| .'|   __| | .'|   |  _| -_|  _|  |-   -|_ -|  |   __|   | .'| . | | -_| . |\n" +
                "|__|__|_| |___|__,|__|  |_|__,|_|_|_| |___|_|    |_____|___|  |_____|_|_|__,|___|_|___|___|");
        getServer().getPluginManager().registerEvents(this, this); // Register events

        saveDefaultConfig(); // Create config.yml if it doesn't exist
        int plantingRadius = getConfig().getInt("planting-radius", 3); // Default to 3 if not set
        this.plantingRadius = plantingRadius;
        Objects.requireNonNull(this.getCommand("areaplanter")).setExecutor(new AreaPlanterCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("AreaPlanter has been disabled.");
    }
}
