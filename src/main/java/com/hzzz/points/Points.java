package com.hzzz.points;

import com.hzzz.points.commands.Death;
import com.hzzz.points.commands.Here;
import com.hzzz.points.commands.PointsCommand;
import com.hzzz.points.commands.Where;
import com.hzzz.points.listeners.DeathListeners;

import static org.bukkit.ChatColor.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Points extends JavaPlugin{
    public static FileConfiguration config;
    private static Points _instance;

    @Override
    public void onLoad() {
        saveDefaultConfig();  // 如果配置文件不存在, 保存默认的配置
    }

    @Override
    public void onEnable() {
        _instance = this;

        // 读取配置和注册指令
        config = getConfig();

        // here
        if (config.getBoolean("here.enable", false)) {
            Objects.requireNonNull(Bukkit.getPluginCommand("here")).setExecutor(new Here());
        }

        // where
        if (config.getBoolean("here.enable", false)) {
            Objects.requireNonNull(Bukkit.getPluginCommand("where")).setExecutor(new Where());
        }

        // death
        if (config.getBoolean("death.enable", false)) {
            Objects.requireNonNull(Bukkit.getPluginCommand("death")).setExecutor(new Death());
        }

        // points
        Objects.requireNonNull(Bukkit.getPluginCommand("points")).setExecutor(new PointsCommand());

        // 注册监听
        Bukkit.getPluginManager().registerEvents(new DeathListeners(),this);

        // 启动消息
        this.getLogger().info(BLUE +"<Points>插件启动");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);  // 关闭插件时, 确保取消我调度的所有任务
        saveConfig();// 保存配置

        // 消息
        this.getLogger().info(BLUE +"<Points>插件关闭");
    }

    public static Points getInstance(){  // 获取实例的方法
        return _instance;
    }
}