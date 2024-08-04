package io.github.mrgucci1.areaPlanter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreaPlanterCommand implements CommandExecutor {

    private final AreaPlanter plugin;

    public AreaPlanterCommand(AreaPlanter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("areaplanter")) {
            if (!sender.hasPermission("areaplanter.admin")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }

            if (args.length == 0) {
                return false; // Show help message
            }

            switch (args[0].toLowerCase()) {
                case "enable":
                    plugin.setEnabled(true);
                    sender.sendMessage("AreaPlanter enabled.");
                    break;
                case "disable":
                    plugin.setEnabled(false);
                    sender.sendMessage("AreaPlanter disabled.");
                    break;
                case "radius":
                    if (args.length < 2) {
                        return false; // Show help message
                    }
                    try {
                        int newRadius = Integer.parseInt(args[1]);
                        if (newRadius >= 1) {
                            plugin.setPlantingRadius(newRadius);
                            sender.sendMessage("Planting radius set to " + newRadius + ".");
                        } else {
                            sender.sendMessage("Radius must be at least 1.");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Invalid radius. Please enter a number.");
                    }
                    break;
                default:
                    return false; // Show help message
            }
            return true;
        }
        return false;
    }
}

