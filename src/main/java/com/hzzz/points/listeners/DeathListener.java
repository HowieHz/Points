package com.hzzz.points.listeners;

import com.hzzz.points.Points;
import com.hzzz.points.listeners.base_listener.HowieUtilsListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.hzzz.points.data_manager.operations_utils.DeathLog.insertDeathLog;
import static com.hzzz.points.data_manager.operations_utils.DeathMessageConfig.isEnableDeathMessage;
import static com.hzzz.points.utils.Utils.runTaskAsynchronously;

/**
 * 玩家死亡事件监听器
 */
public final class DeathListener extends HowieUtilsListener {
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
        Player player = e.getEntity();  // 获取玩家

        // 配置文件检查和权限检查
        if (!checkPermissionOneConfigNode(player, "death.message.listener", "points.listener.death.message")) {
            return;
        }

        if (config.getBoolean("death.message.enable", false)) {  // 出现错误默认不发送死亡消息
            Future<Boolean> future = Bukkit.getScheduler().callSyncMethod(Points.getInstance(), () -> isEnableDeathMessage(player));
            try {
                if (future.get()) {
                    // 生成并发送消息给执行者
                    player.sendMessage(buildPlayerCoordinatesMessage("death.message", player, " X-> ", NamedTextColor.RED));
                }
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        }

        // 记录死亡日志
        if (config.getBoolean("death.log.enable", false)) {
            Component deathMessage = e.deathMessage();
            if (deathMessage == null) {  // 被手动设置deathMessage才可能为null吧
                return;
            }
            runTaskAsynchronously(() -> insertDeathLog(player, deathMessage.toString(), config.getInt("death.log.record-limit", 5)));
        }
    }
}
