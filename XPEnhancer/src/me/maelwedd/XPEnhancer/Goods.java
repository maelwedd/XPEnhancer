package me.maelwedd.XPEnhancer;

import org.bukkit.util.config.Configuration;

import sun.security.krb5.Config;

public class Goods {

	public String name;
	public String type;
	public int id;
	public int cost = 0;
	public boolean enabled = false;
	
	public Goods(String name, String type, int id, int cost, boolean enabled, Configuration CONFIG)	{
		this.name = name;
		this.type = type;
		this.id = id;
		this.cost = cost;
		this.enabled = enabled;
		
		CONFIG.setProperty(name + ".type", type);
		CONFIG.setProperty(name + ".id", id);
		CONFIG.setProperty(name + ".cost", cost);
		CONFIG.setProperty(name + ".enabled", enabled);
		
		CONFIG.save();
		
	}
	
}
