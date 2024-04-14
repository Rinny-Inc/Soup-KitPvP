package io.noks.kitpvp.managers;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.avaje.ebean.validation.NotNull;
import com.google.common.collect.Maps;

import io.noks.kitpvp.enums.RefreshType;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.managers.caches.CombatTag;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.Perks;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.Stats;

public class PlayerManager {
	public static final Map<UUID, PlayerManager> players = Maps.newConcurrentMap();
	private final @NotNull Player player;
	private final @NotNull UUID playerUUID;
	private final @NotNull Ability ability;
	private final @NotNull Perks perks;
	private boolean usedSponsor;
	private boolean usedRecraft;
	private boolean allowBuild;
	private final @NotNull Stats stats;
	private final @NotNull PlayerSettings settings;
	private final @NotNull Economy economy;
	private @Nullable CombatTag combatTag;

	public PlayerManager(UUID playerUUID) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.ability = new Ability();
		this.perks = new Perks();
		this.usedSponsor = false;
		this.usedRecraft = false;
		this.allowBuild = false;
		this.stats = new Stats();
		this.settings = new PlayerSettings();
		this.economy = new Economy();
		players.putIfAbsent(playerUUID, this);
		this.applyScoreboard();
	}
	
	public PlayerManager(UUID playerUUID, Stats stats, PlayerSettings settings, Economy economy, Perks perks) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.ability = new Ability();
		this.perks = perks;
		this.usedSponsor = false;
		this.usedRecraft = false;
		this.allowBuild = false;
		this.stats = stats;
		this.settings = settings;
		this.economy = economy;
		players.putIfAbsent(playerUUID, this);
		if (!settings.hasScoreboardEnabled()) {
			return;
		}
		this.applyScoreboard();
	}

	public static PlayerManager get(UUID playerUUID) {
		if (players.containsKey(playerUUID)) {
			return (PlayerManager) players.get(playerUUID);
		}
		return null;
	}

	public void remove() {
		players.remove(this.playerUUID);
	}

	public Player getPlayer() {
		return this.player;
	}

	public UUID getPlayerUUID() {
		return this.playerUUID;
	}
	
	public boolean isInSpawn() {
		return !this.ability.hasAbility();
	}

	public Ability getAbility() {
		return this.ability;
	}

	public boolean hasUsedSponsor() {
		return this.usedSponsor;
	}

	public void setUsedSponsor(boolean use) {
		this.usedSponsor = use;
	}

	public boolean hasUsedRecraft() {
		return this.usedRecraft;
	}

	public void setUsedRecraft(boolean use) {
		this.usedRecraft = use;
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
		return this.combatTag != null;
	}
	
	public void updateCombatTag(CombatTag newTag) {
		this.combatTag = newTag;
	}
	
	public CombatTag getCurrentCombatTag() {
		return this.combatTag;
	}
	
	public Perks getActivePerks() {
		return this.perks;
	}
	
	public void kill(boolean backToSpawn) {
		if (!backToSpawn) {
			if (hasCombatTag()) {
				if (this.player.getLastDamage() > 0.0D) {
					stats.addDeaths();
				}
				refreshScoreboardLine(RefreshType.DEATHS);
				this.combatTag = null;
			}
			refreshScoreboardLine(RefreshType.KILLSTREAK);
			if (stats.getKillStreak() > stats.getBestKillStreak()) {
				stats.updateBestKillStreak();
			}
		}
		if (this.ability.hasAbility()) this.ability.remove();
		if (hasUsedSponsor()) setUsedSponsor(false);
		if (hasUsedRecraft()) setUsedRecraft(false);
		this.player.eject();
		this.player.setWalkSpeed(0.2F);
		this.player.setMaximumNoDamageTicks(20);
		this.player.setItemOnCursor(null);
	}
	
	public void applyScoreboard() {
		final Scoreboard scoreboard = this.player.getScoreboard();
		if (scoreboard.getObjective(DisplaySlot.SIDEBAR) == null) {
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
		}
	}
	
	public void refreshScoreboardLine(RefreshType type) {
		final Scoreboard board = this.player.getScoreboard();
		switch (type) {
		case KILLS:
			board.getTeam(type.getName()).setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getKills());
			break;
		case KILLSTREAK:
			board.getTeam(type.getName()).setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getKillStreak());
			break;
		case DEATHS:
			board.getTeam(type.getName()).setSuffix(ChatColor.DARK_AQUA.toString() + this.stats.getDeaths());
			break;
		case CREDITS:
			board.getTeam(type.getName()).setSuffix(ChatColor.DARK_AQUA.toString() + this.economy.getMoney());
			break;
		case COMBATTAG:
			final Objective sidebar = board.getObjective(DisplaySlot.SIDEBAR);
			if (sidebar.getScore(ChatColor.RED + "Combat Tag: ") != null && this.combatTag == null) {
				if (board.getTeam(type.getName()) == null) {
					break;
				}
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
			break;
		default:
			break;
		}
	}
	
	private String format(double sec) {
	    int seconds = (int) sec;
	    int milliseconds = (int) ((sec - seconds) * 1000);

	    return String.format("%d.%03ds", seconds, milliseconds);
	}
}
