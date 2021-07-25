package dev._2lstudios.koth.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigurationUtil {
  private final Plugin plugin;

  public ConfigurationUtil(Plugin plugin) {
    this.plugin = plugin;
  }

  public YamlConfiguration getConfiguration(String filePath) {
    File dataFolder = this.plugin.getDataFolder();
    File file = new File(filePath.replace("%datafolder%", dataFolder.toPath().toString()));

    if (file.exists())
      return YamlConfiguration.loadConfiguration(file);
    return new YamlConfiguration();
  }

  public void createConfiguration(String file) {
    try {
      File dataFolder = this.plugin.getDataFolder();

      file = file.replace("%datafolder%", dataFolder.toPath().toString());

      File configFile = new File(file);

      if (!configFile.exists()) {
        String[] files = file.split("/");
        InputStream inputStream = this.plugin.getClass().getClassLoader().getResourceAsStream(files[files.length - 1]);
        File parentFile = configFile.getParentFile();

        if (parentFile != null)
          parentFile.mkdirs();

        if (inputStream != null) {
          Files.copy(inputStream, configFile.toPath(), new java.nio.file.CopyOption[0]);
          System.out.print(("[%pluginname%] File " + configFile + " has been created!").replace("%pluginname%",
              this.plugin.getDescription().getName()));
        } else {
          configFile.createNewFile();
        }
      }
    } catch (IOException e) {
      System.out.print("[%pluginname%] Unable to create configuration file!".replace("%pluginname%",
          this.plugin.getDescription().getName()));
    }
  }

  public void saveConfiguration(YamlConfiguration yamlConfiguration, String file) {
    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
      try {
        yamlConfiguration.save(file.replace("%datafolder%", this.plugin.getDataFolder().toPath().toString()));
      } catch (IOException e) {
        System.out.print("[%pluginname%] Unable to save configuration file!".replace("%pluginname%",
            this.plugin.getDescription().getName()));
      }
    });
  }

  public void deleteConfiguration(String file) {
    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
      File file1 = new File(file.replace("%datafolder%", this.plugin.getDataFolder().toPath().toString()));
      if (file1.exists()) {
        file1.delete();
      }
    });
  }

  public void saveConfigurationSync(YamlConfiguration yamlConfiguration, String file) {
    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
      try {
        File dataFolder = this.plugin.getDataFolder();

        yamlConfiguration.save(file.replace("%datafolder%", dataFolder.toPath().toString()));
      } catch (IOException e) {
        System.out.print("[%pluginname%] Unable to save configuration file!".replace("%pluginname%",
            this.plugin.getDescription().getName()));
      }
    });
  }
}