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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LandBrush extends JavaPlugin {
	
	public static HashMap<String, LandBrushPlayer> landBrushPlayers = new HashMap<String, LandBrushPlayer>();
	private final LandBrushPlayerListener landBrushPlayerListener = new LandBrushPlayerListener(this);
	protected static final Logger log = Logger.getLogger("Minecraft");
	private static String logPrefix;
	
	@Override
	public void onDisable() {
		// let the console know our plugin has been disabled
		log.info(logPrefix + "has been disabled.");
	}

	@Override
	public void onEnable() {
		// store log prefix
		logPrefix = "[" + getDescription().getName() + " v" + getDescription().getVersion() + "] ";
		
		// let the console know our plugin has been enabled
		log.info(logPrefix + "has been enabled.");
		
		// add all the players to land brush
		for (Player p : getServer().getOnlinePlayers()) {
			this.addLandBrushPlayer(p);
		}
		
		// get the plugin manager and register some events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, landBrushPlayerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, landBrushPlayerListener, Event.Priority.Normal, this);
	}
	
	// shows the help menu
	private void showHelp(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "LandBrush Help Menu");
		sender.sendMessage("/" + label + " help " + ChatColor.GRAY + "(shows this menu)");
		sender.sendMessage("/" + label + " [on/off] " + ChatColor.GRAY + "(turns plugin on or off for you)");
		sender.sendMessage("/" + label + " default " + ChatColor.GRAY + "(sets settings to default)");
		sender.sendMessage("/" + label + " undo [all/steps] " + ChatColor.GRAY + "(goes back in time via a TARDIS)");
		sender.sendMessage("/" + label + " size 5 " + ChatColor.GRAY + "(sets brush size)");
		sender.sendMessage("/" + label + " base [72/me] " + ChatColor.GRAY + "(sets base height to your Y position)");
		sender.sendMessage("/" + label + " tool [269/wood_spade] " + ChatColor.GRAY + "(sets the tool by name)");
		sender.sendMessage("/" + label + " spread 3 " + ChatColor.GRAY + "(sets the spread of the bottom)");
		sender.sendMessage("/" + label + " material [12/sand 2/grass] " + ChatColor.GRAY + "(sets materials by ID)");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = sender.getServer().getPlayer(sender.getName());
		String arglist = "";
		for (int i = 0; i < args.length; i++) {
			arglist = arglist.concat(args[i] + " ");
		}
		// get the landbrush player from the sender
		try {
			LandBrushPlayer lbp = landBrushPlayers.get(((Player) sender).getName());
			if (command.getName().equalsIgnoreCase("landbrush") && args.length >= 1 && (sender.hasPermission("landbrush.*") || sender.isOp())) {
				// help command				
				if (args[0].equalsIgnoreCase("help")) {
					showHelp(sender, label);
					return true;
				}
				else if (args[0].equalsIgnoreCase("on")) {
					lbp.setState(true);
					sender.sendMessage(ChatColor.AQUA + "You have turned LandBrush " + ChatColor.GOLD + "ON" + ChatColor.AQUA + ".");
					return true;
				}
				else if (args[0].equalsIgnoreCase("off")) {
					lbp.setState(false);
					sender.sendMessage(ChatColor.AQUA + "You have turned LandBrush " + ChatColor.GOLD + "OFF" + ChatColor.AQUA + ".");
					return true;
				}
				else if (args[0].equalsIgnoreCase("default")) {
					Material[] dm = { Material.SAND, Material.GRASS };
					lbp.setBrushSize(5);
					lbp.setSpread(3);
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
						}
					}
					return true;
				}
				// setting the new tool
				else if (args[0].equalsIgnoreCase("tool")) {
					// if we only passed in tool and nothing else
					if (args.length == 1) {
						// if our current item in hand is nothing
						try {
							if (p.getItemInHand().getType().equals(Material.AIR)) {
								// give us the tool
								p.setItemInHand(new ItemStack(lbp.getTool(), 1, (short)0));
							}
							else {
								// if we have an item in our hand set the tool to this item
								lbp.setTool(p.getItemInHand().getType());
							}
						}
						catch (NullPointerException e) {
							
						}
					}
					// if we have passed in something
					else {
						// by default we want to make the tool the wooden shovel
						Material tool = Material.WOOD_SPADE;
						try {
							tool = Material.matchMaterial(args[1]);
						}
						// this material doesn't exist
						catch (NullPointerException e) {
							log.warning(logPrefix + "ERROR: matchMaterial() - Caused by an attempt at setting tool to " + args[1]);
							sender.sendMessage(ChatColor.RED + "ERROR: Invalid material type \"" + args[1] + "\"");
						}
						lbp.setTool(tool);
					}
					return true;
				}
				// setting the scale
				else if (args[0].equalsIgnoreCase("spread")) {
					try {
						lbp.setSpread(Integer.parseInt(args[1]));							
					}
					// catch a number format exception which means we didn't enter a number
					catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "ERROR: Spread needs to be an integer value (3 by default).");
					}					
					return true;
				}
				// setting the new tool
				else if (args[0].equalsIgnoreCase("material")) {
					if (args.length == 3) {
						Material[] m = { Material.SAND, Material.GRASS };
						m[0] = Material.matchMaterial(args[1]);
						m[1] = Material.matchMaterial(args[2]);
						if ((m[0].isBlock()) && m[1].isBlock()) {
							sender.sendMessage(ChatColor.AQUA + "Materials set to " + ChatColor.YELLOW + m[0].toString() + " " + m[1].toString() + ChatColor.AQUA + ".");
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
					try {
						if (args.length == 2) {
							if (args[1].equalsIgnoreCase("all")) {
								sender.sendMessage(ChatColor.GREEN + "Undoing all of your steps...");
								for (int i = 0; i < lbp.getUndoSize(); i++) {
									lbp.undo();
								}
							}
							else {
    							int steps = Integer.parseInt(args[1]);
    							sender.sendMessage(ChatColor.GREEN + "Undoing " + steps + " steps...");
    							for (int i = 0; i < steps; i++) {
    								lbp.undo();
    							}
							}
						} 
						else {
							lbp.undo();
						} 
					} 
					catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "ERROR: Steps needs to be a number.");
					}
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
	
	// makes sure that we add a player into the hashmap
	public void addLandBrushPlayer(Player player) {
		log.info("HashMap: " + landBrushPlayers.toString());
		log.info("Adding " + player.getName());
		if (player.hasPermission("landbrush.*") || player.isOp()) {
			player.sendMessage(ChatColor.GREEN + "LandBrush enabled!");
			// this will try to update the player if it's found in the hashmap
			try {
				LandBrushPlayer lbp = landBrushPlayers.get(player.getName());
				lbp.updatePlayer(player);
			}
			// if we didn't find the player in the hashmap we'll just add thim like normal
			catch (Exception e) {
				landBrushPlayers.put(player.getName(), new LandBrushPlayer(player));
			}
		}
		else {
			log.info("Removing " + player.getName());
			try {
				landBrushPlayers.remove(player.getName());
			} catch (NullPointerException e) {}
		}
	}
	
	// remove them from hashmap
	public void removeLandBrushPlayer(Player player) {
		log.info("Removing " + player.getName());
		try {
			if (landBrushPlayers.containsKey(player.getName())) {
				landBrushPlayers.remove(player.getName());
				player.sendMessage(ChatColor.RED + "LandBrush diabled.");
			}
		}
		catch (NullPointerException e) {}
	}

}
