package dev._2lstudios.koth.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev._2lstudios.koth.koth.KothManager;

public class PlayerJoinListener implements Listener {
  private final KothManager kothManager;

  public PlayerJoinListener(KothManager kothManager) {
    this.kothManager = kothManager;
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerJoin(final PlayerJoinEvent event) {
    this.kothManager.removePlayer(event.getPlayer().getUniqueId());
  }
}