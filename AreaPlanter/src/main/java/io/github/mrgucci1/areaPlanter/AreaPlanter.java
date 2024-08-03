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
public final class AreaPlanter extends JavaPlugin implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();

        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand(); // Or getItemInHand()

        if (isValidFarmland(clickedBlock) && isValidSeed(heldItem)) {
            int seedsUsed = plant3x3Area(clickedBlock, heldItem.getType(), player);
            consumeSeed(player, seedsUsed);
        }
    }

    private int plant3x3Area(Block centerBlock, Material seedType, Player player) {
        int seedsPlanted = 0;
        int seedsAvailable = player.getInventory().getItemInMainHand().getAmount(); // Or getItemInHand()

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block blockToPlant = centerBlock.getRelative(x, 1, z);
                if (isValidFarmland(blockToPlant) && seedsPlanted < seedsAvailable) {
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

    @Override
    public void onEnable() {
        getLogger().info("**********AreaPlanter has been enabled!");
        getServer().getPluginManager().registerEvents(this, this); // Register events
    }

    @Override
    public void onDisable() {
        getLogger().info("AreaPlanter has been disabled.");
    }
}
