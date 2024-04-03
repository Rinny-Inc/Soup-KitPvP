package io.noks.kitpvp.managers;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.avaje.ebean.validation.NotNull;
import com.google.common.collect.Maps;

import io.noks.kitpvp.Main;
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
	
	public void kill() {
		if (this.player.getLastDamage() > 0.0D && hasCombatTag()) stats.addDeaths();
		if (stats.getKillStreak() > stats.getBestKillStreak()) {
			stats.updateBestKillStreak();
		}
		if (this.ability.hasAbility()) this.ability.remove();
		if (hasUsedSponsor()) setUsedSponsor(false);
		if (hasUsedRecraft()) setUsedRecraft(false);
		if (hasCombatTag()) {
			final PlayerManager killer = players.get(this.combatTag.getLastAttackerUUID());
			killer.combatTag = null;
			this.combatTag = null;
		}
		this.player.eject();
	}
	
	public void giveMainItem() {
		player.getInventory().clear();
		player.getInventory().setContents(Main.getInstance().getItemUtils().getSpawnItems());
		player.updateInventory();
		player.setWalkSpeed(0.2F);
		player.setMaximumNoDamageTicks(20);
	}
}
