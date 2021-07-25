package dev._2lstudios.koth.commands;

import dev._2lstudios.koth.koth.KothEvent;
import dev._2lstudios.koth.koth.KothManager;
import org.bukkit.entity.Player;

public class KothListCommand {
  public void appendKoth(StringBuilder stringBuilder, String kothName, KothManager kothManager) {
    KothEvent koth = kothManager.getKothEvent(kothName);

    if (koth != null) {
      stringBuilder.append("&a" + kothName + "\n&fMundo: &e" + koth.getWorldName() + "&f (X: &6" + koth.getCenterX()
          + "&f Z: &6" + koth.getCenterZ() + "&f)\n");
    }
  }

  public KothListCommand(Player player, KothManager kothManager) {
    StringBuilder stringBuilder = new StringBuilder("&aTiempos de KoTH:\n");

    for (String kothName : kothManager.getKothEventsNames()) {
      appendKoth(stringBuilder, kothName, kothManager);
    }

    player.sendMessage(kothManager.getKothEventsNames().toString());
  }
}