package com.hzzz.points.listeners;

import com.hzzz.points.Points;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;

public final class DeathListeners implements Listener {
    private static DeathListeners instance;

    public static DeathListeners getInstance() {
        if (instance == null) {
            instance = new DeathListeners();
        }
        return instance;
    }

    private DeathListeners() {}

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        final FileConfiguration config = Points.config;  // 读取配置

        if (config.getBoolean("death.message.enable", false)){
            Player player = e.getEntity();  // 获取玩家

            // 权限检查 玩家是否开启检查
    //        if (config.getBoolean("where.permission ", false) && !sender.hasPermission("points.where.self")) {
    //            sender.sendMessage(text.no_permission);
    //            return true;
    //        }

            // 生成并发送消息给执行者
            player.sendMessage(builderPlayerCoordinatesMessage("death.message", config, player));
    }
    }
}
