package io.noks.kitpvp.managers.caches;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

public class Stats {
	private int kills;
	private int deaths;
	private int killStreak;
	private int bestKillStreak;
	private int bounty;
	private boolean paused;

	public Stats() {
		this.kills = 0;
		this.deaths = 0;
		this.bestKillStreak = 0;
		this.killStreak = 0;
		this.bounty = 0;
		this.paused = false;
	}
	public Stats(int kill, int death, int bestKS, int bounty) {
		this.kills = kill;
		this.deaths = death;
		this.bestKillStreak = bestKS;
		this.killStreak = 0;
		this.bounty = bounty;
		this.paused = false;
	}

	private Double getRatio() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		df.setDecimalSeparatorAlwaysShown(true);
		double kills = getKills();
		double deaths = getDeaths();
		if (kills == 0.0D && deaths == 0.0D) {
			return Double.valueOf(0.0D);
		}
		if (deaths < 2.0D) {
			return Double.valueOf(kills);
		}
		return Double.valueOf(Math.round(kills / deaths * 100.0D) / 100.0D);
	}

	public int getKills() {
		return this.kills;
	}

	public void addKills() {
		this.kills++;
	}

	public int getDeaths() {
		return this.deaths;
	}

	public void addDeaths() {
		if (this.paused) {
			return;
		}
		this.deaths++;
	}

	public int getBestKillStreak() {
		return this.bestKillStreak;
	}

	public void updateBestKillStreak() {
		if (this.paused) {
			return;
		}
		this.bestKillStreak = this.killStreak;
		this.killStreak = 0;
	}

	public int getKillStreak() {
		return this.killStreak;
	}

	public void addKillStreak() {
		this.killStreak++;
	}
	
	public int getBounty() {
		return this.bounty;
	}

	public String[] toStrings() {
		return new String[] { ChatColor.GRAY + "Kills -> " + ChatColor.GREEN + this.kills,
				ChatColor.GRAY + "Deaths -> " + ChatColor.RED + this.deaths,
				ChatColor.GRAY + "Killstreak -> " + ChatColor.GOLD + this.killStreak,
				ChatColor.GRAY + "Highest Killstreak -> " + ChatColor.GOLD + this.bestKillStreak,
				ChatColor.GRAY + "K/D Ratio -> " + ChatColor.GOLD + getRatio(),
				ChatColor.GRAY + "Bounty -> " + ChatColor.RED + this.bounty};
	}
}
