# AreaPlanter

**Plant crops in a configurable area with a single right-click!**

AreaPlanter is a Bukkit/Spigot/Paper plugin that enhances the farming experience in Minecraft by allowing players to plant crops in a specified area with a single right-click. No more tedious block-by-block planting!

## Example of Planting with radius set to 5:
![Minecraft2024 08 05-14 58 28 11-ezgif com-optimize](https://github.com/user-attachments/assets/04a9bb30-71bb-4e50-9ef8-a5916c9ffc12)

## Features

* **Area Planting:** Right-click on farmland with seeds to plant crops in a configurable area around the clicked block.
* **Customizable Radius:** Server admins can easily adjust the planting radius by editing the `config.yml` file.
* **Seed Efficiency:** The plugin consumes only the necessary number of seeds based on the actual planting area and available farmland.
* **Vanilla Seed Support:** Supports all vanilla Minecraft seeds that can be planted on farmland.
* **Prioritized Seed Consumption:**  **Seeds are consumed from the player's inventory first, preserving the stack in their hand whenever possible.** 

## Installation

1. **Download:** Download the latest `AreaPlanter.jar` file from the releases page.
2. **Install:** Place the `AreaPlanter.jar` file into your server's `plugins` folder.
3. **Start/Reload:** Start or reload your server to enable the plugin.
4. **Configure:**  Adjust the planting radius and seed consumption behavior in the generated `config.yml` file within the `plugins/AreaPlanter` folder.

## Configuration

The `config.yml` file in the `plugins/AreaPlanter` folder allows you to customize the plugin's behavior:

* **planting-radius:**  Sets the default planting radius (default: 3). Make sure to use an odd number to ensure a central planting block.
* **consume-from-inventory-first:**  Enables or disables prioritizing seed consumption from the inventory (default: `true`).

## How it Works

1. **Right-Click:** When a player right-clicks on farmland with seeds in their hand, the plugin checks the surrounding area based on the configured radius.
2. **Planting:** If there's enough farmland and seeds available, the plugin plants crops around the clicked block.
3. **Seed Consumption:** 
    * If `consume-from-inventory-first` is enabled, the plugin will first attempt to consume seeds from the player's inventory, leaving the stack in their hand intact if possible. 
    * Otherwise, it will consume seeds directly from the player's hand.

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests on GitHub.

## License

This plugin is licensed under the MIT License. See the `LICENSE` file for details.
