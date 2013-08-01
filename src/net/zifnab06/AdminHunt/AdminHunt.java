package net.zifnab06.AdminHunt;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.java.JavaPlugin;


public class AdminHunt extends JavaPlugin {
	private List<String> enabledPlayers;

	@Override
	public void onDisable(){
		getConfig().set("playerList", enabledPlayers);
		saveConfig();
	}
	@Override
	public void onEnable(){
		//Create new config file - only used for persistance between sessions
		File config_file = new File(getDataFolder(), "config.yml");
		if (!config_file.exists()) {
			getConfig().options().copyDefaults(true);
			getConfig().set("playerList", new ArrayList<String>());
			saveConfig();
		}
		//restore players from last session
		enabledPlayers = getConfig().getStringList("playerList");
	}
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		if (command.getName().equalsIgnoreCase("pvp-toggle") && sender instanceof Player) {
			if (args.length == 0) return false;
			if(enabledPlayers.size() > 0)
			sender.sendMessage(enabledPlayers.get(0));
			if (sender.hasPermission("adminhunt.toggle")){
				Player player = (Player) sender;
				if (!enabledPlayers.contains(args[0])){
					enabledPlayers.add(args[0]);
				} else {
					enabledPlayers.remove(args[0]);
				}
				return true;
			}
			return false;
		}
		return false;

	}
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	private void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
		Entity defender = event.getEntity();
		Entity attacker = ((Projectile) event.getDamager()).getShooter();
		if (defender instanceof Player && attacker instanceof Player){
			Player player = (Player) defender;
			String defenderName = player.getDisplayName();
			if ( enabledPlayers.contains(defenderName)){
				event.setCancelled(false);
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Projectile) {
			onEntityDamageByProjectile(event);
			return;
		}
		Entity defender = event.getEntity();
		Entity attacker = event.getDamager();
		if (defender instanceof Player && attacker instanceof Player){
			Player player = (Player) defender;
			String defenderName = player.getDisplayName();
			if ( enabledPlayers.contains(defenderName)){
				event.setCancelled(false);
			}
		}
	}

}










