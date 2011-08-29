package me.leonblade.landbrush;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LandBrush extends JavaPlugin {

	private HashMap<String, LandBrushPlayer> landBrushPlayers = new HashMap<String, LandBrushPlayer>();
	private final LandBrushPlayerListener landBrushPlayerListener = new LandBrushPlayerListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	private static String logPrefix;
	
	@Override
	public void onDisable() {
		// let the console know our plugin has been disabled
		log.info(getDescription().getName() +  " has been disabled.");
	}

	@Override
	public void onEnable() {
		// let the console know our plugin has been enabled
		log.info(getDescription().getName() + " version " + getDescription().getVersion() + " has been enabled.");
		
		logPrefix = "[" + getDescription().getName() + "] ";
		
		for (Player p : getServer().getOnlinePlayers()) {
			this.landBrushPlayers.put(p.getName(), new LandBrushPlayer(p));
			log.info(logPrefix + "Adding player " + p.getName());
		}
		
		// get the plugin manager and register some events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, landBrushPlayerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, landBrushPlayerListener, Event.Priority.Normal, this);
	}
	
	private void showHelp(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "LandBrush Help Menu");
		sender.sendMessage("/" + label + " help " + ChatColor.GRAY + "(shows this menu)");
		sender.sendMessage("/" + label + " default " + ChatColor.GRAY + "(sets settings to default)");
		sender.sendMessage("/" + label + " undo " + ChatColor.GRAY + "(goes back in time via a TARDIS)");
		sender.sendMessage("/" + label + " size 5 " + ChatColor.GRAY + "(sets brush size)");
		sender.sendMessage("/" + label + " base me" + ChatColor.GRAY + "(sets base height to your Y position)");
		sender.sendMessage("/" + label + " base 72 " + ChatColor.GRAY + "(sets base size statically)");
		sender.sendMessage("/" + label + " tool wood_spade " + ChatColor.GRAY + "(sets the tool by name)");
		sender.sendMessage("/" + label + " tool 269 " + ChatColor.GRAY + "(sets the tool by ID)");
		sender.sendMessage("/" + label + " scale 3 " + ChatColor.GRAY + "(sets the scale of the beach sand)");
		sender.sendMessage("/" + label + " material 3 12 2 " + ChatColor.GRAY + "(sets materials by ID)");
		sender.sendMessage("/" + label + " material dirt sand grass " + ChatColor.GRAY + "(sets materials by name)");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = ((Player)sender);
		String arglist = "";
		for (int i = 0; i < args.length; i++) {
			arglist = arglist.concat(args[i] + " ");
		}
		// get the landbrush player from the sender
		try {
			LandBrushPlayer lbp = this.landBrushPlayers.get(((Player) sender).getName());
			if (command.getName().equalsIgnoreCase("landbrush") && args.length >= 1) {
				// help commands
				if (args[0].equalsIgnoreCase("help")) {
					showHelp(sender, label);
					return true;
				}
				else if (args[0].equalsIgnoreCase("default")) {
					Material[] dm = { Material.DIRT, Material.SAND, Material.GRASS };
					lbp.setBrushSize(5);
					lbp.setScale(3.0);
					lbp.setMaterials(dm);
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
						lbp.setBaseY((int)lbp.getPlayer().getLocation().getY() - 1);
					} 
					else {
						try {
							lbp.setBaseY(Integer.parseInt(args[1]));
						} 
						catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "ERROR: Neither string or integer value found for tool.");
							return false;
						}
					}
					return true;
				}
				// setting the new tool
				else if (args[0].equalsIgnoreCase("tool")) {
					Material tool = Material.WOOD_SPADE;
					try {
						tool = Material.matchMaterial(args[1]);
					}
					// this material doesn't exist
					catch (NullPointerException e) {
						log.warning(logPrefix + "ERROR: matchMaterial() - Caused by an attempt at setting tool to " + args[1]);
						sender.sendMessage(ChatColor.RED + "ERROR: Invalid material type \"" + args[1] + "\"");
						return true;
					}
					lbp.setTool(tool);				
					return true;
				}
				// setting the scale
				else if (args[0].equalsIgnoreCase("scale")) {
					try {
						lbp.setScale(Double.parseDouble(args[1]));							
					}
					// catch a number format exception which means we didn't enter a number
					catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "ERROR: Scale needs to be a double value (3.0 by default).");
						return true;
					}					
					return true;
				}
				// setting the new tool
				else if (args[0].equalsIgnoreCase("material")) {
					if (args.length == 4) {
						Material[] m = { Material.DIRT, Material.SAND, Material.GRASS };
						m[0] = Material.matchMaterial(args[1]);
						m[1] = Material.matchMaterial(args[2]);
						m[2] = Material.matchMaterial(args[3]);
						if ((m[0].isBlock() || m[0] == null) && m[1].isBlock() && m[2].isBlock()) {
							sender.sendMessage(ChatColor.AQUA + "Materials set to " + ChatColor.YELLOW + m[0].toString() + " " + m[1].toString() + " " + m[2].toString() + ChatColor.AQUA + ".");
							lbp.setMaterials(m);
						}
						else {
							sender.sendMessage(ChatColor.RED + "One or more of your materials were not blocks.");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "ERROR: Not enough materials specified.");
					}
					return true;
				}
				// the time traveling machine
				else if (args[0].equalsIgnoreCase("undo")) {
					lbp.undo();
					return true;
				}
			}
			else {
				showHelp(sender, label);
				return true;
			}
				
		// something else went wrong
		} 
		catch (CommandException e) {
			log.warning(logPrefix + "CAUGHT EXCEPTION:\n\tType: " + e.toString() + "\n\tCommand: " + arglist + "\n\tBy: " + p.getName());
			return false;
		} 
		catch (NullPointerException e) {
			log.warning(logPrefix + "CAUGHT EXCEPTION:\n\tType: " + e.toString() + "\n\tCommand: " + arglist + "\n\tBy: " + p.getName());
			return false;
		} 
		catch (ArrayIndexOutOfBoundsException e) {
			log.warning(logPrefix + "CAUGHT EXCEPTION:\n\tType: " + e.toString() + "\n\tCommand: " + arglist + "\n\tBy: " + p.getName());
			return false;
		}
		
		return false;
	}
	
	// gets the hashmap for landbrush players
	public HashMap<String, LandBrushPlayer> getLandBrushPlayers() {
		return landBrushPlayers;
	}
	
}
