package com.hzzz.points.listeners;

import com.hzzz.points.Points;
import com.hzzz.points.listeners.interfaces.NamedListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;

import static com.hzzz.points.commands.commands_utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.utils.Utils.checkPermission;
import static com.hzzz.points.data_manager.operations_utils.DeathMessageConfig.isEnableDeathMessage;
import static com.hzzz.points.data_manager.operations_utils.DeathLog.insertDeathLog;

/**
 * 玩家死亡事件监听器
 */
public final class DeathListener implements NamedListener {
    private static final DeathListener instance = new DeathListener();
    private static final String NAME = "玩家死亡事件";

    /**
     * 获取监听器实例
     *
     * @return 监听器实例
     */
    public static DeathListener getInstance() {
        return instance;
    }

    /**
     * 单例 无参数
     */
    private DeathListener() {
    }

    /**
     * 获取监听器名字
     *
     * @return 监听器名字
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 玩家死亡事件监听(PlayerDeathEvent)
     *
     * @param e 事件
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        Player player = e.getEntity();  // 获取玩家

        // 配置文件检查和权限检查
        if (config.getBoolean("death.message.listener-permission.enable", false)
                && !checkPermission(player, config.getString("death.message.listener-permission.node", "points.listener.death.message"))) {
            return;
        }

        try {
            if (config.getBoolean("death.message.enable", false) && isEnableDeathMessage(player)) {  // 出现错误默认不发送死亡消息
                // 生成并发送消息给执行者
                player.sendMessage(builderPlayerCoordinatesMessage("death.message", player, " X-> ", NamedTextColor.RED));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // 记录死亡日志
        if (config.getBoolean("death.log.enable", false)) {
            Component deathMessage = e.deathMessage();
            if (deathMessage == null) {  // 被手动设置deathMessage才可能为null吧
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(Points.getInstance(), () -> insertDeathLog(player, deathMessage.toString()));
        }
    }
}
