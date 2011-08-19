package me.leonblade.landbrush;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LandBrush extends JavaPlugin {

	private HashMap<String, LandBrushPlayer> landBrushPlayers = new HashMap<String, LandBrushPlayer>();
	private final LandBrushPlayerListener landBrushPlayerListener = new LandBrushPlayerListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable() {
		// let the console know our plugin has been disabled
		log.info(getDescription().getName() +  " has been disabled.");
	}

	@Override
	public void onEnable() {
		// let the console know our plugin has been enabled
		log.info(getDescription().getName() + " version " + getDescription().getVersion() + " has been enabled.");
		
		for (Player p : getServer().getOnlinePlayers()) {
			this.landBrushPlayers.put(p.getName(), new LandBrushPlayer(p));
			log.info("["+getDescription().getName()+"] Adding player " + p.getName());
		}
		
		// get the plugin manager and register some events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, landBrushPlayerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, landBrushPlayerListener, Event.Priority.Normal, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// get the landbrush player from the sender
		LandBrushPlayer lbp = this.landBrushPlayers.get(((Player) sender).getName());
		try {
			if (command.getName().equalsIgnoreCase("landbrush")) {
				// help commands
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "LandBrush Help Menu");
					sender.sendMessage("/" + label + " help " + ChatColor.GRAY + "(shows this menu)");
					sender.sendMessage("/" + label + " undo " + ChatColor.GRAY + "(goes back in time via a TARDIS)");
					sender.sendMessage("/" + label + " size 5 " + ChatColor.GRAY + "(sets brush size)");
					sender.sendMessage("/" + label + " base me" + ChatColor.GRAY + "(sets base height to your Y position)");
					sender.sendMessage("/" + label + " base 72 " + ChatColor.GRAY + "(sets base size statically)");
					sender.sendMessage("/" + label + " tool wooden_spade " + ChatColor.GRAY + "(sets the tool by name)");
					sender.sendMessage("/" + label + " tool 269 " + ChatColor.GRAY + "(sets the tool by ID)");
					sender.sendMessage("/" + label + " scale 3 " + ChatColor.GRAY + "(sets the scale of the beach sand)");
					return true;
				}
				// brush size
				else if (args[0].equalsIgnoreCase("size")) {
					lbp.setBrushSize(Integer.parseInt(args[1]));
					return true;
				}
				// base Y
				else if (args[0].equalsIgnoreCase("base")) {
					if (args[1].equalsIgnoreCase("me")) {
						lbp.setBaseY((int)lbp.getPlayer().getLocation().getY());
					} else {
						try {
							lbp.setBaseY(Integer.parseInt(args[1]) - 1);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "Error: Neither string or integer value found for tool.");
							return false;
						}
					}
					return true;
				}
				// setting the new tool
				else if (args[0].equalsIgnoreCase("tool")) {
					Material tool = null;
					try {
						tool = Material.getMaterial(Integer.parseInt(args[1]));
					} catch (Exception e) {
						log.warning("["+getDescription().getName()+"] Error: Tried to getMaterial with " + args[1]);
					}
					try {
						tool = Material.matchMaterial(args[1]);
					} catch (Exception e) {
						log.warning("["+getDescription().getName()+"] Tried to getMaterial with " + args[1]);
					}
					lbp.setTool(tool);				
					return true;
				}
				// setting the scale
				else if (args[0].equalsIgnoreCase("scale")) {
					try {
						lbp.setScale(Double.parseDouble(args[1]));
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Error: Scale needs to be a double value (3.0 by default).");
						return false;
					}					
					return true;
				}
				// the time traveling machine
				else if (args[0].equalsIgnoreCase("undo")) {
					lbp.undo();
					return true;
				}
			}
		// something went wrong when entering a command
		} catch (Exception e) {
			log.warning("[LandBrush] Error: Player " + lbp.getPlayer().getName() + " tried to access a command => " + 
							e.getCause().getLocalizedMessage());
			return false;
		}		 
		return false;
	}
	
	// gets the hashmap for landbrush players
	public HashMap<String, LandBrushPlayer> getLandBrushPlayers() {
		return landBrushPlayers;
	}
	
}
