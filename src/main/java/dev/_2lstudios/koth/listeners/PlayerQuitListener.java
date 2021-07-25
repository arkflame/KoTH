package dev._2lstudios.koth.listeners;

import dev._2lstudios.koth.koth.KothManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
  private final KothManager kothManager;

  public PlayerQuitListener(KothManager kothManager) {
    this.kothManager = kothManager;
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.kothManager.removePlayer(event.getPlayer().getUniqueId());
  }
}