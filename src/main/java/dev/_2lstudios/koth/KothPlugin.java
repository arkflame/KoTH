package dev._2lstudios.koth;

import dev._2lstudios.koth.commands.KothCommand;
import dev._2lstudios.koth.commands.KothsCommand;
import dev._2lstudios.koth.koth.KothManager;
import dev._2lstudios.koth.listeners.BlockPlaceListener;
import dev._2lstudios.koth.listeners.PlayerMoveListener;
import dev._2lstudios.koth.listeners.PlayerQuitListener;
import dev._2lstudios.koth.listeners.PlayerTeleportListener;
import dev._2lstudios.koth.placeholders.KothPlaceholders;
import dev._2lstudios.koth.schedule.KothScheduleManager;
import dev._2lstudios.koth.tasks.KothSecondTask;
import dev._2lstudios.koth.utils.ConfigurationUtil;
import java.io.File;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

        getCommand("koth").setExecutor((CommandExecutor) new KothCommand(kothManager, kothScheduleManager));
        getCommand("koths").setExecutor((CommandExecutor) new KothsCommand(kothManager, kothScheduleManager));

        pluginManager.registerEvents((Listener) new BlockPlaceListener(kothManager), (Plugin) this);
        pluginManager.registerEvents((Listener) new PlayerMoveListener(kothManager), (Plugin) this);
        pluginManager.registerEvents((Listener) new PlayerQuitListener(kothManager), (Plugin) this);
        pluginManager.registerEvents((Listener) new PlayerTeleportListener(kothManager), (Plugin) this);

        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            this.kothPlaceholders = new KothPlaceholders((Plugin) this, kothManager, kothScheduleManager);
            this.kothPlaceholders.register();
        }

        server.getScheduler().runTaskTimerAsynchronously((Plugin) this,
                (Runnable) new KothSecondTask(server, kothScheduleManager, kothManager), 20L, 20L);
    }

    public void onDisable() {
        kothScheduleManager.save();
    }
}