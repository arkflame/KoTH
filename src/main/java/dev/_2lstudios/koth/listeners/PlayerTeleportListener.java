package dev._2lstudios.koth.listeners;

import dev._2lstudios.koth.koth.KothEvent;
import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.koth.KothPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {
  private final KothManager kothManager;

  public PlayerTeleportListener(KothManager kothManager) {
    this.kothManager = kothManager;
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    Player player = event.getPlayer();
    KothPlayer kothPlayer = this.kothManager.getPlayer(player.getUniqueId());
    KothEvent lastKothEvent = kothPlayer.getKothEvent();
    Location toLocation = event.getTo();
    boolean inside = false;

    for (KothEvent kothEvent : this.kothManager.getKothEvents()) {
      if (kothEvent.isRunning() && kothEvent.isInside(toLocation)) {
        kothPlayer.setKothEvent(kothEvent);
        inside = true;

        break;
      }
    }
    if (inside && lastKothEvent == null) {
      player.sendMessage(ChatColor.GREEN + "Estas capturando el KoTH!");
    } else if (!inside && lastKothEvent != null) {
      kothPlayer.setKothEvent(null);
      player.sendMessage(ChatColor.RED + "Ya no estas capturando el KoTH!");
    }
  }
}