package me.sevtix.mlgrush;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Actions implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (MLGRush.status == RoomStatus.SERVICE) {
			return;
		}
		e.setDamage(0);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if (MLGRush.status == RoomStatus.SERVICE) {
			return;
		}

		e.setCancelled(true);
		MLGRush.broadcastToPlayer(e.getPlayer(), "§cDu darfst keine Items droppen");
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (MLGRush.status == RoomStatus.SERVICE) {
			return;
		}

		if (MLGRush.status == RoomStatus.WAITING || MLGRush.movementLock) {
			e.setTo(e.getFrom());
		}

		if (MLGRush.status == RoomStatus.PLAYING) {
			if (e.getPlayer().getLocation().getY() < MLGRush.respawnHeight) {
				MLGRush.tpToSpawn(e.getPlayer(), MLGRush.playerList.get(e.getPlayer()), Main.instance);
			}

		}
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		e.setCancelled(true);
		e.setFoodLevel(20);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(e.getClickedBlock().getType() == Material.BED_BLOCK) {
				e.setCancelled(true);
				MLGRush.broadcastToPlayer(e.getPlayer(), "§cKeine Zeit zum schlafen!");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreakEvent(BlockBreakEvent e) {
		if (MLGRush.status == RoomStatus.SERVICE) {
			return;
		}

		if (e.getBlock().getType() == Material.SANDSTONE) {
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			MLGRush.placedBlocks.remove(e.getBlock());
		} else {
			e.setCancelled(true);
		}

		if (e.getBlock().getType() == Material.BED_BLOCK) {
			
			Block toCheck = e.getBlock().getLocation().subtract(0D, 1D, 0D).getBlock();

			switch (toCheck.getData()) {
			case 14:
				if(MLGRush.playerList.get(e.getPlayer()) == 2) {
					MLGRush.player2Points++;
					e.getPlayer().setExp(0.99f);
					e.getPlayer().setLevel(MLGRush.player2Points);
					MLGRush.broadcastForAll(e.getPlayer().getPlayerListName() + " §7hat das §7Bett von Team §cRot §7zerstört");
					MLGRush.resetGame();
					MLGRush.clearCachedBlocks();
				} else {
					MLGRush.broadcastToPlayer(e.getPlayer(), "§cBist du sicher, dass du hier richtig bist?!");
				}

				break;
			case 11:
				// BETT VON TEAM BLAU WURDE ABGEBAUT
				if(MLGRush.playerList.get(e.getPlayer()) == 1) {
					MLGRush.player1Points++;
					e.getPlayer().setExp(0.99f);
					e.getPlayer().setLevel(MLGRush.player1Points);
					MLGRush.broadcastForAll(e.getPlayer().getPlayerListName() + " §7hat das §7Bett von Team §bBlau §7zerstört");
					MLGRush.resetGame();
					MLGRush.clearCachedBlocks();
				} else {
					MLGRush.broadcastToPlayer(e.getPlayer(), "§cBist du sicher, dass du hier richtig bist?!");
				}
				break;
			}

		}
	}

	@EventHandler
	public void onPlaceEvent(BlockPlaceEvent e) {
		if (MLGRush.status == RoomStatus.SERVICE) {
			return;
		}
		
		if(MLGRush.status == RoomStatus.PLAYING) {
			
			if(e.getBlockPlaced().getY() >= MLGRush.buildHeight) {
				e.setCancelled(true);
			} else {
				MLGRush.placedBlocks.add(e.getBlockPlaced());
				System.out.println("---- Cached placed block from " + e.getPlayer() + " in placedBlocks");
			}
			

		} else {
			e.setCancelled(true);
			e.setBuild(false);
		}


	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (MLGRush.status == RoomStatus.SERVICE) {
			return;
		}

		e.setJoinMessage(null);
		e.getPlayer().setMaxHealth(2);
		e.getPlayer().setHealth(2);
		e.getPlayer().setFoodLevel(20);

		if (MLGRush.status == RoomStatus.WAITING && Bukkit.getOnlinePlayers().size() <= 2) {
			// ALLOWED FOR JOINING
			MLGRush.joinPlayer(e.getPlayer());
		}

		MLGRush.tryStartGame();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (MLGRush.status == RoomStatus.SERVICE) {
			return;
		}

		MLGRush.clearCachedBlocks();

		e.setQuitMessage(null);
		MLGRush.quitPlayer(e.getPlayer());
		if (MLGRush.status == RoomStatus.PLAYING) {
			MLGRush.kickAll();
		}
	}
}
