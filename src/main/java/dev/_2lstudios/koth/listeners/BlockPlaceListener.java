package dev._2lstudios.koth.listeners;

import dev._2lstudios.koth.koth.KothEvent;
import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.koth.KothPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.PlayerInventory;

public class BlockPlaceListener implements Listener {
  private final KothManager kothManager;

  public BlockPlaceListener(KothManager kothManager) {
    this.kothManager = kothManager;
  }

  @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    Block block = event.getBlock();

    if (block != null && block.getType() == Material.getMaterial("WEB")) {
      Player player = event.getPlayer();
      KothPlayer kothPlayer = this.kothManager.getPlayer(player.getUniqueId());

      if (kothPlayer != null) {
        KothEvent kothEvent = kothPlayer.getKothEvent();

        if (kothEvent != null) {
          PlayerInventory inventory = player.getInventory();

          inventory.setItem(inventory.getHeldItemSlot(), null);
          player.sendMessage(ChatColor.RED + "En serio estas intentando buguear el KoTH?");
          event.setCancelled(true);
        }
      }
    }
  }
}