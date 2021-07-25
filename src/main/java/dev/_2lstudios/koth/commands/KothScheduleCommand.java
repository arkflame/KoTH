package dev._2lstudios.koth.commands;

import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KothScheduleCommand {
  public KothScheduleCommand(Player player, String[] args, String label, KothManager kothManager,
      KothScheduleManager kothScheduleManager) {
    if (!player.hasPermission("koth.admin")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPermisos insuficientes!"));

      return;
    }
    if (args.length < 2) {
      StringBuilder stringBuilder = new StringBuilder();

      stringBuilder.append("&c/" + label + " schedule add <hour> <minute> <name>\n");
      stringBuilder.append("&c/" + label + " schedule remove <hour> <minute>");

      player.sendMessage(ChatColor.translateAlternateColorCodes('&', stringBuilder.toString()));
    } else if (args[1].equalsIgnoreCase("add")) {
      if (args.length < 5) {
        player.sendMessage(ChatColor.RED + "/" + label + " schedule add <hour> <minute> <name>");
      } else {
        try {
          int hour = Integer.parseInt(args[2]);
          int minute = Integer.parseInt(args[3]);
          String name = args[4];

          if (kothManager.getKothEvent(name) != null) {
            kothScheduleManager.add(hour, minute, name);
            player.sendMessage(ChatColor.GREEN + "Agregaste el horario de koth correctamente!");
          } else {
            player.sendMessage(ChatColor.RED + "El KoTH que intentas agregar no existe!");
          }
        } catch (NumberFormatException e) {
          player.sendMessage(ChatColor.RED + "Ingresaste numeros invalidos!");
        }
      }
    } else if (args[1].equalsIgnoreCase("remove")) {
      if (args.length < 4) {
        player.sendMessage(ChatColor.RED + "/" + label + " schedule remove <hour> <minute>");
      } else {
        try {
          int hour = Integer.parseInt(args[2]);
          int minute = Integer.parseInt(args[3]);

          if (kothScheduleManager.remove(hour, minute)) {
            player.sendMessage(ChatColor.GREEN + "Eliminaste el horario de koth correctamente!");
          } else {
            player.sendMessage(ChatColor.RED + "No hay ningun koth establecido en ese horario!");
          }
        } catch (NumberFormatException e) {
          player.sendMessage(ChatColor.RED + "Ingresaste numeros invalidos!");
        }
      }
    }
  }
}