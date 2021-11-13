package us.noks.kitpvp.managers;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import net.minecraft.server.EntityPlayer;
import us.noks.kitpvp.managers.caches.Ability;
import us.noks.kitpvp.managers.caches.Economy;
import us.noks.kitpvp.managers.caches.Settings;
import us.noks.kitpvp.managers.caches.Stats;
import us.noks.kitpvp.utils.ItemUtils;

public class PlayerManager {
	public static final Map<UUID, PlayerManager> players = Maps.newConcurrentMap();
	private Player player;
	private UUID playerUUID;
	private Ability ability;
	private boolean useSponsor;
	private boolean useRecraft;
	private boolean allowBuild;
	private Stats stats;
	private Settings settings;
	private Economy economy;

	public PlayerManager(UUID playerUUID) {
		this.playerUUID = playerUUID;
		this.player = Bukkit.getPlayer(this.playerUUID);
		this.ability = new Ability();
		this.useSponsor = false;
		this.useRecraft = false;
		this.allowBuild = false;
		this.stats = new Stats();
		this.settings = new Settings();
		this.economy = new Economy();
	}

	public static void create(UUID uuid) {
		players.putIfAbsent(uuid, new PlayerManager(uuid));
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

	public EntityPlayer getCraftPlayer() {
		return ((CraftPlayer) getPlayer()).getHandle();
	}

	public UUID getPlayerUUID() {
		return this.playerUUID;
	}

	public Ability getAbility() {
		return this.ability;
	}

	public boolean hasUseSponsor() {
		return this.useSponsor;
	}

	public void setUseSponsor(boolean use) {
		this.useSponsor = use;
	}

	public boolean hasUseRecraft() {
		return this.useRecraft;
	}

	public void setUseRecraft(boolean use) {
		this.useRecraft = use;
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

	public Settings getSettings() {
		return this.settings;
	}

	public Economy getEconomy() {
		return this.economy;
	}

	public void giveMainItem() {
		this.player.getInventory().clear();
		this.player.getInventory().setItem(0, ItemUtils.getInstance().getAbilitiesSelector());
		this.player.updateInventory();
		this.player.setWalkSpeed(0.2F);
		this.player.setMaximumNoDamageTicks(10);
	}
}
