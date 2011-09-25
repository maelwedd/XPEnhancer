package me.maelwedd.XPEnhancer;


import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;


public class XPEnhancerPlayerListener extends PlayerListener{
	
	public static XPEnhancer plugin;
	public XPEnhancerPlayerListener(XPEnhancer instance)	{
		plugin = instance;
	}
	
	// What to do on PlayerInteractEvent
	public void onPlayerInteract(PlayerInteractEvent event)	{
		
		Player player = event.getPlayer();
		
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))		{
		
			// If player presses stone-button, that's our cue to action
			// 
			if (event.getClickedBlock().getType().equals(Material.STONE_BUTTON) && plugin.buttonLoc(event.getClickedBlock()))	{
				
				if (player.getGameMode().equals(GameMode.SURVIVAL))	{
					player.setGameMode(GameMode.CREATIVE);
				}
				else if (player.getGameMode().equals(GameMode.CREATIVE))	{
					player.setGameMode(GameMode.SURVIVAL);
				}
			}
			else if (true)	{
				player.chat("Imma gonna let you finish, but!");
			}
	
		}

	}
	
}
