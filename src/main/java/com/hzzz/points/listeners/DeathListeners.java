package com.hzzz.points.listeners;

import com.hzzz.points.Points;
import com.hzzz.points.data_manager.DeathSQLite;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.commands.utils.Utils.checkPermission;

public final class DeathListeners implements Listener {
    private static final DeathListeners INSTANCE = new DeathListeners();

    public static DeathListeners getInstance() {
        return INSTANCE;
    }

    private DeathListeners() {
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        final FileConfiguration config = Points.config;  // 读取配置

        if (config.getBoolean("death.message.enable", false)) {
            Player player = e.getEntity();  // 获取玩家

            // 权限检查
            if (config.getBoolean("where.permission ", false) && !checkPermission(player, "points.death.message")) {
                return;
            }

            if (DeathSQLite.getInstance().IsEnableDeathMessage(player)) {
                // 生成并发送消息给执行者
                player.sendMessage(builderPlayerCoordinatesMessage("death.message", config, player));
            }
        }
    }
}
