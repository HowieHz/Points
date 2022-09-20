package com.hzzz.points.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.commands.utils.Utils.checkPermission;
import static com.hzzz.points.data_manager.operations_set.DeathMessageConfig.IsEnableDeathMessage;
import static com.hzzz.points.Points.config;

public final class DeathListeners implements Listener {
    private static final DeathListeners INSTANCE = new DeathListeners();

    public static DeathListeners getInstance() {
        return INSTANCE;
    }

    private DeathListeners() {
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();  // 获取玩家

        // 配置文件检查和权限检查
        if (config.getBoolean("death.message.permission ", false) && !checkPermission(player, "points.death.message")) {
            return;
        }

        if (IsEnableDeathMessage(player)) {
            // 生成并发送消息给执行者
            player.sendMessage(builderPlayerCoordinatesMessage("death.message", config, player));
        }
    }
}
