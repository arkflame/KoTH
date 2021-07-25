package dev._2lstudios.koth.commands;

import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KothCommand implements CommandExecutor {
  private final KothManager kothManager;
  private final KothScheduleManager kothScheduleManager;

  public KothCommand(KothManager kothManager, KothScheduleManager kothScheduleManager) {
    this.kothManager = kothManager;
    this.kothScheduleManager = kothScheduleManager;
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;

      if (args.length > 0) {
        if (args[0].equalsIgnoreCase("start")) {
          new KothStartCommand(player, this.kothManager);
        } else if (args[0].equalsIgnoreCase("stop")) {
          new KothStopCommand(player, this.kothManager);
        } else if (args[0].equalsIgnoreCase("list")) {
          new KothListCommand(player, this.kothManager);
        } else if (args[0].equalsIgnoreCase("create")) {
          new KothCreateCommand(args, player, this.kothManager);
        } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
          new KothRemoveCommand(args, player, this.kothManager);
        } else if (args[0].equalsIgnoreCase("schedule")) {
          new KothScheduleCommand(player, args, label, this.kothManager, this.kothScheduleManager);
        } else if (args[0].equalsIgnoreCase("times")) {
          new KothTimesCommand(sender, this.kothScheduleManager, this.kothManager);
        } else if (args[0].equalsIgnoreCase("set")) {
          new KothSetCommand(args, player, this.kothManager);
        } else {
          new KothHelpCommand(player, label);
        }
      } else {
        new KothHelpCommand(player, label);
      }
    } else {
      sender
          .sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo puedes usar este comando desde la consola!"));
    }

    return true;
  }
}