package com.hzzz.points;

import com.hzzz.points.commands.*;
import com.hzzz.points.data_manager.sqlite.ConfigSQLite;
import com.hzzz.points.data_manager.sqlite.DeathLogSQLite;
import com.hzzz.points.listeners.AntiBoomListener;
import com.hzzz.points.listeners.DeathListener;
import com.hzzz.points.listeners.interfaces.NamedListener;
import com.hzzz.points.utils.data_structure.CommandInfo;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.hzzz.points.utils.Text.*;
import static com.hzzz.points.utils.Utils.*;

/**
 * <p>插件主类</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @since 2022-09-12 12:31
 */
public final class Points extends JavaPlugin {
    private File langConfigFile;  // 语言配置文件
    private FileConfiguration langConfig = null;  // 语言配置文件
    private static Points instance;
    private final List<String> commands = new ArrayList<>();  // 已注册的指令
    private final List<NamedListener> eventHandlers = new ArrayList<>();  // 已注册的监听器
    private final CommandInfo[] commandInfos = {  // 指令 要注册的执行器 判断是否开启的配置文件节点(为null就是直接开启) 其他的也需要满足的判断
            new CommandInfo("here", Here.getInstance(), "here.enable", true),  // here指令
            new CommandInfo("where", Where.getInstance(), "where.enable", true),  // where指令
            new CommandInfo("points", PointsCommand.getInstance(), null, true),  // points指令
            new CommandInfo("death", Death.getInstance(), "death.enable",
                    DeathLogSQLite.getInstance().isReady() && ConfigSQLite.getInstance().isReady()),  // death指令
            new CommandInfo("enderchest", Enderchest.getInstance(), "enderchest.enable", true),  // enderchest指令
    };
    public static final Logger pluginLogger = Logger.getLogger("Points");  // Points.pluginLogger

    /**
     * 获取插件实例
     *
     * @return Points实例
     */
    public static Points getInstance() {
        return instance;
    }

