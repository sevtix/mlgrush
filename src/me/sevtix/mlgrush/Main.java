package me.sevtix.mlgrush;

import java.net.URL;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public static Main instance;
	
	public Main() {
		instance = this;
	}
	
	public boolean checkActivation() {
		try {
			
			URL url = new URL("http://sevtix.cc/mlgrush-activation/activate.php");
			Scanner s = new Scanner(url.openStream());
			
			String read = s.nextLine();
			s.close();
			
			Bukkit.getConsoleSender().sendMessage("§8---- Antwort vom Aktivierungsserver erhalten");
			
			if(read.equals("Aktiviert")) {
				return true;
			} else {
				return false;
			}
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void onEnable() {
		
		Bukkit.getConsoleSender().sendMessage("§8---- [ §cMLG§bRUSH §8] ----");
		Bukkit.getConsoleSender().sendMessage("§8---- Ueberpruefe Lizenzinformationen");
		if(!checkActivation()) {
			Bukkit.getConsoleSender().sendMessage("§8---- §cDieses Plugin ist nicht fuer diesen Server aktiviert");
			Bukkit.getConsoleSender().sendMessage("§8---- §cLizenzanfragen: sevtixdev@gmail.com");
			Bukkit.getConsoleSender().sendMessage("§8---- §cMLGRush wird jetzt deaktiviert");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		Bukkit.getConsoleSender().sendMessage("§8---- §aAktivierung erfolgreich");
		
		this.getServer().getPluginCommand("mlgrush").setExecutor(new Commands(this));
		this.getConfig().options().copyDefaults(true);
		this.getConfig().addDefault("params.spawn1.world", "world");
		this.getConfig().addDefault("params.spawn1.x", 0);
		this.getConfig().addDefault("params.spawn1.y", 0);
		this.getConfig().addDefault("params.spawn1.y", 0);
		this.getConfig().addDefault("params.spawn1.yaw", 0);
		this.getConfig().addDefault("params.spawn1.pitch", 0);
		
		this.getConfig().addDefault("params.spawn2.world", "world");
		this.getConfig().addDefault("params.spawn2.x", 0);
		this.getConfig().addDefault("params.spawn2.y", 0);
		this.getConfig().addDefault("params.spawn2.y", 0);
		this.getConfig().addDefault("params.spawn2.yaw", 0);
		this.getConfig().addDefault("params.spawn2.pitch", 0);
		
		this.getConfig().addDefault("params.misc.respawnheight", 82);
		
		Bukkit.getConsoleSender().sendMessage("§8---- Konfiguration erstellt falls noch nicht vorhanden");
		Bukkit.getConsoleSender().sendMessage("§8---- [ §cMLG§bRUSH §8] ----");
		
		this.saveConfig();
		
		this.getServer().getPluginManager().registerEvents(new Actions(), this);
		MLGRush.loadResources(this);
	}
	
	
	@Override
	public void onDisable() {
		MLGRush.clearCachedBlocks();
	}
	
	
	
}
