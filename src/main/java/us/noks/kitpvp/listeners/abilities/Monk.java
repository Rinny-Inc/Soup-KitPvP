package us.noks.kitpvp.listeners.abilities;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import us.noks.kitpvp.Main;
import us.noks.kitpvp.enums.AbilitiesEnum;
import us.noks.kitpvp.managers.PlayerManager;

public class Monk implements Listener {
	private Main plugin;

	public Monk(Main main) {
		this.plugin = main;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent event) {
		ItemStack item = event.getPlayer().getItemInHand();
		Player player = event.getPlayer();
		PlayerManager pm = PlayerManager.get(player.getUniqueId());
		if (pm.getAbility().hasAbility(AbilitiesEnum.MONK) && event.getRightClicked() instanceof Player
				&& item.getType() == Material.BLAZE_ROD) {
			if (pm.getAbility().hasAbilityCooldown()) {
				double cooldown = pm.getAbility().getAbilityCooldown().longValue() / 1000.0D;
				player.sendMessage(ChatColor.RED + "You can use your ability in "
						+ (new DecimalFormat("#.#")).format(cooldown) + " seconds.");
				return;
			}
			pm.getAbility().setAbilityCooldown();
			Player rightClicked = (Player) event.getRightClicked();
			PlayerInventory inv = rightClicked.getInventory();
			int slot = (new Random()).nextInt(inv.getSize());
			ItemStack replaced = inv.getItemInHand();
			if (replaced == null)
				replaced = new ItemStack(Material.AIR);
			ItemStack replacer = inv.getItem(slot);
			if (replacer == null)
				replacer = new ItemStack(Material.AIR);
			inv.setItemInHand(replacer);
			inv.setItem(slot, replaced);
		}
	}
}
