package me.leonblade.landbrush;

import java.util.Random;

import me.leonblade.landbrush.LandBrushPlayer.LBUndo;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class Brush {

	private LandBrushPlayer lbPlayer;
	private int targetY;
	private Material type;
	private LBUndo undo = new LBUndo();
	
	public Brush(LandBrushPlayer player) {
		this.lbPlayer = player;
	}
	
	public void draw() {
		// get our hitblox
		HitBlox hb = new HitBlox(this.lbPlayer.getPlayer(), this.lbPlayer.getPlayer().getWorld());
		
		// grab our cursor position block
		Block b = hb.getTargetBlock();
		Random rand = new Random();
		Integer tempRadius;
		
		// first we want to lay down dirt at the very bottom so we set the material to dirt
		type = Material.DIRT;
		tempRadius = this.lbPlayer.getBrushSize() + (int) (this.lbPlayer.getBrushSize() * this.lbPlayer.getScale()) + rand.nextInt((int) this.lbPlayer.getScale()) + 1;
		targetY = this.lbPlayer.getBaseY();
		circle(b.getX(), b.getZ(), tempRadius);
		
		// now we can lay down some sand
		type = Material.SAND;
		tempRadius = this.lbPlayer.getBrushSize() + (int) (this.lbPlayer.getBrushSize() * (this.lbPlayer.getScale()*0.5)) + rand.nextInt((int) this.lbPlayer.getScale()) + 1;
		targetY++;
		circle(b.getX(), b.getZ(), tempRadius);
		
		// now we can lay down some more sand
		type = Material.SAND;
		tempRadius = this.lbPlayer.getBrushSize() + (int) (this.lbPlayer.getBrushSize() * (this.lbPlayer.getScale()*0.2)) + rand.nextInt((int) this.lbPlayer.getScale()) + 1;
		targetY++;
		circle(b.getX(), b.getZ(), tempRadius);
		
		// now we can lay down one more sand disk
		type = Material.SAND;
		tempRadius = this.lbPlayer.getBrushSize() + rand.nextInt(2) + 1;
		targetY++;
		circle(b.getX(), b.getZ(), tempRadius);
		
		// now we can lay down one more sand disk
		type = Material.GRASS;
		// setting the range to the this.lbPlayer.getBrushSize()
		tempRadius = this.lbPlayer.getBrushSize();
		targetY++;
		circle(b.getX(), b.getZ(), tempRadius);	
		
		// push our undo to the player
		this.lbPlayer.addUndoStep(undo);
	}
	
	// draw circle will draw a circle based on a this.lbPlayer.getBrushSize() and a starting block
	// based on Bresenham's circle algorithm :)
	private void circle(int cx, int cy, int radius) {
		int f = 1 - radius;
		int ddF_x = 1;
		int ddF_y = -2 * radius;
		int x = 0;
		int y = radius;
		
		setBlock(cx, cy + radius);
		setBlock(cx, cy - radius);
		setBlock(cx + radius, cy);
		setBlock(cx - radius, cy);
		
		// fill in the mario coin gap
		lineTo(cx - radius, cy, cx + radius, cy);
		
		while (x < y) {
			if (f >= 0) {
				y--;
				ddF_y += 2;
				f += ddF_y;
			}
			x++;
			ddF_x += 2;
			f += ddF_x;
			
			lineTo(cx - x, cy + y, cx + x, cy + y);
			lineTo(cx - x, cy - y, cx + x, cy - y);
			lineTo(cx - y, cy + x, cx + y, cy + x);
			lineTo(cx - y, cy - x, cx + y, cy - x);
		}
	}
	
	// draw a line with the block from one point to another
	// based on Bresenham's line algorithm :)
	private void lineTo(int x0, int y0, int x1, int y1) {
		int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
		int dy = Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
		int err = (dx > dy ? dx : -dy)/2;
		while (true) {
			setBlock(x0, y0);
			if (x0 == x1 && y0 == y1) break;
			int e2 = err;
			if (e2 > -dx) { err -= dy; x0 += sx; }
			if (e2 < dy) { err += dx; y0 += sy; }
		}
	}
	
	// this will set the block in the X and Z position
	private void setBlock(int x, int z) {
		// grab the block
		Block b = this.lbPlayer.getPlayer().getWorld().getBlockAt(x, targetY, z);
		// place the block there if a block in that position in this draw already hasn't been made
		if (undo.hm.get(b.getLocation()) == null)
			undo.put(b);		
		// now we can change the block after we saved it in our hashmap
		b.setType(type);
	}	
}
