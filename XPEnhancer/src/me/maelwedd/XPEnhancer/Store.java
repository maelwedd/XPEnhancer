package me.maelwedd.XPEnhancer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;

public class Store extends Location{
	
	public Goods goods;
	
	public Store(World world, double x, double y, double z, int storeID, Goods goods, Configuration STORES) {
		super(world, x, y, z);
		this.goods = goods;
		
		STORES.setProperty(storeID + ".goods", goods.name);
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

	
}
