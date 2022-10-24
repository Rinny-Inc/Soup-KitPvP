package io.noks.kitpvp.managers.caches;

import org.bukkit.ChatColor;

public class Economy {
	private int coins;
	private int bronze;
	private int silver;
	private int gold;
	
	public Economy() {
		this.coins = 0;
	}
	public Economy(int money) {
		this.coins = money;
		this.calculate();
	}

	public int getMoney() {
		return this.coins;
	}

	public int getBronze() {
		return this.bronze;
	}

	public int getSilver() {
		return this.silver;
	}

	public int getGold() {
		return this.gold;
	}

	public void add(int amount, MoneyType type) {
		this.coins += amount * type.getDivider() / 100;
		this.calculate();
	}

	public void remove(int amount, MoneyType type) {
		this.coins -= amount * type.getDivider() / 100;
		this.calculate();
	}

	private void calculate() {
		this.bronze = this.coins % MoneyType.BRONZE.getDivider();
		this.silver = this.coins % MoneyType.SILVER.getDivider() / MoneyType.BRONZE.getDivider();
		this.gold = this.coins % MoneyType.GOLD.getDivider() / MoneyType.SILVER.getDivider();
	}

	public String[] toStrings() {
		return new String[] { ChatColor.RED + "Bronze -> " + ChatColor.GOLD + this.bronze,
				ChatColor.RED + "Silver -> " + ChatColor.GRAY + this.silver,
				ChatColor.RED + "Gold -> " + ChatColor.YELLOW + this.gold };
	}

	public enum MoneyType {
		BRONZE(100), SILVER(10000), GOLD(1000000);

		private int divider;

		MoneyType(int divider) {
			this.divider = divider;
		}

		public int getDivider() {
			return this.divider;
		}
	}
}
