package org.minemalia.blowable.support;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.minemalia.blowable.BlowablePlugin;

public class WorldGuardSupport {
    
    private static boolean worldGuardEnabled = false;
    private static boolean initialized = false;
    
    /**
     * Initialize WorldGuard support
     */
    public static void initialize() {
        if (initialized) return;
        
        try {
            // Check if WorldGuard is available
            Class.forName("com.sk89q.worldguard.WorldGuard");
            worldGuardEnabled = true;
            BlowablePlugin.instance.getLogger().info("WorldGuard detected - integration enabled");
        } catch (ClassNotFoundException e) {
            worldGuardEnabled = false;
            BlowablePlugin.instance.getLogger().info("WorldGuard not detected - integration disabled");
        }
        
        initialized = true;
    }
    
    /**
     * Check if a block is in a WorldGuard region where explosions are disabled
     * 
     * @param block The block to check
     * @return true if the block is in a protected region, false otherwise
     */
    public static boolean isInProtectedRegion(Block block) {
        if (!worldGuardEnabled || !BlowablePlugin.instance.getConfig().getBoolean("worldGuardIntegration.enabled", true)) {
            return false;
        }
        
        // Check if the world is enabled in the config
        World world = block.getWorld();
        if (!BlowablePlugin.instance.getConfig().getBoolean("WorldSettings." + world.getName() + ".enabled", false)) {
            return false;
        }
        
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            
            // Check if explosions are allowed at this location
            // We use null for the subject parameter since we're just checking the region flags
            return !query.testState(
                BukkitAdapter.adapt(block.getLocation()),
                null,
                Flags.OTHER_EXPLOSION, Flags.TNT, Flags.CREEPER_EXPLOSION
            );
        } catch (Exception e) {
            // If any error occurs, log it and return false to be safe
            BlowablePlugin.instance.getLogger().warning("Error checking WorldGuard protection: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if WorldGuard integration is enabled
     * 
     * @return true if WorldGuard is available and integration is enabled
     */
    public static boolean isEnabled() {
        return worldGuardEnabled && BlowablePlugin.instance.getConfig().getBoolean("worldGuardIntegration.enabled", true);
    }
    
    /**
     * Check if blocks in protected regions should always show full health
     * 
     * @return true if blocks in protected regions should always show full health
     */
    public static boolean alwaysFullHealth() {
        return isEnabled() && BlowablePlugin.instance.getConfig().getBoolean("worldGuardIntegration.alwaysFullHealth", true);
    }
}