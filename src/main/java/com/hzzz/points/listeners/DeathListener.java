package com.hzzz.points.listeners;

import com.hzzz.points.interfaces.NamedListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.utils.Utils.checkPermission;
import static com.hzzz.points.data_manager.operations_set.DeathMessageConfig.IsEnableDeathMessage;
import static com.hzzz.points.data_manager.operations_set.DeathLog.insertDeathLog;
import static com.hzzz.points.Points.config;

/**
 * 玩家死亡事件监听器
 */
public final class DeathListener implements NamedListener { // TODO NamedListener改继承
    private static final DeathListener INSTANCE = new DeathListener();
    private static final String name = "玩家死亡事件";

    /**
     * 获取监听器实例
     *
     * @return 监听器实例
     */
    public static DeathListener getInstance() {
        return INSTANCE;
    }


    private DeathListener() {
    }

    /**
     * 获取监听器名字
     *
     * @return 监听器名字
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 玩家死亡事件监听(PlayerDeathEvent)
     *
     * @param e 事件
     */
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();  // 获取玩家

        // 配置文件检查和权限检查
        if (config.getBoolean("death.message.listener-permission.enable", false)
                && !checkPermission(player, config.getString("death.message.listener-permission.node", "points.listener.death.message"))) {
            return;
        }

        if (config.getBoolean("death.message.enable", false) && IsEnableDeathMessage(player)) {
            // 生成并发送消息给执行者
            player.sendMessage(builderPlayerCoordinatesMessage("death.message", config, player, " X-> ", NamedTextColor.RED));
        }

        // 记录死亡日志
        if (config.getBoolean("death.log.enable", false)) {
            Component deathMessage = e.deathMessage();
            if (deathMessage == null) {  // 被手动设置deathMessage才可能为null吧
                return;
            }
            insertDeathLog(player, deathMessage.toString());
        }
    }
}
