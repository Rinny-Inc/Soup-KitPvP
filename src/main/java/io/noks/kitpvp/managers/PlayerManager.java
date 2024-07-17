package io.noks.kitpvp.managers;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.avaje.ebean.validation.NotNull;
import com.google.common.collect.Maps;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.managers.caches.CombatTag;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Guild;
import io.noks.kitpvp.managers.caches.Perks;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.Stats;

public class PlayerManager extends Ability {
	public static final Map<UUID, PlayerManager> players = Maps.newConcurrentMap();
	private final @NotNull Player player;
	private final @NotNull UUID playerUUID;
	private final @NotNull Perks perks;
	private boolean usedSponsor;
	private boolean allowBuild;
	private final @NotNull Stats stats;
	private final @NotNull PlayerSettings settings;
	private final @NotNull Economy economy;
	private @Nullable CombatTag combatTag;
	public @Nullable BukkitTask currentTask; // TEMP FIX
	private @Nullable Guild guild;

	public PlayerManager(UUID playerUUID) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.perks = new Perks();
		this.usedSponsor = false;
		this.allowBuild = false;
		this.stats = new Stats();
		this.settings = new PlayerSettings();
		this.economy = new Economy();
		players.putIfAbsent(playerUUID, this);
		//this.applyScoreboard();
	}
	
	public PlayerManager(UUID playerUUID, Stats stats, PlayerSettings settings, Economy economy, Perks perks, Guild guild) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.perks = perks;
		this.usedSponsor = false;
		this.allowBuild = false;
		this.stats = stats;
		this.settings = settings;
		this.economy = economy;
		this.guild = guild;
		players.putIfAbsent(playerUUID, this);
		if (!settings.hasScoreboardEnabled()) {
			return;
		}
		//this.applyScoreboard();
	}

	public static PlayerManager get(UUID playerUUID) {
		if (players.containsKey(playerUUID)) {
			return players.get(playerUUID);
		}
		return null;
	}

	public void drop() {
		players.remove(this.playerUUID);
	}

	public Player getPlayer() {
		return this.player;
	}

	public UUID getPlayerUUID() {
		return this.playerUUID;
	}

	public boolean hasUsedSponsor() {
		return this.usedSponsor;
	}

	public void setUsedSponsor(boolean use) {
		this.usedSponsor = use;
	}

	public boolean isAllowBuild() {
		return this.allowBuild;
	}

	public void setAllowBuild(boolean allow) {
		this.allowBuild = allow;
	}

	public Stats getStats() {
		return this.stats;
	}

	public PlayerSettings getSettings() {
		return this.settings;
	}

	public Economy getEconomy() {
		return this.economy;
	}
	
	public boolean hasCombatTag() {
		return this.combatTag != null && this.combatTag.getTime() > System.currentTimeMillis();
	}
	
	public void updateCombatTag(CombatTag newTag) {
		if (this.combatTag != null && this.combatTag.getLastAttackerUUID() == newTag.getLastAttackerUUID()) {
			this.combatTag.resetTime();
			return;
		}
		this.combatTag = newTag;
	}
	
	public CombatTag getCurrentCombatTag() {
		return this.combatTag;
	}
	
	public Perks getActivePerks() {
		return this.perks;
	}
	
	public boolean isPartOfAGuild() {
		return this.guild == null;
	}
	
	public void updateGuild(Guild guild) {
		this.guild = guild;
	}
	
	public Guild getGuild() {
		return this.guild;
	}
	
	public void kill(boolean backToSpawn) {
		if (!backToSpawn) {
			if (hasCombatTag()) {
				this.combatTag = null;
			}
			if (this.player.getLastDamage() > 1.5D && this.hasAbility()) {
				stats.addDeaths();
			}
			refreshScoreboardLine(RefreshType.DEATHS, RefreshType.KILLSTREAK);
			if (stats.getKillStreak() > stats.getBestKillStreak()) {
				stats.updateBestKillStreak();
			}
		}
		if (this.hasAbility()) {
			this.removeAbility();
		}
		if (hasUsedSponsor()) setUsedSponsor(false);
		this.player.eject();
		this.player.setWalkSpeed(0.2F);
		this.player.setMaximumNoDamageTicks(20);
		this.player.setLevel(0);
		this.player.setExp(0.0F);
		this.player.setItemOnCursor(null);
	}
	
	/*public void applyScoreboard() {
		final Scoreboard scoreboard = this.player.getServer().getScoreboardManager().getNewScoreboard();
		if (scoreboard.getObjective("life") == null) {
			final Objective life = scoreboard.registerNewObjective("life", "health");
			life.setDisplaySlot(DisplaySlot.BELOW_NAME);
			final char heart = '\u2764';
			life.setDisplayName(ChatColor.RED.toString() + heart);
		}
		final Objective sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		sidebar.setDisplayName(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "SoupWorld");
		Team line;
		if (scoreboard.getTeam("line1") == null) {
			line = scoreboard.registerNewTeam("line1");
			line.setPrefix(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------");
			line.addEntry("-----");
			line.setSuffix("-------");
			sidebar.getScore("-----").setScore(15);
		}
		if (scoreboard.getTeam("kills") == null) {
			line = scoreboard.registerNewTeam("kills");
			line.addEntry("Kills: ");
			line.setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getKills());
			sidebar.getScore("Kills: ").setScore(14);
		}
		if (scoreboard.getTeam("ks") == null) {
			line = scoreboard.registerNewTeam("ks");
			line.addEntry("Killstreak: ");
			line.setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getKillStreak());
			sidebar.getScore("Killstreak: ").setScore(13);
		}
		if (scoreboard.getTeam("deaths") == null) {
			line = scoreboard.registerNewTeam("deaths");
			line.addEntry("Deaths: ");
			line.setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getDeaths());
			sidebar.getScore("Deaths: ").setScore(12);
		}
		if (scoreboard.getTeam("coins") == null) {
			line = scoreboard.registerNewTeam("coins");
			line.addEntry("Credits: ");
			line.setSuffix(ChatColor.DARK_AQUA.toString() + this.economy.getMoney());
			sidebar.getScore("Credits: ").setScore(11);
		}
		if (scoreboard.getTeam("bounty") == null) {
			line = scoreboard.registerNewTeam("bounty");
			line.addEntry("Bounty: ");
		}
		if (scoreboard.getTeam("tag") == null) {
			line = scoreboard.registerNewTeam("tag");
			line.addEntry(ChatColor.RED + "Combat Tag: ");
		}
		if (scoreboard.getTeam("line2") == null) {
			line = scoreboard.registerNewTeam("line2");
			line.setPrefix(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------");
			line.addEntry(ChatColor.RESET.toString() + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "-----");
			line.setSuffix("-------");
			sidebar.getScore(ChatColor.RESET.toString() + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "-----").setScore(10);
		}
		// TODO: COPY THE MAIN SCOREBOARD INTO THIS ONE AND UPDATE EVERYONE ELSE PERSONAL SCOREBOARD TEAM
		this.player.setScoreboard(scoreboard);
	}*/
	
	public void refreshScoreboardLine(RefreshType... rtype) {
		final Scoreboard board = this.player.getScoreboard();
		if (board == Main.getInstance().getServer().getScoreboardManager().getMainScoreboard()) {
			return;
		}
		for (RefreshType type : rtype) {
			final Team team = board.getTeam(type.getName());
			switch (type) {
			case KILLS:
				team.setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getKills());
				continue;
			case KILLSTREAK:
				team.setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getKillStreak());
				continue;
			case DEATHS:
				team.setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getDeaths());
				continue;
			case CREDITS:
				team.setSuffix(ChatColor.DARK_AQUA.toString() + this.economy.getMoney());
				continue;
			/*case COMBATTAG:
				final Objective sidebar = board.getObjective(DisplaySlot.SIDEBAR);
				if (board.getTeam(type.getName()) == null) {
					break;
				}
				if (sidebar.getScore(ChatColor.RED + "Combat Tag: ") != null && this.combatTag == null) {
					board.getScores(ChatColor.RED + "Combat Tag: ").clear();
					sidebar.getScore(ChatColor.RESET.toString() + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "-----").setScore(10);
					this.combatTag = null;
					break;
				}
				if (board.getTeam(type.getName()) != null) {
					board.getTeam(type.getName()).setSuffix(ChatColor.RESET + ": " + this.format(this.combatTag.getRemainingTime()));
					if (sidebar.getScore(ChatColor.RED + "Combat Tag: ") == null) {
						sidebar.getScore(ChatColor.RED + "Combat Tag: ").setScore(10);
						sidebar.getScore(ChatColor.RESET.toString() + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "-----").setScore(9);
					}
					break;
				}
				break;*/
			default:
				continue;
			}
		}
	}
	
	/*private String format(double sec) {
	    int seconds = (int) sec;
	    int milliseconds = (int) ((sec - seconds) * 1000);

	    return String.format("%d.%03ds", seconds, milliseconds);
	}*/
}
