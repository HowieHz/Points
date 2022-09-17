package com.hzzz.points.listeners;

import com.hzzz.points.Points;
import com.hzzz.points.text.text;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.hzzz.points.commands.utils.Utils.builder_player_coordinates_message;

public final class DeathListeners implements Listener {
    private final FileConfiguration config;

    public DeathListeners() {
        config = Points.config;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (config.getBoolean("death.message.enable", false)){
            Player player = e.getEntity();  // 获取玩家

            // 权限检查
    //        if (config.getBoolean("where.permission ", false) && !sender.hasPermission("points.where.self")) {
    //            sender.sendMessage(text.no_permission);
    //            return true;
    //        }

            // 生成并发送消息给执行者
            player.sendMessage(builder_player_coordinates_message("death.message", config, player));
    }
    }
}
