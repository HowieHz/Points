package top.howiehz.points;

import top.howiehz.points.commands.*;
import top.howiehz.points.data_manager.sqlite.ConfigSQLite;
import top.howiehz.points.data_manager.sqlite.DeathLogSQLite;
import top.howiehz.points.listeners.AntiBoomListener;
import top.howiehz.points.listeners.DeathListener;
import top.howiehz.points.listeners.base_listener.NamedListener;
import top.howiehz.points.utils.base_utils_class.BaseUtilsClass;
import top.howiehz.points.utils.data_structure.CommandInfo;
import top.howiehz.points.utils.data_structure.tuple.Tuple4;
import top.howiehz.points.utils.github_update_checker.UpdateChecker;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static top.howiehz.points.utils.Utils.*;
import static top.howiehz.points.utils.message.Lang.getMessage;
import static top.howiehz.points.utils.message.Lang.reloadLangConfig;
import static top.howiehz.points.utils.message.MsgKey.*;

/**
 * <p>插件主类</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @since 2022-09-12 12:31
 */
public final class Points extends JavaPlugin {
    private static Points instance;
    public static final Logger pluginLogger = Logger.getLogger("Points");  // Points.pluginLogger
    private final List<String> commands = new ArrayList<>();  // 已注册的指令
    private final List<NamedListener> eventHandlers = new ArrayList<>();  // 已注册的监听器
    private static BukkitAudiences adventure;  // 好用的adventure

    /**
     * 设置instance，方便获取实例
     */
    public Points() {
        super();
        instance = this;
    }

    /**
     * 获取插件实例
     *
     * @return Points实例
     */
    public static Points getInstance() {
        return instance;
    }

