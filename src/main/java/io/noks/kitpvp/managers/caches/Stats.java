package io.noks.kitpvp.managers.caches;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;

public class Stats {
	private int kills;
	private int deaths;
	private int killStreak;
	private int bestKillStreak;
	private boolean paused;

	public void set(int kill, int death, int bestKS) {
		this.kills = kill;
		this.deaths = death;
		this.bestKillStreak = bestKS;
		this.killStreak = 0;
		this.paused = false;
	}

	public Double getRatio() {
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

	public String[] toStrings() {
		return new String[] { ChatColor.GRAY + "Kills -> " + ChatColor.RED + this.kills,
				ChatColor.GRAY + "Deaths -> " + ChatColor.RED + this.deaths,
				ChatColor.GRAY + "Current KillStreak -> " + ChatColor.RED + this.killStreak,
				ChatColor.GRAY + "Best KillStreak -> " + ChatColor.RED + this.bestKillStreak,
				ChatColor.GRAY + "Ratio -> " + ChatColor.RED +

						getRatio() };
	}
}
