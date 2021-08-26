package dev._2lstudios.koth.koth;

import dev._2lstudios.koth.schedule.KothSchedule;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import dev._2lstudios.koth.utils.ConfigurationUtil;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.plugin.Plugin;

public class KothManager {
  private final Plugin plugin;
  private final ConfigurationUtil configurationUtil;
  private final KothScheduleManager kothScheduleManager;
  private Map<UUID, KothPlayer> kothPlayerMap = new HashMap<>();
  private Map<String, KothEvent> kothEvents = new HashMap<>();

  public KothManager(Plugin plugin, ConfigurationUtil configurationUtil, KothScheduleManager kothScheduleManager) {
    String[] kothFiles = (new File(plugin.getDataFolder().getPath() + "/koths/")).list();

    this.plugin = plugin;
    this.configurationUtil = configurationUtil;
    this.kothScheduleManager = kothScheduleManager;

    if (kothFiles != null)
      for (String fileName : kothFiles)
        createKothEvent(fileName.replace(".yml", ""));
  }

  public boolean createKothEvent(String name) {
    if (this.kothEvents.containsKey(name)) {
      return false;
    }
    this.configurationUtil.createConfiguration("%datafolder%/koths/" + name + ".yml");

    this.kothEvents.put(name, new KothEvent(this.plugin, this.configurationUtil, this.kothPlayerMap.values(), name));
    return true;
  }

  public boolean removeKothEvent(String name) {
    this.configurationUtil.deleteConfiguration("%datafolder%/koths/" + name + ".yml");

    return (this.kothEvents.remove(name) != null);
  }

  public KothPlayer getPlayer(UUID uuid) {
    KothPlayer kothPlayer = this.kothPlayerMap.getOrDefault(uuid, null);

    if (kothPlayer == null) {
      kothPlayer = new KothPlayer(uuid);
      this.kothPlayerMap.put(uuid, kothPlayer);
    }

    return kothPlayer;
  }

  public void removePlayer(UUID uuid) {
    this.kothPlayerMap.remove(uuid);
  }

  public Collection<String> getKothEventsNames() {
    return this.kothEvents.keySet();
  }

  public Iterable<KothEvent> getKothEvents() {
    return this.kothEvents.values();
  }

  public KothEvent getKothEvent(String kothName) {
    return this.kothEvents.getOrDefault(kothName, null);
  }

  public Collection<KothPlayer> getKothPlayers() {
    return this.kothPlayerMap.values();
  }

  public boolean startNext() {
    KothSchedule schedule = this.kothScheduleManager.next();

    if (schedule != null) {
      final KothEvent koth = getKothEvent(schedule.getName());

      if (koth != null) {
        koth.start();

        this.kothScheduleManager.setCurrent(schedule);
      }
    }

    return schedule != null;
  }

  public boolean stop() {
    KothSchedule schedule = this.kothScheduleManager.getCurrent();

    if (schedule != null) {
      getKothEvent(schedule.getName()).stop();

      return true;
    }

    return false;
  }
}

/*
 * Location:
 * C:\Users\LinsaFTW\Desktop\KoTH.jar!\dev\_2lstudios\koth\koth\KothManager.
 * class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */