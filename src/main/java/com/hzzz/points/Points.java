package com.hzzz.points;

import com.hzzz.points.commands.Death;
import com.hzzz.points.commands.Here;
import com.hzzz.points.commands.PointsCommand;
import com.hzzz.points.commands.Where;
import com.hzzz.points.data_manager.sqlite.ConfigSQLite;
import com.hzzz.points.data_manager.sqlite.DeathLogSQLite;
import com.hzzz.points.data_structure.CommandInfo;
import com.hzzz.points.interfaces.NamedListener;
import com.hzzz.points.listeners.AntiBoomListener;
import com.hzzz.points.listeners.DeathListener;
import com.hzzz.points.text.text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.hzzz.points.utils.Utils.logDetailInfo;
import static com.hzzz.points.text.text.*;

public final class Points extends JavaPlugin {
    public static FileConfiguration config;  // Points.config
    public static final Logger logger = Logger.getLogger("Points");  // Points.logger
    private static Points INSTANCE;
    private final List<String> commands = new ArrayList<>();  // 已注册的指令

    private final List<NamedListener> event_handlers = new ArrayList<>();  // 已注册的监听器

    public static Points getInstance() {  // 获取实例的方法
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        logger.info(plugin_loading);  // 插件正在加载

        saveDefaultConfig();  // 如果配置文件不存在, 保存默认的配置

        logger.info(plugin_loaded);  // 插件已加载
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        logger.info(plugin_starting);  // 插件正在启动

        // 读取配置
        config = getConfig();

        // 注册指令
        CommandInfo[] command_info = {  // 指令 要注册的执行器 判断是否开启的配置文件节点(为null就是直接开启) 其他的也需要满足的判断
                new CommandInfo("here", Here.getInstance(), "here.enable", true),  // here指令
                new CommandInfo("where", Where.getInstance(), "where.enable", true),  // where指令
                new CommandInfo("points", PointsCommand.getInstance(), null, true),  // points指令
                new CommandInfo("death", Death.getInstance(), "death.enable",
                        DeathLogSQLite.getInstance().state() && ConfigSQLite.getInstance().state())  // death指令
        };

        for (CommandInfo info : command_info) {
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
            if (ConfigSQLite.getInstance().state() && DeathLogSQLite.getInstance().state()) {
                logger.info(String.format(text.sqlite_ready, "config.sqlite, death_log.sqlite"));

                // 数据库成功启动才启动death模块
                // 注册监听
                registerEvents(DeathListener.getInstance());
            } else {
                logger.info(String.format(text.sqlite_not_ready, "config.sqlite, death_log.sqlite"));
            }
        }

        // anti-boom模块 监听器注册
        if (config.getBoolean("anti-boom.enable", false)) {
            // 注册监听
            registerEvents(AntiBoomListener.getInstance());
        }

        logger.info(plugin_started);  // 插件已启动
    }

    @Override
    public void onDisable() {
        logger.info(plugin_disabling);  // 插件正在关闭

        Bukkit.getScheduler().cancelTasks(this);  // 关闭插件时, 确保取消我调度的所有任务
        disableExecutor();  // 卸载指令
        disableEventHandler();  // 卸载监听器

        logger.info(plugin_disabled);  // 插件已关闭
    }

    public void registerEvents(NamedListener listener) {  // 注册监听器
        event_handlers.add(listener);
        Bukkit.getPluginManager().registerEvents(listener, this);
        logDetailInfo(String.format(register_event, listener.getName()));  // 详细log
    }

    public void setExecutor(String command, CommandExecutor executor) {  // 注册指令执行器
        commands.add(command);
        Objects.requireNonNull(Bukkit.getPluginCommand(command)).setExecutor(executor);
        logDetailInfo(String.format(set_executor, command));  // 详细log
    }

    public void disableEventHandler() {  // 注销监听器
        for (NamedListener listener : event_handlers) {
            HandlerList.unregisterAll(listener);
            logDetailInfo(String.format(already_disable_event, listener.getName()));  // 详细log
        }
        event_handlers.clear();
        logDetailInfo(all_event_disabled);  // 详细log
    }

    public void disableExecutor() {  // 注销指令执行器
        for (String command : commands) {
            Objects.requireNonNull(Bukkit.getPluginCommand(command)).setExecutor(null);
            logDetailInfo(String.format(already_disable_executor, command));  // 详细log
        }
        commands.clear();
        logDetailInfo(all_executor_disabled);  // 详细log
    }

    public void onReload() {
        onDisable();

        // reload一遍配置文件，用于重载 这个和onDisable谁先都一样
        reloadConfig();
        logDetailInfo(config_reloaded);  // 详细log

        onEnable();
    }
}