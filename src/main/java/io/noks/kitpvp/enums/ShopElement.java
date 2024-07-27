package io.noks.kitpvp.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ShopElement {
	REPAIR_ALL("Repair All", new ItemStack(Material.IRON_CHESTPLATE, 1), 50, 0, (byte) 100),
	//ADRENALINE("Adrenaline", new ItemStack(Material.POTION, 1), 1000000, 1, (byte) 100),
	APPLES("Golden Apples", new ItemStack(Material.GOLDEN_APPLE, 1), 150, 2, (byte) 50),
	GRANDPA_STICK("GrandPa Stick", new ItemStack(Material.IRON_CHESTPLATE, 1), 25, 3, (byte) 30);
	
	private String name;
	private ItemStack item;
	private int price;
	// TODO private ItemStack loot;
	private byte stock;
	private final byte defaultStock;
	private int slot;
	
	ShopElement(String name, ItemStack item, int price, int slot, byte stock) {
		this.name = name;
		this.item = item;
		this.price = price;
		this.slot = slot;
		this.stock = stock;
		this.defaultStock = stock;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public byte getStockLeft() {
		return this.stock;
	}
	
	public void removeStock() {
		this.stock--;
	}
	public void resetStock() {
		this.stock = defaultStock;
	}
	
	public int getSlot() {
		return this.slot;
	}
}
