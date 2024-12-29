package org.infernworld.infernwarp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public final class InfernWarp extends JavaPlugin {

    public static FileConfiguration config;
    public static File Config;
    public static FileConfiguration data;
    public static File Data;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        createDataFile();
        Config = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(Config);
        Data = new File(getDataFolder(), "data.yml");
        data = YamlConfiguration.loadConfiguration(Data);
        WarpCommand warpCommand = new WarpCommand(this, data, Data);
        getCommand("setwarp").setExecutor(warpCommand);
        getCommand("warp").setExecutor(warpCommand);
        getCommand("delwarp").setExecutor(warpCommand);
    }
    public static FileConfiguration getCfg() {
        return config;
    }
    public void createDataFile() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        Data = new File(getDataFolder(), "data.yml");
        if (!Data.exists()) {
            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(Data);
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
