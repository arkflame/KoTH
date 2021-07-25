package dev._2lstudios.koth.commands;

import dev._2lstudios.koth.koth.KothEvent;
import dev._2lstudios.koth.koth.KothManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KothSetCommand {
  public KothSetCommand(String[] args, Player player, KothManager kothManager) {
    if (!player.hasPermission("koth.admin")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPermisos insuficientes!"));

      return;
    }
    if (args.length > 3) {
      String kothName = args[1];
      String option = args[2];
      String value = args[3];
      KothEvent kothEvent = kothManager.getKothEvent(kothName);

      if (kothEvent != null) {
        if (option.equalsIgnoreCase("pos1")) {
          kothEvent.setPosition1(player.getLocation());
          player
              .sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEstableciste la posicion 1 correctamente!"));
        } else if (option.equalsIgnoreCase("pos2")) {
          kothEvent.setPosition2(player.getLocation());
          player
              .sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEstableciste la posicion 2 correctamente!"));
        } else if (option.equalsIgnoreCase("rewards")) {
          kothEvent.setRewards(player.getInventory().getContents());
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEstableciste los premios correctamente!"));
        } else if (option.equalsIgnoreCase("capturetime")) {
          try {
            kothEvent.setCaptureTime(Integer.parseInt(value) * 1000);
            player.sendMessage(
                ChatColor.translateAlternateColorCodes('&', "&aEstableciste el tiempo de captura correctamente!"));
          } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIngresaste un numero invalido."));
          }
        } else if (option.equalsIgnoreCase("maxtime")) {
          try {
            kothEvent.setMaxTime(Integer.parseInt(value) * 1000);
            player.sendMessage(
                ChatColor.translateAlternateColorCodes('&', "&aEstableciste el tiempo de finalizacion correctamente!"));
          } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIngresaste un numero invalido."));
          }
        } else {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIngresaste una opcion invalida."));
        }
      } else {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEl evento especificado no existe."));
      }
    } else {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&',
          "&c/koth set <koth> <option> <value>\n&cOpciones: pos1, pos2, rewards, capturetime, maxtime"));
    }
  }
}