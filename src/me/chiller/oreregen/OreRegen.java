package me.chiller.oreregen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class OreRegen extends JavaPlugin implements Listener
{
	private Map<Material, Double> regenTimes = new HashMap<Material, Double>();
	private List<BukkitRunnable> regenTimers = new ArrayList<BukkitRunnable>();
	
	public void onEnable()
	{
		saveDefaultConfig();
		
		ConfigurationSection oresSection = getConfig().getConfigurationSection("ores");
		
		for (String key : oresSection.getKeys(false))
		{
			Material material = Material.valueOf(key.toUpperCase());
			
			regenTimes.put(material, oresSection.getDouble(key, 0));
		}
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable()
	{
		for (BukkitRunnable runnable : regenTimers)
		{
			runnable.cancel();
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (regenTimes.containsKey(event.getBlock().getType()))
		{
			double regenTime = regenTimes.get(event.getBlock().getType());
			
			final Location loc = event.getBlock().getLocation();
			final Material mat = event.getBlock().getType();
			
			if (regenTime != 0)
			{
				BukkitRunnable runnable = new BukkitRunnable()
				{
					public void run()
					{
						if (loc.getBlock().getType() == Material.AIR) loc.getBlock().setType(mat);
					}
				};
				
				runnable.runTaskLater(this, (int) (regenTime * 60) * 20);
				regenTimers.add(runnable);
			}
		}
	}
}