package dev._2lstudios.koth.listeners;

import dev._2lstudios.koth.koth.KothEvent;
import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.koth.KothPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
  private final KothManager kothManager;

  public PlayerMoveListener(KothManager kothManager) {
    this.kothManager = kothManager;
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onPlayerMove(PlayerMoveEvent event) {
    Location from = event.getFrom();
    Location to = event.getTo();

    if (!from.equals(to)) {
      Player player = event.getPlayer();
      KothPlayer kothPlayer = this.kothManager.getPlayer(player.getUniqueId());

      if (kothPlayer != null) {
        KothEvent kothEvent = kothPlayer.getKothEvent();

        if (kothEvent != null && (player.isDead() || player.getGameMode() == GameMode.SPECTATOR)) {
          kothPlayer.setKothEvent(null);

          return;
        }

        Location toLocation = event.getTo();
        boolean inside = false;

        for (KothEvent kothEvent1 : this.kothManager.getKothEvents()) {
          if (kothEvent1.isRunning() && kothEvent1.isInside(toLocation)) {
            kothPlayer.setKothEvent(kothEvent1);
            inside = true;

            break;
          }
        }
        if (inside && kothEvent == null) {
          player.sendMessage(ChatColor.GREEN + "Estas capturando el KoTH!");
        } else if (!inside && kothEvent != null) {
          kothPlayer.setKothEvent(null);
          player.sendMessage(ChatColor.RED + "Ya no estas capturando el KoTH!");
        }
      }
    }
  }
}