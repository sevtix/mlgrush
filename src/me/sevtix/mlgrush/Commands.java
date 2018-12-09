package me.sevtix.mlgrush;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {

	private Plugin plugin;

	public Commands(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {

		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("mlgrush")) {
				Player p = (Player) sender;

				if (p.hasPermission(MLGRush.ADMIN_PERMISSION)) {
					switch (args.length) {
					case 0:
						p.sendMessage(MLGRush.PREFIX + "§7Release 1.0 by sevtixdev");
						break;

					case 1:
						switch (args[0]) {
						case "serviceon":
							MLGRush.status = RoomStatus.SERVICE;
							p.sendMessage(MLGRush.PREFIX + "§aService Mode AN");
							break;
							
						case "serviceoff":
							MLGRush.status = RoomStatus.WAITING;
							p.sendMessage(MLGRush.PREFIX + "§cService Mode AUS");
							break;
							
							
						case "setrespawnheight":
							MLGRush.setRespawnHeight(p, plugin);
							break;
							
						case "setbuildheight":
							MLGRush.setBuildHeight(p, plugin);
							break;
							
							
						}
						break;

					case 2:

						switch (args[0]) {
						case "setspawn":
							try {
								int id = Integer.parseInt(args[1]);
								if (id >= 1 && id <= 2) {
									MLGRush.setSpawn(p, id, plugin);
								} else {
									p.sendMessage(MLGRush.PREFIX + "§cEs ist ein Fehler aufgetreten: ID muss zwischen 1 und 2 liegen");
									return false;
								}
							} catch (Exception e) {
								p.sendMessage(MLGRush.PREFIX + "§cEs ist ein Fehler aufgetreten: " + e.getMessage());
							}
							break;

						case "tp":
							try {
								int id = Integer.parseInt(args[1]);
								if (id >= 1 && id <= 2) {
									MLGRush.tpToSpawn(p, id, plugin);
								} else {
									p.sendMessage(MLGRush.PREFIX + "§cEs ist ein Fehler aufgetreten: ID out of range");
									return false;
								}
							} catch (Exception e) {
								p.sendMessage(MLGRush.PREFIX + "§cEs ist ein Fehler aufgetreten: " + e.getMessage());
							}

							break;

						}

						break;

					}
				} else {
					p.sendMessage(MLGRush.PREFIX + "§cDu hast keine Rechte für diesen Befehl");
				}
			}
		}
		return false;
	}

}