    /**
     * 第一次加载要做的事情：<br>初始化配置文件并读取，<br>初始化文件夹，<br>加载bStats，<br>加载语言文件
     */
    @Override
    public void onLoad() {
        instance = this;

        // 如果配置文件不存在, 保存默认的配置
        // config.yml
        saveDefaultConfig();

        // 保存语言文件 加载文字 要在加载配置文件之后，因为要读取配置文件中language.file_name项
        saveLangConfig();

        // 插件正在加载 这个要在读取加载语言文件之后，不然输出的就是null
        logInfo(getMessage("plugin_loading"));

        // 初始化数据库存放的文件夹
        File file = new File("./plugins/Points/database");
        //文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            if (file.mkdirs()) {
                logDetailedInfo(getCreateDatabaseFolderSuccessfully());
            } else {
                logError(getCreateDatabaseFolderFailed());
            }
        }

        // 开启bstats
        if (getConfig().getBoolean("bStats.enable", true)) {  // 默认开启
            int pluginId = 16544;
            new Metrics(this, pluginId);
        }

        logInfo(getPluginLoaded());  // 插件已加载
    }

    /**
     * 启用(重新加载插件)的时候要做的事情：<br>指令和监听器的注册
     */
    @Override
    public void onEnable() {
        logInfo(getMessage("plugin_starting"));  // 插件正在启动

        // 读取配置 供初始化使用
        FileConfiguration config = getConfig();

        // 注册指令
        for (CommandInfo info : commandInfos) {
            if (info.and) {
                if (info.enabling == null) {
                    setExecutor(info.command, info.executor);
                } else {
                    if (config.getBoolean(info.enabling, false)) {
                        setExecutor(info.command, info.executor);
                    }
                }
            }
        }

        // death模块 监听器注册
        if (config.getBoolean("death.enable", false)) {
            // 数据库检查 启动数据库
            if (ConfigSQLite.getInstance().isReady() && DeathLogSQLite.getInstance().isReady()) {
                logDetailedInfo(String.format(getSqliteReady(), "config.sqlite, death_log.sqlite"));

                // 数据库成功启动才启动death模块
                // 注册监听
                registerEvents(DeathListener.getInstance());
            } else {
                logError(String.format(getSqliteNotReady(), "config.sqlite, death_log.sqlite"));
            }
        }

        // anti-boom模块 监听器注册
        if (config.getBoolean("anti-boom.enable", false)) {
            // 注册监听
            registerEvents(AntiBoomListener.getInstance());
        }

        logInfo(getPluginStarted());  // 插件已启动
    }

    @Override
    public void onDisable() {
        logInfo(getPluginDisabling());  // 插件正在关闭

        Bukkit.getScheduler().cancelTasks(this);  // 关闭插件时, 确保取消我调度的所有任务
        disableExecutor();  // 卸载指令
        disableEventHandler();  // 卸载监听器

        logInfo(getPluginDisabled());  // 插件已关闭
    }

    /**
     * 注册监听器<br>
     * 替代Bukkit.getPluginManager().registerEvents(listenerInstance, this)<br>
     *
     * @param listenerInstance 需要注册的监听器的实例
     */
    private void registerEvents(NamedListener listenerInstance) {
        eventHandlers.add(listenerInstance);
        Bukkit.getPluginManager().registerEvents(listenerInstance, this);
        logDetailedInfo(String.format(getRegisterListeners(), listenerInstance.getName()));  // 详细log
    }

    /**
     * 注册指令执行器(以及tab补全)<br>
     * 替代需要Bukkit.getPluginManager().registerEvents(listenerInstance, this)<br>
     *
     * @param command          根指令
     * @param executorInstance 执行器实例
     */
    private void setExecutor(String command, CommandExecutor executorInstance) {
        commands.add(command);
        Objects.requireNonNull(Bukkit.getPluginCommand(command)).setExecutor(executorInstance);
        logDetailedInfo(String.format(getSetExecutor(), command));  // 详细log
    }

    /**
     * 注销{@link #registerEvents}注册的监听器
     */
    private void disableEventHandler() {
        for (NamedListener listener : eventHandlers) {
            HandlerList.unregisterAll(listener);
            logDetailedInfo(String.format(getAlreadyDisableListeners(), listener.getName()));  // 详细log
        }
        eventHandlers.clear();
        logDetailedInfo(getAllListenersDisabled());  // 详细log
    }

    /**
     * 注销{@link #setExecutor}注册的指令执行器
     */
    private void disableExecutor() {
        for (String command : commands) {
            Objects.requireNonNull(Bukkit.getPluginCommand(command)).setExecutor(null);
            logDetailedInfo(String.format(getAlreadyDisableExecutor(), command));  // 详细log
        }
        commands.clear();
        logDetailedInfo(getAllExecutorDisabled());  // 详细log
    }

    /**
     * 重启插件(重载配置文件)
     */
    public void onReload() {
        onDisable();

        // reload一遍配置文件，用于重载 这个和onDisable谁先都一样
        reloadConfig();
        // 读取配置，加载文字
        reloadLangConfig();

        logDetailedInfo(getConfigReloaded());  // 详细log

        onEnable();
    }

    /**
     * 获取语言配置文件
     *
     * @return 语言配置文件实例
     */
    public FileConfiguration getLangConfig() {
        if (this.langConfig == null) {
            reloadLangConfig();
        }
        return this.langConfig;
    }

    /**
     * 初始化和读取语言配置文件
     */
    private void saveLangConfig() {
        final String fileName = String.format("lang/%s.yml", getConfig().getString("language.file_name", "zh_cn"));
        langConfigFile = new File(getDataFolder(), fileName);
        // 配置文件不存在就初始化文件夹和配置文件
        if (!langConfigFile.exists()) {
            langConfigFile.getParentFile().mkdirs();
            saveResource(fileName, false);
        }

        // 读取配置文件，加载文字
        reloadLangConfig();
    }

    /**
     * 读取语言配置文件，加载文字
     */
    private void reloadLangConfig() {
        langConfigFile = new File(getDataFolder(), String.format("lang/%s.yml", getConfig().getString("language.file_name", "zh_cn")));
        // 读取配置文件
        langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
        // 加载文字
        loadText();
    }
}