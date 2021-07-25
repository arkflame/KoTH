package dev._2lstudios.koth.commands;

import dev._2lstudios.koth.koth.KothManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KothStartCommand {
  public KothStartCommand(Player player, KothManager kothManager) {
    if (!player.hasPermission("koth.admin")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPermisos insuficientes!"));

      return;
    }
    if (!kothManager.startNext())
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "No hay ningun koth para comenzar!"));
  }
}