package me.maelwedd.XPEnhancer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class XPEnhancer extends JavaPlugin {

	protected static Configuration CONFIG;
	
	protected static Configuration STORES;
	
	private ArrayList<Goods> goods = new ArrayList<Goods>();
	private ArrayList<Store> stores = new ArrayList<Store>();
	
	// Simplest way I know of for storing two values...
	private ArrayList<Player> boughtPlayer = new ArrayList<Player>();
	private ArrayList<Goods> boughtGoods = new ArrayList<Goods>();
	
	private final XPEnhancerPlayerListener playerListener = new XPEnhancerPlayerListener(this);
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
	Calendar cal = Calendar.getInstance();
	
	
	public  String name = "XPEnhancer";
	public  String version = "0.3" + "build " + dateFormat.format(cal.getTime());
	public  String author = "Maelwedd";
	
	public static Object button;
	
	Logger log = Logger.getLogger("Minecraft");	
	

	public void onEnable(){ 

		
		// For debug-purposes
		// Have not made this an configurable option, so can only set it in code!
		// Although, I can't seem to get FINE or FINER log-levels to work...
//		log.setLevel(Level.FINE);
//		log.fine(name + " enabled debug logging.");

		log.info("Loading: " + name + " v" + version + " by " + author);

		
		CONFIG = getConfiguration();
		STORES = new Configuration(new File(getDataFolder(), "stores.yml"));
		STORES.load();
		CONFIG.load();
		loadGoods();
		loadStores();

		

		// Start plugin here, first make a pm so we can listen to events from bukkit
		PluginManager pm = this.getServer().getPluginManager();
		
		// Register what events to listen to, the action to be taken on these events are listed in the XPEnhancedPlayerListener-class
		// PLAYER_INTERACT: when player left/right-clicks a block ingame (our case: buttons)
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		
	}
	
	private void loadGoods() {
		
		List<String> keys = CONFIG.getKeys();
		
		log.fine(name + " debug: " + "Trying to load " + keys.size() + " goods." );
		
		// Don't know what getKeys returns...
		// If no keys loaded, create default
		if ( keys == null || keys.size() == 0 ) {
			log.info(name + " info: No config.yml found, creating default.");
			makeDefaultConfig();
			return;
		}

		String good;
		String type;
		int id;
		int use_id;
		int quantity;
		int costxp;
		int costquantity;
		
		// Load all goods into memory
		for ( int i = 0; i < keys.size() ; i++)	{
			
			good = keys.get(i);
			
			try	{
				type = CONFIG.getString( good + ".type");
				log.fine(name + " debug: Goods loaded, type:" + type);
			}
			catch (Exception e)	{
				log.fine(name + " error: Goods " + good + ": Type invalid in config file -> Ignoring goods.");
				continue;
			}
			
			try	{
				id = Integer.parseInt( CONFIG.getString( good + ".id") );
				log.fine(name + " debug: Goods loaded, ID:" + id);
			}
			catch (Exception e)	{
				log.fine(name + " error: Goods " + good + ": ID invalid in config file -> Ignoring goods.");
				continue;
			}
			
			try	{
				use_id = Integer.parseInt( CONFIG.getString( good + ".use_id") );
				log.fine(name + " debug: Goods loaded, use_id:" + use_id);
			}
			catch (Exception e)	{
				log.fine(name + " error: Goods " + good + ": Use_ID invalid in config file -> Ignoring goods.");
				continue;
			}
			
			// Quantity and cost.quantity defaults to 1 of not given in config
			quantity = CONFIG.getInt(good + ".quantity", 1);
			log.fine(name + " debug: Goods loaded, quantity:" + quantity);
			costquantity = CONFIG.getInt(good + ".cost.quantity", 1);
			log.fine(name + " debug: Goods loaded, cost.quantity:" + costquantity);
			// cost.xp defaults to 150 if not given in config
			costxp = CONFIG.getInt(good + ".cost.xp", 150);
			log.fine(name + " debug: Goods loaded, cost.xp:" + costxp);

			goods.add( new Goods( good, type, id, costxp, use_id, quantity, costquantity, CONFIG ));
			log.fine(name + " debug: Goods loaded, total goods:" + goods.size());
		}
		log.info(name + " info: Loaded " + goods.size() + " goods from config.");			
	}

	public void makeDefaultConfig()	{
		
		goods.add(new Goods("grassblock", "block", Material.GRASS.getId(), 150, Material.DIRT.getId(), CONFIG));
		goods.add(new Goods("cow", "entity", Material.RAW_BEEF.getId(), 75, Material.COOKED_BEEF.getId(), CONFIG));
		goods.add(new Goods("pig", "entity", Material.PORK.getId(), 75, Material.GRILLED_PORK.getId(), CONFIG));
		goods.add(new Goods("sheep", "entity", Material.AIR.getId(), 75, Material.WOOL.getId(), CONFIG));
		goods.add(new Goods("chicken", "entity", Material.RAW_CHICKEN.getId(), 75, Material.COOKED_CHICKEN.getId(), CONFIG));
		
		log.fine(name + " debug: Created default Goods.");
		
	}

	public void loadStores()	{

		List<String> keys = STORES.getKeys();

		log.fine(name + " debug: Trying to load " + keys.size() + " stores." );
		
		World world;
		int x = 0;
		int y = 0;
		int z = 0;
		Location loc;
		Goods good;;
		Store store;
		
		if (keys.size() == 0 )	return;
		
		for ( int i = 0; i < keys.size() ; i++)	{
			
			String storeID = keys.get(i);
			
			// Get the world-name. The getString returns null if there is no value with that name
			world = this.getServer().getWorld(STORES.getString( storeID + ".world" ) );
			if ( world == null ) {
				log.fine(name + " error: Store " + storeID + ": no World name in config file -> Ignoring store. ");
				continue;
			}

			try	{
				x = (int)(Double.parseDouble( STORES.getString( storeID + ".location.x") ));
				log.fine(name + " debug: Store " + storeID + ": Loaded x=" + x);
				y = (int)(Double.parseDouble( STORES.getString( storeID + ".location.y") ));
				log.fine(name + " debug: Store " + storeID + ": Loaded y=" + y);
				z = (int)(Double.parseDouble( STORES.getString( storeID + ".location.z") ));
				log.fine(name + " debug: Store " + storeID + ": Loaded z=" + z);
			}
			catch (Exception e)	{
				// Don't care *what* happened: Can't read the location, can't create the store...
				// getString returns null if value is not found
				// Most likely either the value is missing from the config-file or the value is not representing an int
				log.fine(name + " error: Store " + storeID + ": invalid location in config file -> Ignoring store." );
				log.fine(name + " erorr: x=" + x + ", y=" + y + ", z=" + z);
				continue;
			}
			
			try	{
				good = findGoodsName( STORES.getString( storeID + ".goods" ) );
				log.fine(name + " debug: Store " + storeID + ": Loaded goods=" + good.name);
				
			}
			catch (Exception e)	{
				log.fine(name + " error: Store " + storeID + ": invalid goods in config file -> Ignoring store." );
				continue;
			}
			
			// We now have enough to create the store, set the goods, and activate it
			store = new Store(world, x, y, z, storeID, STORES);
			if (! store.setGoods(good) ) {
				log.fine(name + " error: Store " + storeID + ": invalid goods in config file -> Ignoring store." );
				continue;
			}
			if (! store.activate() )	{
				log.fine(name + " error: Store " + storeID + ": can't activate -> Ignoring store." );
				continue;
			}
			
			// Finally, add the store to our stores-list
			log.fine(name + " debug: Trying to add store " + store.toString() + " to list." );
			log.fine(name + " debug: Active:" + store.isActive() + ", Goods=" + store.goods.toString() + ", Loc=" + store.getBlockX() + "," + store.getBlockY() + "," + store.getBlockZ() );
			stores.add(store);
			
		}
		
		log.info(name + " info: Loaded " + stores.size() + " stores from config.");
						
	}
	
	private Goods findGoodsName(String name) {
		for ( int i = 0; i < goods.size() ; i++ )	{
			if ( goods.get(i).name.equals(name) )	return goods.get(i);
		}
		return null;
	}

	public Store newStore(Location loc) {
		
		// Check to see if a store already exists at this location
		
		if ( ! ( getStore(loc) == null ) ) return null;
		
		// Using location data as store ID, given as xyz, that should ensure a unique id for all stores
		String storeid = Integer.toString(loc.getBlockX()) + Integer.toString(loc.getBlockY()) + Integer.toString(loc.getBlockZ());
		
		Store newStore = new Store(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), storeid , STORES);
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
	
	// Returns the goods if the material is allowed goods, null indicates invalid material
	public Goods findGoods(Material mat)	{
		for ( int i = 0; i < goods.size() ; i++ )	{
			if ( goods.get(i).use_id == mat.getId() )	return goods.get(i);
		}
		return null;
	}
	
	public void bought(Player player, Goods goods)	{
		boughtPlayer.add(player);
		boughtGoods.add(goods);
	}

	public Goods boughtUse(Player player)	{

		// This actually makes it so that a player can buy several spawns, and use them in the same order as they were bought
		int ix = boughtPlayer.indexOf(player);
		
		// ix = -1 means the player was not in the list, ie the player have not purchased a spawn-use
		if ( ix < 0 ) return null;

		// Remove this item and return the goods that was purchased earlier
		boughtPlayer.remove(ix);
		return boughtGoods.remove(ix);
	}
	
}
