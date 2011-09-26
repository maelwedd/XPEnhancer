package me.maelwedd.XPEnhancer;


import org.bukkit.command.CommandSender;
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

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;


public class XPEnhancerPlayerListener extends PlayerListener{
	
	public static XPEnhancer plugin;
	public XPEnhancerPlayerListener(XPEnhancer instance)	{
		plugin = instance;
	}
	
	// What to do on PlayerInteractEvent
	public void onPlayerInteract(PlayerInteractEvent event)	{

		// Let's get the most used values
		Player player = event.getPlayer();
		int inHand = player.getItemInHand().getTypeId();
		
			// If the player right clicks air with a stick, they're saying they want to spawn the creature there
			// Add permission support here when I figure that one out...
			if ( inHand == Material.STICK.getId() && event.getAction().equals(Action.LEFT_CLICK_BLOCK) )	{
				
				Goods goods = plugin.boughtUse(player);
				if ( goods != null ) {
					goods.spawn(event.getClickedBlock().getLocation(), player);
				}
				
			}
			
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))		{
			
			//NOTE: This is NOT player-location, but where the player clicked!
			Location loc = event.getClickedBlock().getLocation();
			int clickedType = event.getClickedBlock().getTypeId();
		

			
			// If player presses stone-button, that's our cue to action
			// 
			if ( clickedType == Material.STONE_BUTTON.getId() )	{
				

				// Code to be run to create/edit/delete stores
				// My permissions check doesnt seem to work, weird...
//				if ( player.hasPermission("xpenhancer.create") )	{
				if ( true ) {

					// We use the stick to create/activate/delete stores
					if (inHand == Material.STICK.getId()) {
							
						// Try to create a new store, a null return means a store already exist at that location
						Store newStore = plugin.newStore(loc);
						
						if  ( newStore == null )	{

							// TODO: Add method to delete existing stores here
							
							// A store already exist here, get it.
							// newStore is now existingStore in a way...
							newStore = plugin.getStore(loc);

							if ( newStore.isActive() ) {
								player.sendMessage("Store already exist!");
							}
							else {
								// The activate command returns false if it can't activate due to no goods for store set
								if ( newStore.activate() ) {
									player.sendMessage("Store activated: " + newStore.goods.name);
								}
								else {
									player.sendMessage("Store NOT activated: Did you add goods?");
								}
							}
							
						}
						else	{
							player.sendMessage("Store created! Use objects to set goods.");
						}
					}
					else {
						
						// Check if ItemInHand is "legal" goods, if it is, and there is an unactivated store, set that goods to the store goods
						Goods goods = plugin.findGoods( player.getItemInHand().getType() ); 
						if ( !(goods == null))	{
							// Check if there is a store at location
							Store store = plugin.getStore(loc);
							if ( !(store == null) && !store.isActive() )	{
								store.setGoods(goods);
								player.sendMessage("Goods set to: " + goods.name);
							}

						}

						
					}
				}


				// Code to be run to use a store
//				if ( player.hasPermission("xpenhancer.use"))	{
				// Permissions check is busted somehow...
				if ( true )	{
					
					Store store = plugin.getStore(loc);
					if (! ( store == null ) && store.isActive() ) {
						
						if ( inHand == store.goods.use_id ) {
							
							store.buy(player, plugin);

						}
						else player.sendMessage("Alchemy rules: " + Material.getMaterial(store.goods.use_id).toString().toLowerCase() + " in exchange for " + store.goods.name );
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
