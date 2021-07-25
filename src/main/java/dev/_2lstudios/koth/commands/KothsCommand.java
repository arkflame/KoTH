package dev._2lstudios.koth.commands;

import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KothsCommand implements CommandExecutor {
  private final KothManager kothManager;
  private final KothScheduleManager kothScheduleManager;

  public KothsCommand(KothManager kothManager, KothScheduleManager kothScheduleManager) {
    this.kothManager = kothManager;
    this.kothScheduleManager = kothScheduleManager;
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof org.bukkit.entity.Player) {
      new KothTimesCommand(sender, this.kothScheduleManager, this.kothManager);
    } else {
      sender
          .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo puedes usar este comando desde la consola!"));
    }

    return true;
  }
}