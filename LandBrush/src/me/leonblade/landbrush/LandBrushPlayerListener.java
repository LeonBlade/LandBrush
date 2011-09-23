package me.leonblade.landbrush;

import java.util.logging.Logger;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

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
		this.plugin.addLandBrushPlayer(event.getPlayer());
	}
	
	// responding to when players interact
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		super.onPlayerInteract(event);
		// get the player based on the object passed in the event and pass through the event
		if ((event.getPlayer().hasPermission("landbrush.*") || event.getPlayer().isOp())) {
			try {
				LandBrushPlayer lbp = this.plugin.getLandBrushPlayers().get(event.getPlayer().getName());
				lbp.onPlayerEvent(event);
			} catch (Exception e) {
				this.plugin.addLandBrushPlayer(event.getPlayer());
			}
		}
		else {
			this.plugin.removeLandBrushPlayer(event.getPlayer());
		}
	}
	
}
