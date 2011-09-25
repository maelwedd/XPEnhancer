package me.maelwedd.XPEnhancer;

import org.bukkit.util.config.Configuration;


public class Goods {

	public String name;
	public String type;
	public int id;
	public int cost = 0;
	public int costquantity = 1;
	public int quantity = 1;
	public boolean enabled = false;
	public int use_id = 0;
	
	public Goods(String name, String type, int id, int cost, int use_id, boolean enabled, Configuration CONFIG)	{
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
		
		this.enabled = enabled;
		this.use_id = use_id;
		
		CONFIG.setProperty(name + ".type", type);
		CONFIG.setProperty(name + ".id", id);
		CONFIG.setProperty(name + ".cost.XP", cost);
		CONFIG.setProperty(name + ".cost.quantity", costquantity);
		CONFIG.setProperty(name + ".enabled", enabled);
		CONFIG.setProperty(name + ".use_id", use_id);
		CONFIG.setProperty(name + ".quantity", quantity);
		
		CONFIG.save();
		
	}
	
	public boolean isBlock()	{
		if ( type.toLowerCase() == "block" ) return true;
		return false;
	}
	
}
