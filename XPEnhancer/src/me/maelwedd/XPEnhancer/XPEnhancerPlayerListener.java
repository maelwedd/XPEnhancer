package me.maelwedd.XPEnhancer;


import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;


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
			
			player.chat("I right-clicked on " + event.getClickedBlock().getType() + " with " + player.getItemInHand().getTypeId() + " !");

			player.chat("I should have clicked on a " + Material.STONE_BUTTON + " with a " + Material.STICK.getId() + " to make a store");
			player.chat("I should have used a " + Material.BOOK.getId() + " to use the store");
			
			if (event.getClickedBlock().getType().equals(Material.STONE_BUTTON))	{
				

				player.chat("Match: Stone button");
//				if (player.hasPermission("xpenhancer.create") && player.getItemInHand().equals(Material.STICK))	{
				if ( player.getItemInHand().getTypeId() == Material.STICK.getId() )	{
					
					player.chat("Match: In hand Stick");
					
					Store newStore = plugin.newStore(event.getClickedBlock().getLocation());
					
					if  ( ! newStore.equals(null) )	{
//						plugin.getServer().broadcast("Store created", "xpenhancer.create");
						player.chat("I created a store: " + newStore.getBlockX() + "," + newStore.getBlockY() + "," + newStore.getBlockZ() );
					}
					else	{
//						plugin.getServer().broadcast("Store already exists", "xpenhancer.create");
						player.chat("I'm a loser and can't even create a simple store...");
					}
					
				}
//				else if (player.hasPermission("xpenhancer.use") && player.getItemInHand().equals(Material.BOOK))	{
				else if ( player.getItemInHand().getTypeId() == Material.BOOK.getId() )	{
					
					player.chat("Match: In hand Book");
					
					Store store = plugin.getStore(event.getClickedBlock().getLocation());
					if (! store.equals(null) ) {
						player.setExperience(player.getExperience() - store.goods.cost);
						player.setItemInHand(new ItemStack(store.goods.id, 1));
						player.chat("I'm a store-user!");
					}
					
					
				}
				
				// Old testing code
				/*else if (player.getGameMode().equals(GameMode.SURVIVAL))	{
					player.setGameMode(GameMode.CREATIVE);
				}
				else if (player.getGameMode().equals(GameMode.CREATIVE))	{
					player.setGameMode(GameMode.SURVIVAL);
				}*/
			}

	
		}

	}
	
}
