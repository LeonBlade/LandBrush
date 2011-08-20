package me.leonblade.landbrush;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LandBrushPlayerListener extends PlayerListener {
	
	private final LandBrush plugin;
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	public LandBrushPlayerListener(LandBrush instance) {
		this.plugin = instance;
	}
	
	// when the player joins the server we can add him to the hashmap of landbrush players
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		super.onPlayerJoin(event);
		addLandBrushPlayer(event.getPlayer());
	}
	
	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		// TODO Auto-generated method stub
		super.onPlayerLogin(event);
		addLandBrushPlayer(event.getPlayer());
	}
	
	// makes sure that we add a player into the hashmap
	private void addLandBrushPlayer(Player player) {
		String playerName = player.getName();
		// if our player exists
		if (this.plugin.getLandBrushPlayers().get(playerName) != null) {
			Player pl = player;
			try {
				pl = this.plugin.getServer().getPlayer(playerName);
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + "Invalid");
			}
			try {
				LandBrushPlayer lbp = this.plugin.getLandBrushPlayers().get(pl.getName());
				lbp.getPlayer().sendMessage(ChatColor.GREEN + "You're a LandBrushPlayer!");
			} catch (Exception e) {
				LandBrushPlayer lbp = new LandBrushPlayer(pl);
				this.plugin.getLandBrushPlayers().put(pl.getName(), lbp);
				pl.sendMessage(ChatColor.GREEN + "You are now a LandBrushPlayer!");
			}
		}
		// add our player to the hashmap only if they're not already in there though
		if (this.plugin.getLandBrushPlayers().get(player.getName()) == null) {
			log.info("["+this.plugin.getDescription().getName()+"] Adding LandBrushPlayer " + player.getName());
			this.plugin.getLandBrushPlayers().put(player.getName(), new LandBrushPlayer(player));
		} else {
			// if they're not there we can reset them
			
		}
	}
	
	// responding to when players interact
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		super.onPlayerInteract(event);
		// get the player based on the object passed in the event and pass through the event
		try {
			LandBrushPlayer lbp = this.plugin.getLandBrushPlayers().get(event.getPlayer().getName());
			lbp.onPlayerEvent(event);
		} catch (Exception e) {
			log.warning("["+this.plugin.getDescription().getName()+"] Error: Player not found in LandBrushPlayer hashmap tried to access onPlayerInteract()");
		}
	}
	
}
