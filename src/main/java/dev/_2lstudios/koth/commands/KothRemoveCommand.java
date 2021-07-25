package dev._2lstudios.koth.commands;

import dev._2lstudios.koth.koth.KothManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KothRemoveCommand {
  public KothRemoveCommand(String[] args, Player player, KothManager kothManager) {
    if (!player.hasPermission("koth.admin")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPermisos insuficientes!"));

      return;
    }
    if (kothManager.removeKothEvent(args[1])) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEliminaste el KoTH &b" + args[1] + "&c!"));
    } else {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEl KoTH &b" + args[1] + "&c no existe!"));
    }
  }
}