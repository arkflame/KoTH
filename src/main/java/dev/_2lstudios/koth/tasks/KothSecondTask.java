package dev._2lstudios.koth.tasks;

import dev._2lstudios.koth.koth.KothEvent;
import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.koth.KothPlayer;
import dev._2lstudios.koth.schedule.KothSchedule;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import java.time.LocalTime;
import java.util.Collection;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class KothSecondTask implements Runnable {
  private final Server server;
  private final KothScheduleManager kothScheduleManager;
  private final KothManager kothManager;
  private int broadcastTimer = 0;
  private int lastNoPlayerBroadcast = 0;

  public KothSecondTask(Server server, KothScheduleManager kothScheduleManager, KothManager kothManager) {
    this.server = server;
    this.kothScheduleManager = kothScheduleManager;
    this.kothManager = kothManager;
  }

  public void run() {
    Collection<? extends Player> onlinePlayers = this.server.getOnlinePlayers();
    int onlinePlayersCount = onlinePlayers.size();

    this.lastNoPlayerBroadcast++;
    this.broadcastTimer++;

    if (onlinePlayersCount < 8) {
      if (this.lastNoPlayerBroadcast > 300) {
        this.server
            .broadcastMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&',
                "&cFaltan &b" + (8 - onlinePlayersCount) + " &cjugadores para iniciar el &eKoTH&c!")));

        this.lastNoPlayerBroadcast = 0;
      }
    } else {
      LocalTime localTime = LocalTime.now();
      KothSchedule kothSchedule = this.kothScheduleManager.next();

      if (kothSchedule != null) {
        KothEvent kothEvent = this.kothManager.getKothEvent(kothSchedule.getName());

        if (kothEvent != null && !kothEvent.isRunning()) {
          LocalTime kothTime = kothSchedule.getLocalTime();

          if (localTime.getHour() == kothTime.getHour() && localTime.getMinute() == kothTime.getMinute()) {
            this.kothManager.startNext();
          } else {
            kothScheduleManager.updateNext();
          }
        }
      }

      for (KothEvent kothEvent : this.kothManager.getKothEvents()) {
        kothEvent.tick();

        if (this.broadcastTimer >= 15 && kothEvent.isRunning()) {
          kothEvent.broadcast();
          this.broadcastTimer = 0;
        }
      }

      for (KothPlayer kothPlayer : this.kothManager.getKothPlayers()) {
        if (kothPlayer.getKothEvent() != null)
          kothPlayer.setSecondsInside(kothPlayer.getSecondsInside() + 1);
      }
    }
  }
}