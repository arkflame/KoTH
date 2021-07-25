package dev._2lstudios.koth.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KothHelpCommand {
  public KothHelpCommand(Player player, String label) {
    StringBuilder stringBuilder = new StringBuilder("&aComandos disponibles:\n");

    stringBuilder.append("&e/" + label + " times&7 - &bRevisa los tiempos de KoTH!\n");
    stringBuilder.append("&e/" + label + " start&7 - &bInicia el siguiente KoTH!\n");
    stringBuilder.append("&e/" + label + " list&7 - &bVe un listado KoTHs!\n");
    stringBuilder.append("&e/" + label + " create&7 - &bCrea un nuevo KoTH!\n");
    stringBuilder.append("&e/" + label + " remove&7 - &bRemueve KoTH existente!\n");
    stringBuilder.append("&e/" + label + " schedule&7 - &bPlanea horarios para KoTHs!\n");
    stringBuilder.append("&e/" + label + " set&7 - &bCambia opciones del KoTH!");

    player.sendMessage(ChatColor.translateAlternateColorCodes('&', stringBuilder.toString()));
  }
}