    /**
     * 获取adventure实例
     * <a href="https://docs.adventure.kyori.net/platform/bukkit.html">...</a>
     *
     * @return Adventure实例
     */
    public static BukkitAudiences getAdventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    /**
     * 第一次加载要做的事情：<br>初始化配置文件并读取，<br>初始化文件夹，<br>加载bStats，<br>加载语言文件
     */
    @Override
    public void onLoad() {
        // 如果配置文件不存在, 保存默认的配置
        // config.yml
        saveDefaultConfig();

        // 保存语言文件 加载文字 要在加载配置文件之后，因为要读取配置文件中language.file_name项
        saveLangConfig();
        reloadLangConfig();

        // 插件正在加载
        logInfo(getMessage(PLUGIN_LOADING));

        // 初始化数据库存放的文件夹
        String PATH_TO_SAVE_DATABASE = "./plugins/Points/database";
        File file = new File(PATH_TO_SAVE_DATABASE);
        //文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            if (file.mkdirs()) {
                logDetailedInfo(getMessage(CREATE_DATABASE_FOLDER_SUCCESSFULLY));
            } else {
                logError(getMessage(CREATE_DATABASE_FOLDER_FAILED));
            }
        }

        // 开启bstats
        if (getConfig().getBoolean("bStats.enable", true)) {  // 默认开启
            int pluginId = 16544;
            new Metrics(this, pluginId);
        }

        logInfo(getMessage(PLUGIN_LOADED));  // 插件已加载
    }

    /**
     * 启用(重新加载插件)的时候要做的事情：<br>指令和监听器的注册
     */
    @Override
    public void onEnable() {
        logInfo(getMessage(PLUGIN_STARTING));  // 插件正在启动

        // Initialize an audiences instance for the plugin
        adventure = BukkitAudiences.create(this);

        // 读取配置 供初始化使用
        FileConfiguration config = getConfig();

        // 检查更新
        runTaskAsynchronously(this::updateChecker);

        final CommandInfo[] commandInfos = {  // 指令 要注册的执行器 判断是否开启的配置文件节点(为null就是直接开启) 其他的也需要满足的判断
                new CommandInfo("here", Here::getInstance, "here.enable", () -> true),  // here指令
                new CommandInfo("where", Where::getInstance, "where.enable", () -> true),  // where指令
                new CommandInfo("points", PointsCommand::getInstance, null, () -> true),  // points指令
                new CommandInfo("enderchest", Enderchest::getInstance, "enderchest.enable", () -> true),  // enderchest指令
                new CommandInfo("fair-pvp", FairPVP::getInstance, "fair-pvp.enable",
                        () -> (isLoadDepend("PlaceholderAPI") && isLoadDepend("AureliumSkills"))),  // fair-pvp指令
        };

        // 注册指令
        for (CommandInfo info : commandInfos) {
            if ((info.enabling == null || config.getBoolean(info.enabling, false)) && info.and.get()) {
                setExecutor(info.command, info.executor.get());
            }
        }

        // death模块 监听器注册 指令注册
        if (config.getBoolean("death.enable", false)) {
            // 数据库检查 启动数据库
            if (ConfigSQLite.getInstance().isReady() && DeathLogSQLite.getInstance().isReady()) {
                logDetailedInfo(String.format(getMessage(SQLITE_READY), "config.sqlite, death_log.sqlite"));

                // 数据库成功启动才启动death模块
                // 注册监听
                registerEvents(DeathListener.getInstance());
                // 注册指令
                setExecutor("death", Death.getInstance());
            } else {
                logError(String.format(getMessage(SQLITE_NOT_READY), "config.sqlite, death_log.sqlite"));
            }
        }

        // anti-boom模块 监听器注册
        if (config.getBoolean("anti-boom.enable", false)) {
            // 注册监听
            registerEvents(AntiBoomListener.getInstance());
        }

        logInfo(getMessage(PLUGIN_STARTED));  // 插件已启动
    }

    @Override
    public void onDisable() {
        logInfo(getMessage(PLUGIN_DISABLING));  // 插件正在关闭

        Bukkit.getScheduler().cancelTasks(this);  // 关闭插件时, 确保取消我调度的所有任务
        disableExecutor();  // 卸载指令
        disableEventHandler();  // 卸载监听器

        if (adventure != null) {
            adventure.close();
            adventure = null;
        }

        logInfo(getMessage(PLUGIN_DISABLED));  // 插件已关闭
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
        logDetailedInfo(String.format(getMessage(REGISTER_LISTENERS), listenerInstance.getName()));  // 详细log
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
        logDetailedInfo(String.format(getMessage(SET_EXECUTOR), command));  // 详细log
    }

    /**
     * 注销{@link #registerEvents}注册的监听器
     */
    private void disableEventHandler() {
        for (NamedListener listener : eventHandlers) {
            HandlerList.unregisterAll(listener);
            logDetailedInfo(String.format(getMessage(ALREADY_DISABLE_LISTENERS), listener.getName()));  // 详细log
        }
        eventHandlers.clear();
        logDetailedInfo(getMessage(ALL_LISTENERS_DISABLED));  // 详细log
    }

    /**
     * 注销{@link #setExecutor}注册的指令执行器
     */
    private void disableExecutor() {
        for (String command : commands) {
            Objects.requireNonNull(Bukkit.getPluginCommand(command)).setExecutor(null);
            logDetailedInfo(String.format(getMessage(ALREADY_DISABLE_EXECUTOR), command));  // 详细log
        }
        commands.clear();
        logDetailedInfo(getMessage(ALL_EXECUTOR_DISABLED));  // 详细log
    }

    /**
     * 检查插件是否加载 用于检查依赖
     *
     * @param pluginName 被检查的插件名
     * @return 是否加载
     */
    private boolean isLoadDepend(String pluginName) {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            logInfo(getMessage(LOADED_DEPEND) + pluginName);
            return true;
        } else {
            logError(getMessage(NO_DEPEND) + pluginName);
            return false;
        }
    }

    /**
     * 重启插件(重载配置文件)
     */
    public void onReload() {
        onDisable();
        // 可能需要重写，实例化的类没有摧毁。重新执行一遍onEnable是为了配置文件中可能出现关掉子模块的修改

        // reload一遍配置文件，用于重载 这个和onDisable谁先都一样
        reloadConfig();
        BaseUtilsClass.reloadConfig();
        // 读取配置，加载文字
        reloadLangConfig();

        logInfo(getMessage(CONFIG_RELOADED));  // 详细log

        onEnable();
    }


    /**
     * 初始化和读取语言配置文件
     */
    private void saveLangConfig() {
        final String fileName = String.format("lang/%s.yml", getConfig().getString("language.file_name", "zh_cn"));
        File langConfigFile = new File(getDataFolder(), fileName);
        // 配置文件不存在就初始化文件夹和配置文件
        if (!langConfigFile.exists()) {
            langConfigFile.getParentFile().mkdirs();
            saveResource(fileName, false);
        }
    }

    /**
     * 检查插件更新
     */
    private void updateChecker() {
        // TODO 向游戏内玩家发送（加上对应权限）
        // TODO 定期检查更新（配置文件也要加上）
        // TODO 向哪里检查更新，对应源url
        // TODO 比如请求头，更多地方要开放配置文件，还有手动检查更新的指令
        logInfo(getMessage(UPDATE_CHECKER_START));
        String currentVersion = this.getDescription().getVersion();
        Tuple4<Boolean, Boolean, String, String> result = UpdateChecker.check(currentVersion);
        if (result._1) {
            if (result._2) {
                // 需要更新
                logInfo(getMessage(UPDATE_CHECKER_NEED_UPDATE)
                        .replace("[current_version]", currentVersion)
                        .replace("[latest_version]", result._3)
                        .replace("[html_url]", result._4));
            } else {
                // 是最新版
                logInfo(getMessage(UPDATE_CHECKER_IS_LATEST)
                        .replace("[current_version]", currentVersion));
            }
        } else {
            // 信息获取失败
            logInfo(getMessage(UPDATE_CHECKER_FAIL));
        }
    }
}