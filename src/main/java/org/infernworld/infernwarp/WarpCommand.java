package org.infernworld.infernwarp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class WarpCommand implements CommandExecutor {
    private static InfernWarp plugin;
    private static FileConfiguration dataConfig;
    private static File dataFile;

    public WarpCommand(InfernWarp plugin, FileConfiguration dataConfig, File dataFile) {
        WarpCommand.plugin = plugin;
        WarpCommand.dataConfig = dataConfig;
        WarpCommand.dataFile = dataFile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда доступна только игрокам!");
            return true;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("setwarp")) {
            if (args.length == 0) {
                player.sendMessage(InfernWarp.getCfg().getString("message.setwarp-not-name"));
                return true;
            }
            String name = args[0].toLowerCase();
            if (dataConfig.getConfigurationSection("warps") == null) {
                dataConfig.createSection("warps");
            }
            if (dataConfig.getConfigurationSection("warps") != null && dataConfig.getConfigurationSection("warps").contains(name)) {
                player.sendMessage(InfernWarp.getCfg().getString("message.warp-not-teleport"));
                return true;
            }
            int maxWarps = getMaxWarps(player);
            int currentWarps = dataConfig.getConfigurationSection("warps") != null ? dataConfig.getConfigurationSection("warps").getKeys(false).size() : 0;
            if (currentWarps >= maxWarps) {
                player.sendMessage(InfernWarp.getCfg().getString("message.limit-warps"));
                return true;
            }
            Location location = player.getLocation();
            dataConfig.set("warps." + name, location);
            saveDataConfig();
            player.sendMessage(InfernWarp.getCfg().getString("message.set-warp"));
            return true;

        } else if (cmd.getName().equalsIgnoreCase("warp")) {
            if (args.length == 0) {
                player.sendMessage(InfernWarp.getCfg().getString("message.warp-not-name"));
                return true;
            }
            String name = args[0].toLowerCase();
            Location location = dataConfig.getLocation("warps." + name);
            if (location == null) {
                player.sendMessage(InfernWarp.getCfg().getString("message.warp-not-found"));
                return true;
            }
            player.sendMessage(InfernWarp .getCfg().getString("message.teleport"));
            player.teleport(location);
            return true;

        } else if (cmd.getName().equalsIgnoreCase("delwarp")) {
            if (!player.hasPermission("infernwarp.delwarp")) {
                player.sendMessage(InfernWarp.getCfg().getString("message.del-not-perms"));
                return true;
            }
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Напишите имя варпа, который хотите удалить!");
                return true;
            }
            String name = args[0].toLowerCase();
            if (dataConfig.getConfigurationSection("warps").contains(name)) {
                dataConfig.set("warps." + name, null);
                saveDataConfig();
                player.sendMessage(InfernWarp.getCfg().getString("message.del-warp"));
            }
            return true;
        }
        return false;
    }

    private int getMaxWarps(Player player) {
        if (InfernWarp.getCfg().contains("limits")) {
            Set<String> keys = InfernWarp.getCfg().getConfigurationSection("limits").getKeys(false);
            for (String key : keys) {
                if (player.hasPermission("infernwarp.setwarp." + key)) {
                    return InfernWarp.getCfg().getInt("limits." + key);
                }
            }
        }
        return 0;
    }

    public static void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
