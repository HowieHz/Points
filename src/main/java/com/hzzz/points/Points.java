package com.hzzz.points;

import com.hzzz.points.commands.Death;
import com.hzzz.points.commands.Here;
import com.hzzz.points.commands.PointsCommand;
import com.hzzz.points.commands.Where;
import com.hzzz.points.listeners.DeathListeners;

import static org.bukkit.ChatColor.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public final class Points extends JavaPlugin{
    public static FileConfiguration config;
    public static Logger logger = Logger.getLogger("Points");
    private static Points _instance;
    private final List<String> commands = new ArrayList<>();

    private final List<Listener> eventHandlers = new ArrayList<>();

    @Override
    public void onLoad() {
        saveDefaultConfig();  // 如果配置文件不存在, 保存默认的配置
    }

    @Override
    public void onEnable() {
        _instance = this;

        // 读取配置和注册指令
        config = getConfig();

        logger.info(String.valueOf(config.getBoolean("here.enable", false)));

        // here
        if (config.getBoolean("here.enable", false)) {
            setExecutor("here", Here.getInstance());
        }

        // where
        if (config.getBoolean("here.enable", false)) {
            setExecutor("where", Where.getInstance());
        }

        // death
        if (config.getBoolean("death.enable", false)) {
            setExecutor("death", Death.getInstance());
        }

        // points
        Objects.requireNonNull(Bukkit.getPluginCommand("points")).setExecutor(PointsCommand.getInstance());

        // 注册监听
        registerEvents(DeathListeners.getInstance());

        // 启动消息
        logger.info(BLUE +"<Points>插件启动");
    }

    public void registerEvents(Listener listener) {
        eventHandlers.add(listener);
        Bukkit.getPluginManager().registerEvents(listener,this);
    }

    public void setExecutor(String command, CommandExecutor executor) {
        commands.add(command);
        Objects.requireNonNull(Bukkit.getPluginCommand(command)).setExecutor(executor);
    }

    public void disableExecutor(){
        for (String command : commands){
            Objects.requireNonNull(Bukkit.getPluginCommand(command)).setExecutor(null);
        }
        commands.clear();
    }

    public void disableEventHandler(){
        for (Listener listener : eventHandlers){
            HandlerList.unregisterAll(listener);
        }
        eventHandlers.clear();
    }

    public void onReload(){
        // relaod一遍配置文件，用于重载
        reloadConfig();

        onDisable();
        onEnable();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);  // 关闭插件时, 确保取消我调度的所有任务
        disableExecutor();  // 卸载指令
        disableEventHandler();  // 卸载监听器
        // 消息
        logger.info(BLUE +"<Points>插件关闭");
    }

    public static Points getInstance(){  // 获取实例的方法
        return _instance;
    }
}