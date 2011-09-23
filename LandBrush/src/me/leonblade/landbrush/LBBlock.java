package me.leonblade.landbrush;

import org.bukkit.block.Block;

public class LBBlock {
	private int typeId;
	private int x;
    private int y;
    private int z;
    private byte data;
    
	public LBBlock(Block b) {
		this.typeId = b.getTypeId();
		this.x = b.getX();
		this.y = b.getY();
		this.z = b.getZ();
		this.data = b.getData();
	}
	
	public int getTypeId() { return this.typeId; }	
	public int getX() { return this.x; }
	public int getY() { return this.y; }
	public int getZ() { return this.z; }
	public byte getData() { return this.data; }
}
