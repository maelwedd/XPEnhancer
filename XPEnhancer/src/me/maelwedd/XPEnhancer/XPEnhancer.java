package me.maelwedd.XPEnhancer;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class XPEnhancer extends JavaPlugin {

	protected static Configuration CONFIG;
	
	protected static Configuration STORES;
	
	private ArrayList<Goods> goods = new ArrayList<Goods>();
	private ArrayList<Store> stores = new ArrayList<Store>();
	
	private final XPEnhancerPlayerListener playerListener = new XPEnhancerPlayerListener(this);
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
	Calendar cal = Calendar.getInstance();
	
	
	public  String name = "XPEnhancer";
	public  String version = "0.0" + "build " + dateFormat.format(cal.getTime());
	public  String author = "Maelwedd (maelwedd@starstruct.org)";
	
	public static Object button;
	
	Logger log = Logger.getLogger("Minecraft");	
	

	
	public void onEnable(){ 
		log.info("Loading: " + name + " v" + version + " by " + author);
		
		CONFIG = getConfiguration();
		makeDefaultConfig();
		CONFIG.load();
		
		STORES = new Configuration(new File(getDataFolder(), "stores"));
		
		// Start plugin here, first make a pm so we can listen to events from bukkit
		PluginManager pm = this.getServer().getPluginManager();
		
		// Register what events to listen to, the action to be taken on these events are listed in the XPEnhancedPlayerListener-class
		// PLAYER_INTERACT: when player left/right-clicks a block ingame (our case: buttons)
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		
	}
	
	public void makeDefaultConfig()	{
			
		goods.add(new Goods("grassblock", "block", 2, 0, true, CONFIG));
		//goods.add(new Goods("cow", "entity", "Cow", 0, true, CONFIG));
		
	}

	public void readLocations()	{
		STORES.getAll();
	}
	
	public Store newStore(Location loc) {
		
		// Check to see if a store already exists at this location
		
		if ( ! ( getStore(loc) == null ) ) return null;
		
		// Using the length of the store-array for store-id, but am afraid that'll bite me in the ass pretty soon...
		// What will happen when I'm reading to and from the config file???
		//
		// For now always using goods.get(0) = grassblock as goods
		Store newStore = new Store(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), stores.size() + 1, goods.get(0), STORES);
		stores.add(newStore);
		
		return newStore;
		
	}
	
	public Store lastStore()	{
		if( stores.isEmpty() ) return null;

		return stores.get(stores.size()-1);
	}
	
	public void onDisable(){ 

		// In addition to saving config when exiting, remember to always save when editing it
		CONFIG.save();
		STORES.save();
		log.info(name + " disabled");
	}
	
	public Store getStore(Location loc) {
		if( stores.isEmpty() ) return null;
		
		
		// Use a while loop to check if the location contains a store
		// Inefficient? # of stores is low (~10)

		// Apparently, I don't know how to use iterators, as this only throws null pointer exceptions
//		Iterator<Store> itr = stores.iterator();
//		Store store = null;
//		while(itr.hasNext())	{
//			store = itr.next();
//			if ( store.compareLoc(loc) )	{
//				return store;
//			}
//		}
//		
		

		for ( int i = 0; i < stores.size() ; i++)	{
			if ( stores.get(i).compareLoc(loc) ) return stores.get(i);
		}
		
		
		// If while loop completed, no store with that location was found
		return null;
		
	}
	
}
