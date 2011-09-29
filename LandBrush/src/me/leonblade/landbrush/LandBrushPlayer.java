package me.leonblade.landbrush;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LandBrushPlayer {
	protected static final Logger log = Logger.getLogger("Minecraft");
	private Player player;
	private int baseY = -1;
	private int brushSize = 5;
	private int brushSpread = 5;
	private Material[] materials = { Material.SAND, Material.GRASS };
	private int hashEn = 0;
	private Material tool = Material.WOOD_SPADE;
	private HashMap<Integer, LBUndo> hashUndo = new HashMap<Integer, LBUndo>();
	private Brush brush = new Brush(this);
	private boolean state = true;
	
	// create a new landbrush player and set the player for it
	public LandBrushPlayer(Player player) {
		this.player = player;
	}	
	
	// updating a player rather than remaking one
	public void updatePlayer(Player player) {
		log.info("Updating player " + player.getName() + "...");
		this.player = player;
	}
	
	// land brush undo hash set to hold all
	public static class LBUndo {
		public HashMap<Location, LBBlock> hs = new HashMap<Location, LBBlock>();
		
		// store a new block in the hash set
		public void put(Block b) {
			this.hs.put(b.getLocation(), new LBBlock(b));
		}
	}
	
	// i decided on responding to the event so that the player can handle everything on his own
	public void onPlayerEvent(PlayerInteractEvent event) {
		// if we are currently holding our tool
		if (this.player.getItemInHand().getType() == tool && this.state) {
			// if we are right clicking on the air or a block
			HitBlox hb = new HitBlox(this.player, this.player.getWorld());
			Block b = hb.getTargetBlock();
			
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)	{
				// grab our hitblox and our target block
				// set our base Y position
				setBaseY(b.getY());
			}
			else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				// make sure our base Y is set
				if (getBaseY() < 0) {
					this.player.sendMessage(ChatColor.RED + "ERROR: Please set your base height!");
				} else {
					// grab our brush and start drawing
					brush.draw();
				}
			}
		}
	}
	
	// undo our last action
	public void undo() {
		if (this.hashEn > 0) {
			LBUndo u = this.hashUndo.get(this.hashEn - 1);
			for (LBBlock b : u.hs.values()) {
				setBlock(b);
			}
			this.hashUndo.remove(this.hashEn - 1);
			this.hashEn--;
			this.player.sendMessage(ChatColor.GREEN + "Undo successful " + 
									ChatColor.YELLOW + u.hs.size() + ChatColor.GREEN + " blocks have been replaced.");
		} else {
			this.player.sendMessage(ChatColor.RED + "Nothing left to undo.");
		}
	}
	
	// get undo count
	public int getUndoSize() {
		return this.hashUndo.size();
	}
	
	// gets the state
	public boolean getState() {
		return this.state;
	}
	
	// sets state on or off
	public void setState(boolean s) {
		this.state = s;
	}
	
	// get player
	public Player getPlayer() {
		return this.player;
	}
	
	// get the tool ID
	public Material getTool() {
		return this.tool;
	}
	
	// set the tool ID
	public void setTool(Material m) {
		this.player.sendMessage(ChatColor.AQUA + "LandBrush tool set to " + ChatColor.YELLOW + m.name());
		this.tool = m;
	}
	
	// set the brush size
	public void setBrushSize(int size) {
		if (size >= 15) {
			this.player.sendMessage(ChatColor.GOLD + "WARNING: Be careful using big brush sizes!");
		}
		this.player.sendMessage(ChatColor.AQUA + "Brush size set to " + ChatColor.YELLOW + size);
		this.brushSize = size;
	}
	
	// get the brush size
	public int getBrushSize() {
		return this.brushSize;
	}
	
	// set the base Y
	public void setBaseY(int y) {
		this.player.sendMessage(ChatColor.AQUA + "Base height set to " + ChatColor.YELLOW + y);
		this.baseY = y;
	}	
	
	// get the base Y
	public int getBaseY() {		
		return this.baseY;
	}
	
	// set the scale
	public void setSpread(int s) {
		if (s < 1) {
			this.player.sendMessage(ChatColor.RED + "Please choose a scale of at least 1!");
		} else {
			this.player.sendMessage(ChatColor.AQUA + "Brush scale set to " + ChatColor.YELLOW + s);
			this.brushSpread = s;
			if (s >= 10) {
				this.player.sendMessage(ChatColor.GOLD + "WARNING: Be careful using big scale sizes!");
			}
		}
	}
	
	// get the scale
	public int getSpread() {
		return this.brushSpread;
	}
	
	// set the materials
	public void setMaterials(Material[] m) {
		this.materials = m;
	}
	
	// get the materials
	public Material[] getMaterials() {
		return this.materials;
	}
	
	// add undo action
	public void addUndoStep(LBUndo u) {
		this.hashUndo.put(this.hashEn, u);
		this.hashEn++;
	}
	
	// replace a block to it's former ID and data for undo
	private void setBlock(LBBlock lb) {
		Block b = this.player.getWorld().getBlockAt(lb.getX(), lb.getY(), lb.getZ());
		b.setTypeId(lb.getTypeId());
		b.setData(lb.getData());
	}
}
