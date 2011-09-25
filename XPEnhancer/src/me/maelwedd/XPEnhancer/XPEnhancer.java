package me.maelwedd.XPEnhancer;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class XPEnhancer extends JavaPlugin {

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
		
		// Start plugin here, first make a pm so we can listen to events from bukkit
		PluginManager pm = this.getServer().getPluginManager();
		
		// Register what events to listen to, the action to be taken on these events are listed in the XPEnhancedPlayerListener-class
		// PLAYER_INTERACT: when player left/right-clicks a block ingame (our case: buttons)
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		
	}
	 
	public void onDisable(){ 
	 // Not sure if this needs to actually do something...
	// Maybe unregister listeners? When using config: Save config here!
		log.info(name + " disabled");
	}
	
	// Check if the clicked button is one we have in our DB
	public boolean buttonLoc(Block block)	{
		return true;
	}
	
}
