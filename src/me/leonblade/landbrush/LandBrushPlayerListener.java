package me.leonblade.landbrush;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		// TODO Auto-generated method stub
		super.onPlayerQuit(event);
		removeLandBrushPlayer(event.getPlayer());
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		// TODO Auto-generated method stub
		super.onPlayerKick(event);
		removeLandBrushPlayer(event.getPlayer());
	}
	
	// makes sure that we add a player into the hashmap
	private void addLandBrushPlayer(Player player) {
		// add our player to the hashmap only if they're not already in there though
		if (this.plugin.getLandBrushPlayers().get(player.getName()) == null) {
			log.info("["+this.plugin.getDescription().getName()+"] Adding LandBrushPlayer " + player.getName());
			this.plugin.getLandBrushPlayers().put(player.getName(), new LandBrushPlayer(player));
		}
	}
	
	// removes a player from the landbrush hashmap
	// we use a function to make sure just in case if kick doesn't call quit that it will be covered
	// possibly removed layer
	private void removeLandBrushPlayer(Player player) {
		// remove our player if they exist
		log.info("["+this.plugin.getDescription().getName()+"] Removing LandBrushPlayer " + player.getName());
		this.plugin.getLandBrushPlayers().remove(player);
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
