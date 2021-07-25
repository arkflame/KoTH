package dev._2lstudios.koth.placeholders;

import dev._2lstudios.koth.koth.KothEvent;
import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.schedule.KothSchedule;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class KothPlaceholders extends PlaceholderExpansion {
  private final Plugin plugin;
  private final KothManager kothManager;
  private final KothScheduleManager kothScheduleManager;

  public KothPlaceholders(Plugin plugin, KothManager kothManager, KothScheduleManager kothScheduleManager) {
    this.plugin = plugin;
    this.kothManager = kothManager;
    this.kothScheduleManager = kothScheduleManager;
  }

  private String formatNumber(long number) {
    return (number > 9L) ? ("" + number) : ((number < 1L) ? "00" : ("0" + number));
  }

  public String getFormattedDuration(Duration duration) {
    String hour = formatNumber(duration.toHours());
    String minute = formatNumber(duration.toMinutes() % 60L);
    String seconds = formatNumber(duration.toMillis() / 1000L % 60L);

    return hour + ":" + minute + ":" + seconds;
  }

  public String getIdentifier() {
    return "koth";
  }

  public String getAuthor() {
    return this.plugin.getDescription().getAuthors().toString();
  }

  public String getVersion() {
    return this.plugin.getDescription().getVersion();
  }

  public String onPlaceholderRequest(Player player, String identifier) {
    if (this.plugin.isEnabled() && player != null && !identifier.isEmpty()) {
      if (identifier.equalsIgnoreCase("time")) {
        Collection<? extends Player> onlinePlayers = this.plugin.getServer().getOnlinePlayers();
        int onlinePlayersCount = onlinePlayers.size();

        if (onlinePlayersCount >= 0) {
          KothSchedule currentSchedule = this.kothScheduleManager.getCurrent();

          if (currentSchedule != null) {
            KothEvent currentKothEvent = this.kothManager.getKothEvent(currentSchedule.getName());

            if (currentKothEvent != null) {
              return currentKothEvent.getTimeLeft();
            }
          } else {
            KothSchedule nextSchedule = this.kothScheduleManager.next();

            if (nextSchedule != null) {
              LocalTime nextScheduleTime = nextSchedule.getLocalTime();
              LocalTime localTime = LocalTime.now();
              Duration duration = Duration.between(localTime, nextScheduleTime);

              return getFormattedDuration(duration);
            }
          }
        }
      }

      return String.valueOf("--:--:--");
    }
    return null;
  }
}