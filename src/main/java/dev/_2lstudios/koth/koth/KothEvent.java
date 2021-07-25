package dev._2lstudios.koth.koth;

import dev._2lstudios.koth.KothPlugin;
import dev._2lstudios.koth.schedule.KothSchedule;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import dev._2lstudios.koth.utils.ConfigurationUtil;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class KothEvent {
  private final Plugin plugin;
  private final ConfigurationUtil configurationUtil;
  private final String name;
  private final Collection<KothPlayer> kothPlayers;
  private Location position1;

  KothEvent(Plugin plugin, ConfigurationUtil configurationUtil, Collection<KothPlayer> kothPlayers, String name) {
    World world;
    this.position1 = null;
    this.position2 = null;
    this.running = false;
    this.plugin = plugin;

    YamlConfiguration yamlConfiguration = configurationUtil.getConfiguration("%datafolder%/koths/" + name + ".yml");
    String worldName = yamlConfiguration.getString("locations.world");
    Vector vector1 = yamlConfiguration.getVector("locations.vector1");
    Vector vector2 = yamlConfiguration.getVector("locations.vector2");
    Collection<ItemStack> rewards = new HashSet<>();
    ConfigurationSection rewardsSection = yamlConfiguration.getConfigurationSection("rewards");
    int maxTime = yamlConfiguration.getInt("maxtime");
    int captureTime = yamlConfiguration.getInt("capturetime");

    if (worldName != null) {
      world = Bukkit.getWorld(worldName);
    } else {
      world = null;
    }
    if (rewardsSection != null)
      for (String rewardKey : rewardsSection.getKeys(false)) {
        rewards.add(yamlConfiguration.getItemStack("rewards." + rewardKey));
      }
    this.configurationUtil = configurationUtil;
    this.kothPlayers = kothPlayers;
    this.name = name;

    if (world != null) {
      if (vector1 != null)
        this.position1 = new Location(world, vector1.getX(), vector1.getY(), vector1.getZ());
      if (vector2 != null) {
        this.position2 = new Location(world, vector2.getX(), vector2.getY(), vector2.getZ());
      }
    }
    if (!rewards.isEmpty()) {
      this.rewards = rewards.<ItemStack>toArray(new ItemStack[0]);
    }
    this.maxTime = maxTime;
    this.captureTime = captureTime;
    this.lastStartTime = System.currentTimeMillis();
  }

  private Location position2;
  private ItemStack[] rewards;
  private long lastStartTime;
  private int captureTime;
  private int maxTime;
  private boolean running;

  public boolean isRunning() {
    return this.running;
  }

  public String getWorldName() {
    return this.position1.getWorld().getName();
  }

  public int getCenterX() {
    return (int) ((this.position1.getX() + this.position2.getX()) / 2.0D);
  }

  public int getCenterZ() {
    return (int) ((this.position1.getZ() + this.position2.getZ()) / 2.0D);
  }

  public void broadcast() {
    String capturing;
    KothPlayer topPlayer = getTopPlayer();
    int x = getCenterX();
    int z = getCenterZ();

    if (topPlayer == null) {
      capturing = "\n&eNadie esta capturando el evento de &b&lKoTH &econ premios!";
    } else {
      capturing = "\n&eEstan capturando el evento de &b&lKoTH &econ premios!";
    }

    this.plugin.getServer()
        .broadcastMessage(ChatColor.translateAlternateColorCodes('&', capturing.concat("\n&fMundo: &e" + getWorldName()
            + "&f (X: &6" + x + "&f Z: &6" + z + "&f) Fin: &c" + getTimeLeft() + "&f!\n")));
  }

  public void start() {
    Server server = this.plugin.getServer();

    if (this.position1 != null && this.position2 != null) {
      this.lastStartTime = System.currentTimeMillis();
      this.running = true;
    }

    server.broadcastMessage(
        ChatColor.translateAlternateColorCodes('&', "\n&eEl evento de &b&lKoTH &econ premios ha comenzado!"));
  }

  public void stop(KothPlayer kothPlayer) {
    if (this.running) {
      Server server = this.plugin.getServer();
      Player winner = (kothPlayer != null) ? server.getPlayer(kothPlayer.getUUID()) : null;

      this.running = false;

      for (KothPlayer kothPlayer1 : new HashSet<>(this.kothPlayers)) {
        if (kothPlayer1.getKothEvent() == this) {
          kothPlayer1.setKothEvent(null);
        }
      }

      if (winner == null) {
        server.broadcastMessage(
            ChatColor.translateAlternateColorCodes('&', "\n&eEl evento de &b&lKoTH &econ premios ha finalizado!"

                .concat("\n&cNo hay ganadores del evento!\n")));
      } else {
        Location winnerLocation = winner.getLocation();

        if (this.rewards != null && this.rewards.length > 0) {
          PlayerInventory playerInventory = winner.getInventory();

          server.getScheduler().runTask(this.plugin, () -> {
            World world = winnerLocation.getWorld();

            Firework firework = (Firework) world.spawnEntity(winnerLocation, EntityType.FIREWORK);

            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            fireworkMeta.addEffect(FireworkEffect.builder().trail(true).with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.GREEN).with(FireworkEffect.Type.BALL).withColor(Color.YELLOW).build());

            firework.setFireworkMeta(fireworkMeta);
            for (ItemStack itemStack : this.rewards) {
              if (playerInventory.firstEmpty() != -1) {
                playerInventory.addItem(new ItemStack[] { itemStack });
              } else {
                world.dropItem(winnerLocation, itemStack);
              }
            }
          });
        }
        server.broadcastMessage(
            ChatColor.translateAlternateColorCodes('&', "\n&eEl evento de &b&lKoTH &econ premios ha finalizado!"

                .concat("\n&e&lGanador: &6" + winner.getDisplayName() + "\n")));
      }

      KothScheduleManager kothScheduleManager = KothPlugin.getKothScheduleManager();
      KothSchedule currentSchedule = kothScheduleManager.getCurrent();

      if (currentSchedule != null && currentSchedule.getName().equals(this.name)) {
        KothPlugin.getKothScheduleManager().setCurrent(null);
      }

      KothPlugin.getKothScheduleManager().updateNext();
    }
  }

  public void setMaxTime(int maxTime) {
    this.maxTime = maxTime;
    save();
  }

  public void setCaptureTime(int captureTime) {
    this.captureTime = captureTime;
    save();
  }

  public void setPosition1(Location location1) {
    this.position1 = location1;
    save();
  }

  public void setPosition2(Location location2) {
    this.position2 = location2;
    save();
  }

  public void setRewards(ItemStack[] rewards) {
    this.rewards = rewards;
    save();
  }

  private boolean isBetween(double pos1, double pos2, double pos3) {
    return ((pos1 >= pos2 && pos1 <= pos3) || (pos1 <= pos2 && pos1 >= pos3));
  }

  public boolean isInside(Location location) {
    if (this.position1 != null && this.position2 != null && this.position1.getWorld() == location.getWorld()
        && this.position2.getWorld() == location.getWorld()) {
      return (isBetween(location.getX(), this.position1.getX(), this.position2.getX())
          && isBetween(location.getY(), this.position1.getY(), this.position2.getY())
          && isBetween(location.getZ(), this.position1.getZ(), this.position2.getZ()));
    }

    return false;
  }

  private KothPlayer getTopPlayer() {
    KothPlayer topPlayer = null;
    int topSeconds = 0;

    for (KothPlayer kothPlayer : new HashSet<>(this.kothPlayers)) {
      int secondsInside = kothPlayer.getSecondsInside();

      if (kothPlayer.getKothEvent() == this && secondsInside > topSeconds) {
        topPlayer = kothPlayer;
        topSeconds = secondsInside;
      }
    }

    return topPlayer;
  }

  private void save() {
    YamlConfiguration yamlConfiguration = new YamlConfiguration();

    if (this.position1 != null) {
      yamlConfiguration.set("locations.world", this.position1.getWorld().getName());
      yamlConfiguration.set("locations.vector1", this.position1.toVector());
    }

    if (this.position2 != null)
      yamlConfiguration.set("locations.vector2", this.position2.toVector());
    yamlConfiguration.set("capturetime", Integer.valueOf(this.captureTime));
    yamlConfiguration.set("maxtime", Integer.valueOf(this.maxTime));

    if (this.rewards != null && this.rewards.length > 0) {
      for (int i = 0; i < this.rewards.length; i++) {
        ItemStack itemStack = this.rewards[i];

        yamlConfiguration.set("rewards." + i, itemStack);
      }
    }
    this.configurationUtil.saveConfiguration(yamlConfiguration, "%datafolder%/koths/" + this.name + ".yml");
  }

  public void tick() {
    if (this.running) {
      KothPlayer kothPlayer = getTopPlayer();

      if (kothPlayer != null) {
        if (kothPlayer.getSecondsInside() * 1000 >= this.captureTime) {
          stop(kothPlayer);
        }
      } else if (System.currentTimeMillis() - this.lastStartTime >= this.maxTime) {
        stop(null);
      }
    }
  }

  private int getMaxTime() {
    return this.maxTime;
  }

  private double getLastStartTime() {
    return this.lastStartTime;
  }

  private int getCaptureTime() {
    return this.captureTime;
  }

  public String getTimeLeft() {
    double milliseconds;
    if (this.running) {
      KothPlayer topPlayer = getTopPlayer();

      if (topPlayer == null) {
        milliseconds = getMaxTime() - System.currentTimeMillis() - getLastStartTime();
      } else {
        milliseconds = (getCaptureTime() - topPlayer.getSecondsInside() * 1000);
      }
    } else {
      milliseconds = 0.0D;
    }

    if (milliseconds < 0.0D) {
      return "00:00:00";
    }
    String seconds = String.valueOf((int) (milliseconds / 1000.0D) % 60);
    String minutes = String.valueOf((int) (milliseconds / 60000.0D % 60.0D));
    String hours = String.valueOf((int) (milliseconds / 3600000.0D % 24.0D));

    if (seconds.length() < 2) {
      seconds = "0" + seconds;
    }
    if (minutes.length() < 2) {
      minutes = "0" + minutes;
    }
    if (hours.length() < 2) {
      hours = "0" + hours;
    }
    return hours + ":" + minutes + ":" + seconds;
  }
}