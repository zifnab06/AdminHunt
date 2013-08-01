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
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;

public class AdminHunt extends JavaPlugin implements Listener{
	private List<String> enabledPlayers;

	@Override
	public void onDisable(){
		getConfig().set("playerList", enabledPlayers);
		saveConfig();
		getLogger().info("AdminHunt has stopped :(");
	}
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this, this);
		//Create new config file - only used for persistance between sessions
		File config_file = new File(getDataFolder(), "config.yml");
		if (!config_file.exists()) {
			getConfig().options().copyDefaults(true);
			getConfig().set("playerList", new ArrayList<String>());
			saveConfig();
		}
		//restore players from last session
		enabledPlayers = getConfig().getStringList("playerList");
		getLogger().info("AdminHunt has been started successfully.");

	}
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		if (command.getName().equalsIgnoreCase("adminhunt") && sender instanceof Player) {
			if (args.length == 0) return false;
			if (sender.hasPermission("adminhunt.toggle")){
				if (!enabledPlayers.contains(args[0])){
					getServer().broadcastMessage(ChatColor.GREEN + "[AdminHunt] An admin hunt as begun! Find (and kill) " + args[0] + " as quickly as you can!" + ChatColor.RESET);
					enabledPlayers.add(args[0]);
				} else {
					getServer().broadcastMessage(ChatColor.GREEN + "[AdminHunt] The admin hunt as ended. " + args[0] + " has been found." + ChatColor.RESET);
					enabledPlayers.remove(args[0]);
				}
				return true;
			}
			return false;
		}
		if (command.getName().equalsIgnoreCase("adminhunt-list") && sender instanceof Player) {
			if(sender.hasPermission("adminhunt.toggle.list")){
				String playerList = "Enabled Players:";
				for (String player : enabledPlayers){
					playerList = playerList + " " + player;
				}
				sender.sendMessage(playerList);
				return true;
			}
			return false;
		}
		return false;

	}
	@EventHandler(priority = EventPriority.LOWEST)
	private void onPVPDamage(DisallowedPVPEvent event){
		Player defender = event.getDefender();
		Player attacker = event.getAttacker();
		if (enabledPlayers.contains(defender.getName()) || enabledPlayers.contains(attacker.getName())) { //cancel event
			event.setCancelled(true);
		}
	}
}
