package dev._2lstudios.koth;

import java.io.File;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev._2lstudios.koth.commands.KothCommand;
import dev._2lstudios.koth.commands.KothsCommand;
import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.listeners.BlockPlaceListener;
import dev._2lstudios.koth.listeners.PlayerMoveListener;
import dev._2lstudios.koth.listeners.PlayerQuitListener;
import dev._2lstudios.koth.listeners.PlayerJoinListener;
import dev._2lstudios.koth.listeners.PlayerTeleportListener;
import dev._2lstudios.koth.placeholders.KothPlaceholders;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import dev._2lstudios.koth.tasks.KothSecondTask;
import dev._2lstudios.koth.utils.ConfigurationUtil;

public class KothPlugin extends JavaPlugin {
    private static KothPlugin instance = null;

    private KothScheduleManager kothScheduleManager = null;
    private KothPlaceholders kothPlaceholders = null;

    public static KothPlugin getInstance() {
        return instance;
    }

    public KothScheduleManager getKothScheduleManager() {
        return kothScheduleManager;
    }

    public void onEnable() {
        instance = this;

        ConfigurationUtil configurationUtil = new ConfigurationUtil((Plugin) this);
        Server server = getServer();
        PluginManager pluginManager = server.getPluginManager();
        String dataFolderPath = getDataFolder().getPath();
        File kothsFolder = new File(dataFolderPath + "/koths/");

        kothsFolder.mkdirs();
        kothScheduleManager = new KothScheduleManager(dataFolderPath);
        KothManager kothManager = new KothManager((Plugin) this, configurationUtil, kothScheduleManager);

        kothScheduleManager.load();

        getCommand("koth").setExecutor(new KothCommand(kothManager, kothScheduleManager));
        getCommand("koths").setExecutor(new KothsCommand(kothManager, kothScheduleManager));

        pluginManager.registerEvents(new BlockPlaceListener(kothManager), this);
        pluginManager.registerEvents(new PlayerMoveListener(kothManager), this);
        pluginManager.registerEvents(new PlayerJoinListener(kothManager), this);
        pluginManager.registerEvents(new PlayerQuitListener(kothManager), this);
        pluginManager.registerEvents(new PlayerTeleportListener(kothManager), this);

        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            this.kothPlaceholders = new KothPlaceholders(this, kothManager, kothScheduleManager);
            this.kothPlaceholders.register();
        }

        for (final Player player : getServer().getOnlinePlayers()) {
            kothManager.addPlayer(player);
        }

        server.getScheduler().runTaskTimerAsynchronously(this,
                new KothSecondTask(server, kothScheduleManager, kothManager), 20L, 20L);
    }

    public void onDisable() {
        kothScheduleManager.save();
    }
}