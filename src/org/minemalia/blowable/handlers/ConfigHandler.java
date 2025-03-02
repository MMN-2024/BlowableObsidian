package org.minemalia.blowable.handlers;

import org.minemalia.blowable.BlowablePlugin;
import org.minemalia.blowable.util.Util;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigHandler {

    public static double getDefaultHealth(Material mat) {
        Map<String, Object> blowableBlocks = BlowablePlugin.instance.getConfig().getConfigurationSection("blocksHealth").getValues(false);
        blowableBlocks = lowerMapKeys(blowableBlocks);
        double damage = 1.0;

        if (blowableBlocks.containsKey(mat.toString().toLowerCase()))
            damage = (double) ((Double) blowableBlocks.get(mat.toString().toLowerCase())).doubleValue();

        else if (blowableBlocks.containsKey("all") && mat != Material.LAVA && mat != Material.WATER && mat != Material.AIR)
            damage = (double) ((Double) blowableBlocks.get("all")).doubleValue();

        return damage;
    }

    public static int getRegenTime() {
        return BlowablePlugin.instance.getConfig().getInt("regenerationTime");
    }

    public static double getWitherMoveDamage() {

        FileConfiguration config = BlowablePlugin.instance.getConfig();

        if (config.contains("explosionSettings.witherBlockEating.damage")) {
            return config.getDouble("explosionSettings.witherBlockEating.damage");
        } else {
            return config.getDouble("explosionSettings.default.damage");
        }

    }

    public static double getDefaultRadius(EntityType type) {
        return getExplosionSetting(type, "radius");
    }

    public static double getDefaultDamage(EntityType type) {
        return getExplosionSetting(type, "damage");
    }

    private static double getExplosionSetting(EntityType type, String setting) {

        FileConfiguration config = BlowablePlugin.instance.getConfig();

        if (type == null) {
            if (config.contains("explosionSettings.customExplosions." + setting)) {
                return config.getDouble("explosionSettings.customExplosions." + setting);
            } else {
                return config.getDouble("explosionSettings.default." + setting);
            }
        }

        double damage;

        // In 1.21.4, PRIMED_TNT is now just TNT
        if (type == EntityType.TNT) {
            damage = config.getDouble("explosionSettings.tnt." + setting);
        } else if (type == EntityType.CREEPER) {
            damage = config.getDouble("explosionSettings.creeper." + setting);
        } else if (type == EntityType.WITHER) {
            damage = config.getDouble("explosionSettings.witherCreation." + setting);
        } else if (type == EntityType.WITHER_SKULL) {
            damage = config.getDouble("explosionSettings.witherProjectile." + setting);
        } else {
            damage = config.getDouble("explosionSettings.default." + setting);
        }

        return damage;
    }

    public static boolean makeBlowable(Block b) {

        World world = b.getWorld();
        Map<String, Object> allWorldSettings = BlowablePlugin.instance.getConfig().getConfigurationSection("WorldSettings").getValues(false);
        if (!allWorldSettings.containsKey(world.getName())) {
            return false;
        }
        if (!BlowablePlugin.instance.getConfig().getBoolean("WorldSettings." + world.getName() + ".enabled")) {
            return false;
        }

        List<Integer> bedrockProtection = BlowablePlugin.instance.getConfig().getIntegerList("WorldSettings." + world.getName() + ".bedrockProtection");
        if (b.getType() == Material.BEDROCK && bedrockProtection.contains((Integer) b.getY())) {
            return false;
        }

        Material m = b.getType();
        Map<String, Object> blowableBlocks = BlowablePlugin.instance.getConfig().getConfigurationSection("blocksHealth").getValues(false);
        blowableBlocks = lowerMapKeys(blowableBlocks);
        return blowableBlocks.containsKey(m.toString().toLowerCase())
                || (!b.isLiquid() && b.getType() != Material.AIR && blowableBlocks.containsKey("all"));
    }

    /**
     * Exports the default configuration file if needed.
     * This method will also check if the plugin directory exists and create it if needed.
     */
    public static void exportConfig() {
        try {
            // Ensure plugin directory exists
            File pluginDir = BlowablePlugin.instance.getDataFolder();
            if (!pluginDir.exists()) {
                pluginDir.mkdirs();
                BlowablePlugin.instance.getLogger().info("Created plugin directory.");
            }
            
            // Check if config file exists
            File configFile = new File(pluginDir, "config.yml");
            boolean configExists = configFile.exists();
            
            // Always export if config doesn't exist or version is outdated
            if (!configExists || BlowablePlugin.instance.getConfig().getInt("Config Version", 0) < BlowablePlugin.instance.configVersion) {
                URL inputUrl = BlowablePlugin.instance.getClass().getResource("/config.yml");
                
                // Backup old config if it exists
                if (configExists) {
                    File backupFile = new File(pluginDir, "old_config.yml");
                    if (backupFile.exists()) backupFile.delete();
                    configFile.renameTo(backupFile);
                    BlowablePlugin.instance.getLogger().info("Previous configuration file was renamed to old_config.yml.");
                }
                
                // Copy default config
                Util.copyUrlToFile(inputUrl, configFile);
                BlowablePlugin.instance.getLogger().info("Configuration file was successfully exported to plugin folder.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Ensures all required files exist and creates them if they don't.
     * This should be called during plugin reload.
     */
    public static void ensureFilesExist() {
        try {
            // Make sure plugin directory exists
            File pluginDir = BlowablePlugin.instance.getDataFolder();
            if (!pluginDir.exists()) {
                pluginDir.mkdirs();
                BlowablePlugin.instance.getLogger().info("Created plugin directory.");
            }
            
            // Check if config file exists
            File configFile = new File(pluginDir, "config.yml");
            if (!configFile.exists()) {
                URL inputUrl = BlowablePlugin.instance.getClass().getResource("/config.yml");
                Util.copyUrlToFile(inputUrl, configFile);
                BlowablePlugin.instance.getLogger().info("Missing configuration file was restored.");
            }
            
            // DO NOT reload the configuration here - this was causing infinite recursion
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Map<String, Object> lowerMapKeys(Map<String, Object> map) {
        Map<String, Object> output = new HashMap<String, Object>();
        for (Entry<String, Object> e : map.entrySet()) {
            String key = e.getKey().toLowerCase();
            key = key.replace(" ", "_");
            key = key.replace("wither_projectile", "wither_skull");
            if (key.equals("tnt")) key = key.replace("tnt", "tnt"); // Changed from primed_tnt to tnt for 1.21.4
            key = key.replace("wither_creation", "wither");
            output.put(key, e.getValue());
        }
        return output;
    }
}