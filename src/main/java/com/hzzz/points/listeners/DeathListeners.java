package com.hzzz.points.listeners;

import com.hzzz.points.interfaces.NamedListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.text.text.insert_death_record_fail;
import static com.hzzz.points.utils.Utils.checkPermission;
import static com.hzzz.points.data_manager.operations_set.DeathMessageConfig.IsEnableDeathMessage;
import static com.hzzz.points.data_manager.operations_set.DeathLog.insertDeathLog;
import static com.hzzz.points.Points.config;
import static com.hzzz.points.utils.Utils.logDetailInfo;

public final class DeathListeners implements NamedListener {
    private static final DeathListeners INSTANCE = new DeathListeners();
    private static final String name = "死亡消息";

    public static DeathListeners getInstance() {
        return INSTANCE;
    }

    private DeathListeners() {
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();  // 获取玩家

        // 配置文件检查和权限检查
        if (config.getBoolean("death.message.listener-permission.enable", false)
                && !checkPermission(player, config.getString("death.message.listener-permission.node", "points.listener.death.message"))) {
            return;
        }

        if (IsEnableDeathMessage(player)) {
            // 生成并发送消息给执行者
            player.sendMessage(builderPlayerCoordinatesMessage("death.message", config, player, " X-> ", NamedTextColor.RED));
        }

        // 记录死亡日志
        if (config.getBoolean("death.log.enable", false)) {
            Component deathMessage = e.deathMessage();
            if (deathMessage == null){  // 被手动设置deathMessage才可能为null吧
                return;
            }
            if (!insertDeathLog(player, deathMessage.toString())){
                logDetailInfo(String.format(insert_death_record_fail, player.getName()));  // 详细log 未成功录入死亡信息
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
