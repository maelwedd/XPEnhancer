package me.maelwedd.XPEnhancer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

public class Store extends Location{
	
	public Goods goods = null;
	private boolean activated = false;
	
	private String storeID;
	
	Configuration STORES;

	public Store(World world, double x, double y, double z, String storeID, Configuration STORES) {
		super(world, x, y, z);

		this.STORES = STORES;
		this.storeID = storeID;
		
		STORES.setProperty(storeID + ".world", world.getName());
		STORES.setProperty(storeID + ".location.x", x);
		STORES.setProperty(storeID + ".location.y", y);
		STORES.setProperty(storeID + ".location.z", z);
		
		STORES.save();
		
	}

	public boolean compareLoc(Location loc)	{
		
		// Easy, if the location given matches this store, we're golden
		// 
		// Was using the following code:
		//if ( ( (Location)this ).equals(loc) ) return true;
		// But was afraid it wouldn't work, as the stores loc is set as getBlockX()...
		// while the incoming loc could be detailed: (int)getX() =/= getBlockX()

		// Ugly way to do it?
		if ( loc.getBlockX() == this.getBlockX() )	{
			if ( loc.getBlockY() == this.getBlockY() ) 	{
				if ( loc.getBlockZ() == this.getBlockZ() )	{
					return true;
				}
			}
		}
		
		return false;
	}

	public boolean isBlock()	{
		return goods.isBlock();
	}
	
	public boolean setGoods(Goods goods)	{
		if ( goods == null ) return false;
		if (! activated ) {
			this.goods = goods;
			STORES.setProperty(storeID + ".goods", goods.name);
			STORES.save();
			return true;
		}
		return false;
	}
	
	public boolean activate() {
		// It's already activated, why are you doing this?
		if ( activated ) return true;
		
		// Only activate the store if the store contains goods 
		if (! (goods == null) ) {
			activated = true;
			return true;
		}
		// If there are no goods yet, deny activation
		return false;
	}
	
	public boolean isActive()	{
		return activated;
	}
	
	// Keep all code regarding the actual buying here
	public boolean buy(Player player, XPEnhancer plugin)	{
		
		// Limit the store to only accept if the correct amount of blocks are in hand, makes the trade-in-code simpler
		if (! (player.getItemInHand().getAmount() == goods.costquantity) ) {
			player.sendMessage("Alchemy required specifiq quantities! You need " + goods.costquantity + " of " + needs());
			return false;
		}
		
		// Subtract the required amount of XP, don't mention it if the cost is 0 (avoid cluttering/confusing the player)
		if ( goods.cost > 0 )	{
			// First check to see if the player can afford this
			int newXP = player.getExperience() - goods.cost;
			// Check to see if the player can afford it, <0 means ending up with negative XP, no good...
			if ( newXP < 0 ) 	{
				player.sendMessage("Transaction cost too high: " + goods.cost + " -- Player XP only: " + player.getExperience());
				return false;
			}
			player.setExperience(newXP);
			player.sendMessage("Transaction cost: " + goods.cost + " -- Remaining XP: " + newXP);
		}
		
		// The store replaces the item in hand with the store goods, simple way to pay materials for the use
		player.setItemInHand(new ItemStack(goods.id, goods.quantity));
		player.sendMessage("Alchemy success: " + toString());
		
		// If the store is an entity-store, provide the player the opportunity to "cash in" the entity at a later date
		if ( ! isBlock() )	{
			plugin.bought(player, goods);
		}
		
		return true;
	}
	
	public String needs()	{
		 String needs = Material.getMaterial(goods.use_id).toString().toLowerCase();
		 // Replace underscores with spaces to make it nicer
		 needs.replaceAll("_", " ");
		 return needs;
	}
	
	public String toString()	{
		return goods.name.toLowerCase();
	}
	
}
