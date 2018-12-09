package me.sevtix.mlgrush;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

public class MLGRush {

	public static boolean isRunning = false;
	public static boolean movementLock = false;
	public static RoomStatus status = RoomStatus.WAITING;

	// PLAYER HASHMAP

	public static HashMap<Player, Integer> playerList = new HashMap<>();
	public static Double respawnHeight = 0D;
	public static Double buildHeight = 0D;

	public static String ADMIN_PERMISSION = "mlgrush.admin";
	public static String PREFIX = "§8[§cMLG§l§bRush§8] ";
	
	public static int player1Points = 0;
	public static int player2Points = 0;
	
	public static Scoreboard scoreboard;
	
	public static ArrayList<Block> placedBlocks = new ArrayList<>();

	
	// INIT
	// --------------------------------------------------------------------

	public static void loadResources(Plugin plugin) {
		respawnHeight = plugin.getConfig().getDouble("params.misc.respawnheight");
		buildHeight = plugin.getConfig().getDouble("params.misc.buildheight");
		
		scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		scoreboard.registerNewTeam("Rot");
		scoreboard.registerNewTeam("Blau");
		scoreboard.getTeam("Rot").setPrefix("§c");
		scoreboard.getTeam("Rot").setDisplayName("§c");
		scoreboard.getTeam("Blau").setPrefix("§b");
		scoreboard.getTeam("Blau").setDisplayName("§b");
	}
	
	
	// SPAWN MANAGEMENT
	// --------------------------------------------------------------------

	public static void setRespawnHeight(Player p, Plugin plugin) {
		plugin.getConfig().set("params.misc.respawnheight", p.getLocation().getY());
		plugin.saveConfig();
		p.sendMessage(PREFIX + "§aRespawnhöhe erfolgreich gesetzt!");
	}
	
	public static void setBuildHeight(Player p, Plugin plugin) {
		plugin.getConfig().set("params.misc.buildheight", p.getLocation().getY());
		plugin.saveConfig();
		p.sendMessage(PREFIX + "§aMaximale Bauhöhe erfolgreich gesetzt!");
	}
	
	public static void setSpawn(Player p, int id, Plugin plugin) {
		plugin.getConfig().set("params.spawn" + id + ".world", p.getWorld().getName());
		plugin.getConfig().set("params.spawn" + id + ".x", p.getLocation().getX());
		plugin.getConfig().set("params.spawn" + id + ".y", p.getLocation().getY());
		plugin.getConfig().set("params.spawn" + id + ".z", p.getLocation().getZ());
		plugin.getConfig().set("params.spawn" + id + ".yaw", p.getLocation().getYaw());
		plugin.getConfig().set("params.spawn" + id + ".pitch", p.getLocation().getPitch());
		plugin.saveConfig();
		p.sendMessage(PREFIX + "§aSpawnpunkt " + id + " erfolgreich gesetzt!");
	}

	public static void tpToSpawn(Player p, int id, Plugin plugin) {
		double x = plugin.getConfig().getDouble("params.spawn" + id + ".x");
		double y = plugin.getConfig().getDouble("params.spawn" + id + ".y");
		double z = plugin.getConfig().getDouble("params.spawn" + id + ".z");
		float yaw = (float) plugin.getConfig().getDouble("params.spawn" + id + ".yaw");
		float pitch = (float) plugin.getConfig().getDouble("params.spawn" + id + ".pitch");
		Location loc = new Location(Bukkit.getWorld(plugin.getConfig().getString("params.spawn" + id + ".world")), x, y,
				z, yaw, pitch);
		p.teleport(loc);
		p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
		giveItems(p);
		p.setFoodLevel(20);
	}

	@SuppressWarnings("deprecation")
	public static void resetGame() {

		MLGRush.movementLock = true;
		
		Bukkit.getScheduler().scheduleAsyncDelayedTask(Main.instance, new Runnable() {
			
			@Override
			public void run() {
				MLGRush.movementLock = false;
			}
		}, 20);
		
		int index = 1;

		for (Player each : playerList.keySet()) {
			tpToSpawn(each, playerList.get(each), Main.instance);
			System.out.println("---- Teleporting " + each.getName() + " to spawn " + index);
			index++;
		}
	}
	
