package dev._2lstudios.koth.commands;

import dev._2lstudios.koth.koth.KothEvent;
import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.schedule.KothSchedule;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import java.time.LocalTime;
import java.util.Collection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KothTimesCommand {
  private final KothManager kothManager;

  private String formatNumber(int number) {
    return (number > 9) ? ("" + number) : ("0" + number);
  }

  public String getFormattedTime(LocalTime localTime) {
    String hour = formatNumber(localTime.getHour());
    String minute = formatNumber(localTime.getMinute());

    return hour + ":" + minute;
  }

  public void appendKothTime(StringBuilder stringBuilder, KothSchedule kothSchedule) {
    KothEvent koth = this.kothManager.getKothEvent(kothSchedule.getName());

    if (koth != null) {
      stringBuilder.append("&6" + kothSchedule.getFormattedTime() + "&7 - &a" + kothSchedule.getName() + "\n&fMundo: &e"
          + koth.getWorldName() + "&f (X: &6" + koth.getCenterX() + "&f Z: &6" + koth.getCenterZ() + "&f)\n");
    }
  }

  public KothTimesCommand(CommandSender sender, KothScheduleManager kothScheduleManager, KothManager kothManager) {
    this.kothManager = kothManager;

    Collection<KothSchedule> schedules = kothScheduleManager.getSchedules();
    String formattedTime = getFormattedTime(LocalTime.now());

    if (schedules.isEmpty()) {
      sender.sendMessage(
          ChatColor.RED + "No hay tiempos de koth establecidos!\nHorario actual: " + ChatColor.YELLOW + formattedTime);
    } else {
      StringBuilder scheduleBuilder = new StringBuilder("&aTiempos de KoTH:\n");

      for (KothSchedule kothSchedule : schedules) {
        appendKothTime(scheduleBuilder, kothSchedule);
      }

      scheduleBuilder.append("\n\n&aHorario actual: &e" + formattedTime);

      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', scheduleBuilder.toString()));
    }
  }
}