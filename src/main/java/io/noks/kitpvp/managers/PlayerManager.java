package io.noks.kitpvp.managers;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import io.noks.kitpvp.Main;
import io.noks.kitpvp.managers.caches.Ability;
import io.noks.kitpvp.managers.caches.CombatTag;
import io.noks.kitpvp.managers.caches.Economy;
import io.noks.kitpvp.managers.caches.PlayerSettings;
import io.noks.kitpvp.managers.caches.Stats;

public class PlayerManager {
	public static final Map<UUID, PlayerManager> players = Maps.newConcurrentMap();
	private final Player player;
	private final UUID playerUUID;
	private Ability ability;
	private boolean usedSponsor;
	private boolean usedRecraft;
	private boolean allowBuild;
	private final Stats stats;
	private final PlayerSettings settings;
	private final Economy economy;
	private @Nullable CombatTag combatTag;

	public PlayerManager(UUID playerUUID) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.ability = new Ability();
		this.usedSponsor = false;
		this.usedRecraft = false;
		this.allowBuild = false;
		this.stats = new Stats();
		this.settings = new PlayerSettings();
		this.economy = new Economy();
		players.putIfAbsent(playerUUID, this);
	}
	
	public PlayerManager(UUID playerUUID, Stats stats, PlayerSettings settings, Economy economy) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.ability = new Ability();
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
		return this.ability == null;
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
	
	public void kill() {
		if (this.player.getLastDamage() > 0.0D && hasCombatTag()) stats.addDeaths();
		if (stats.getKillStreak() > stats.getBestKillStreak()) {
			stats.updateBestKillStreak();
		}
		if (this.ability.hasAbility()) this.ability.remove();
		if (hasUsedSponsor()) setUsedSponsor(false);
		if (hasUsedRecraft()) setUsedRecraft(false);
		if (hasCombatTag()) this.combatTag = null;
		this.player.eject();
	}
	
	public void giveMainItem() {
		player.getInventory().clear();
		player.getInventory().setItem(0, Main.getInstance().getItemUtils().getAbilitiesSelector());
		player.updateInventory();
		player.setWalkSpeed(0.2F);
		player.setMaximumNoDamageTicks(20);
	}
}