	public static void kickAll() {
		for (Player each : Bukkit.getOnlinePlayers()) {
			each.kickPlayer("§cServer startet neu");
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
	}

	// PLAYER MANAGMENT
	// --------------------------------------------------------------------

	public static void joinPlayer(Player PLAYER) {
		PLAYER.setLevel(0);
		PLAYER.setExp(0.99f);
		playerList.put(PLAYER, Bukkit.getOnlinePlayers().size());
		tpToSpawn(PLAYER, playerList.get(PLAYER), Main.instance);
		applyColoring(PLAYER, playerList.get(PLAYER));
		System.out.println("---- Teleporting " + PLAYER.getName() + " to spawn " + Bukkit.getOnlinePlayers().size());
		broadcastForAll("§a" + PLAYER.getName() + " hat das Spiel betreten");
	}
	
	@SuppressWarnings("deprecation")
	public static void applyColoring(Player PLAYER, int ID) {
		switch(ID) {
		case 1:
			PLAYER.setPlayerListName("§c" + PLAYER.getName());
			scoreboard.getTeam("Rot").addPlayer(PLAYER);
			break;
		
		case 2:
			PLAYER.setPlayerListName("§b" + PLAYER.getName());
			scoreboard.getTeam("Blau").addPlayer(PLAYER);
			break;
		}
		
		setScoreboardForAll();
	}
	
	public static void setScoreboardForAll() {
		for(Player each : Bukkit.getOnlinePlayers()) {
			each.setScoreboard(scoreboard);
		}
	}

	public static void quitPlayer(Player PLAYER) {
		broadcastForAll("§c" + PLAYER.getName() + " hat das Spiel verlassen");
	}

	// BROADCASTING
	// --------------------------------------------------------------------

	public static void broadcastForAll(String MESSAGE) {
		for (Player each : Bukkit.getOnlinePlayers()) {
			each.sendMessage(PREFIX + MESSAGE);
		}
	}

	public static void broadcastToPlayer(Player PLAYER, String MESSAGE) {
		PLAYER.sendMessage(PREFIX + MESSAGE);
	}

	@SuppressWarnings("deprecation")
	public static void titleForAll(String TITLE, String SUBTITLE) {
		for (Player each : Bukkit.getOnlinePlayers()) {
			each.sendTitle(TITLE, SUBTITLE);
			each.playSound(each.getLocation(), Sound.ORB_PICKUP, 1, 1);
		}
	}

	@SuppressWarnings("deprecation")
	public static void titleToPlayer(Player PLAYER, String TITLE, String SUBTITLE) {
		PLAYER.sendTitle(TITLE, SUBTITLE);
		PLAYER.playSound(PLAYER.getLocation(), Sound.ORB_PICKUP, 1, 1);
	}

	// HELPERS
	// --------------------------------------------------------------------------------

	public static void giveItems(Player TO) {
		ItemStack stick = new ItemStack(Material.STICK);
		stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		ItemMeta stickmeta = stick.getItemMeta();
		stickmeta.setDisplayName("§7Stock");
		stickmeta.setLore(null);
		stick.setItemMeta(stickmeta);
		
		ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
		ItemMeta pickaxemeta = pickaxe.getItemMeta();
		pickaxemeta.setDisplayName("§7Hacke");
		pickaxemeta.setLore(null);
		pickaxe.setItemMeta(pickaxemeta);
		
		ItemStack block = new ItemStack(Material.SANDSTONE, 64);
		ItemMeta blockmeta = block.getItemMeta();
		blockmeta.setDisplayName("§7Block");
		blockmeta.setLore(null);
		block.setItemMeta(blockmeta);
		
		
		TO.getInventory().clear();
		TO.getInventory().setItem(0, stick);
		TO.getInventory().setItem(1, pickaxe);
		TO.getInventory().setItem(2, block);
	}
	
	public static void clearCachedBlocks() {
		for(Block b : placedBlocks) {
			b.setType(Material.AIR);
		}
		System.out.println("---- Cleared cached blocks");
	}
	
	public static void tryStartGame() {
		// CHECK IF ENOUGH PLAYERS
		if (Bukkit.getOnlinePlayers().size() == 2) {
			// START GAME
			MLGRush.isRunning = true;
			status = RoomStatus.PLAYING;
			titleForAll("§7Die Spiele", "§abeginnen");
		}
	}

}
