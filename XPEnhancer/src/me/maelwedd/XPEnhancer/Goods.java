package me.maelwedd.XPEnhancer;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;


public class Goods {

	public String name;
	public String type;
	public int id;
	public int cost = 0;
	public int costquantity = 1;
	public int quantity = 1;
	public int use_id = 0;
	
	public Goods(String name, String type, int id, int cost, int use_id, Configuration CONFIG)	{
		this.name = name;
		this.type = type;
		this.id = id;
		
		// Only accept costs of XP, negative cost would give the player XP. Default to 0 cost if negative number.
		if ( cost >= 0 )	{
			this.cost = cost;	
		}
		else	{
			this.cost = 0;
		}

		this.use_id = use_id;
		
		CONFIG.setProperty(name + ".type", type);
		CONFIG.setProperty(name + ".id", id);
		CONFIG.setProperty(name + ".cost.XP", cost);
		CONFIG.setProperty(name + ".cost.quantity", costquantity);
		CONFIG.setProperty(name + ".use_id", use_id);
		CONFIG.setProperty(name + ".quantity", quantity);
		
		CONFIG.save();
		
	}
	
	public Goods(String name, String type, int id, int cost, int use_id, int quantity, int costquantity, Configuration CONFIG)	{
		this(name, type, id, cost, use_id, CONFIG);
		
		this.quantity = quantity;
		this.costquantity = costquantity;
		
		CONFIG.setProperty(name + ".cost.quantity", costquantity);
		CONFIG.setProperty(name + ".quantity", quantity);
		CONFIG.save();
		
	}
	
	public boolean isBlock()	{
		if ( type.toLowerCase().equals("block") ) return true;
		return false;
	}
	
	public boolean spawn(Location loc, Player player)	{
		
		// Only goods that are entities (ie not blocks) can be used to spawn things
		if ( isBlock() ) return false;
		
		CreatureType spawn = null;
		
		// This was the easiest way to do it...
		if ( type.toLowerCase().equals("cow") ) spawn = CreatureType.COW;
		else if ( name.toLowerCase().equals("chicken") ) spawn = CreatureType.CHICKEN;
		else if ( name.toLowerCase().equals("sheep") ) spawn = CreatureType.SHEEP;
		else if ( name.toLowerCase().equals("pig") ) spawn = CreatureType.PIG;
				
		if ( spawn == null ) return false;
		
		player.getWorld().spawnCreature(loc, spawn);
		player.sendMessage("Teleportation used: " + type.toLowerCase() + " spawned.");
		
		return true;
				
	}
	
}
