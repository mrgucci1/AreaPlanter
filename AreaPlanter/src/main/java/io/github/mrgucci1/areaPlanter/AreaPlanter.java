package io.github.mrgucci1.areaPlanter;

import org.bukkit.GameMode;
import org.bukkit.Sound;
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
    private boolean consumeFromInventoryFirst;


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();

        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand(); // Or getItemInHand()

        if (isValidFarmland(clickedBlock) && isValidSeed(heldItem)) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ITEM_CROP_PLANT, 1.0f, 1.0f); // Adjust sound and volume as needed
            int seedsUsed = plantArea(clickedBlock, heldItem.getType(), player);
            player.swingMainHand();
            if (player.getGameMode() != GameMode.CREATIVE) {
                consumeSeed(player, seedsUsed);
            }
        }
    }

    private int plantArea(Block centerBlock, Material seedType, Player player) {
        int seedsPlanted = 0;
        int seedsInInventory = 0;
        if (player.getGameMode() == GameMode.CREATIVE) {
            seedsInInventory = Integer.MAX_VALUE;
        } else {
            // The player's inventory includes the number of seeds in hand
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == seedType) {
                    seedsInInventory += item.getAmount();
                }
            }
        }

        // Calculate the distance the planted crops will travel from the center block
        int distanceFromCenter = plantingRadius / 2;

        for (int x = -distanceFromCenter; x <= distanceFromCenter; x++) {
            for (int z = -distanceFromCenter; z <= distanceFromCenter; z++) {
                Block blockBelow = centerBlock.getRelative(x, 0, z);
                Block blockToPlant = centerBlock.getRelative(x, 1, z);
                if (isValidFarmland(blockBelow) && seedsPlanted < seedsInInventory && blockToPlant.getType() == Material.AIR) {
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
        if (consumeFromInventoryFirst) {
            consumeSeedFromInventoryFirst(player, seedsUsed);
        } else {
            consumeSeedFromHand(player, seedsUsed);
        }
    }

    private void consumeSeedFromHand(Player player, int seedsUsed) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem.getAmount() <= seedsUsed) {
            // Player doesn't have enough seeds, so clear the item from their hand
            player.getInventory().setItemInMainHand(null);
        } else {
            // Remove the used seeds from the stack
            heldItem.setAmount(heldItem.getAmount() - seedsUsed);
        }
    }

    private void consumeSeedFromInventoryFirst(Player player, int seedsUsed) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Material seedType = heldItem.getType();

        // 1. Check inventory for matching seeds
        int seedsFoundInInventory = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == seedType && i != player.getInventory().getHeldItemSlot()) { // Exclude the hand slot
                seedsFoundInInventory += item.getAmount();
            }
        }

        // 2. Calculate how many seeds to consume from hand vs. inventory
        int seedsToConsumeFromHand = Math.max(0, seedsUsed - seedsFoundInInventory);
        int seedsToConsumeFromInventory = seedsUsed - seedsToConsumeFromHand;

        // 3. Consume seeds from inventory
        if (seedsToConsumeFromInventory > 0) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() == seedType && i != player.getInventory().getHeldItemSlot()) {
                    int amountToRemove = Math.min(item.getAmount(), seedsToConsumeFromInventory);

                    if (amountToRemove == item.getAmount()) {
                        player.getInventory().setItem(i, null); // Remove the entire stack if it's fully consumed
                    } else {
                        item.setAmount(item.getAmount() - amountToRemove);
                    }

                    seedsToConsumeFromInventory -= amountToRemove;
                    if (seedsToConsumeFromInventory == 0) {
                        break;
                    }
                }
            }
        }

        if (seedsToConsumeFromHand > 0) {
            if (heldItem.getAmount() <= seedsToConsumeFromHand) {
                player.getInventory().setItemInMainHand(null);
            } else {
                heldItem.setAmount(heldItem.getAmount() - seedsToConsumeFromHand);
            }
        }
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
        this.consumeFromInventoryFirst = getConfig().getBoolean("consume-from-inventory-first", true);
        this.plantingRadius = plantingRadius;
    }

    @Override
    public void onDisable() {
        getLogger().info("AreaPlanter has been disabled.");
    }
}
