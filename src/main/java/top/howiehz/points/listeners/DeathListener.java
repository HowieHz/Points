package top.howiehz.points.listeners;

import top.howiehz.points.listeners.base_listener.HowieUtilsListener;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import top.howiehz.points.data_manager.operations_utils.DeathLog;
import top.howiehz.points.data_manager.operations_utils.DeathMessageConfig;
import top.howiehz.points.utils.Utils;

import java.sql.SQLException;

import static top.howiehz.points.utils.Utils.sendComponentMessage;

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
    public void onPlayerDeath(@NotNull PlayerDeathEvent e) {
        Player player = e.getEntity();  // 获取玩家

        // 配置文件检查和权限检查
        if (!checkPermissionOneConfigNode(player, "death.message.listener", "points.listener.death.message")) {
            return;
        }
        Utils.runTaskAsynchronously(() -> {
            // isEnableDeathMessage insertDeathLog需要异步
            try {
                if (config.getBoolean("death.message.enable", false) && DeathMessageConfig.isEnableDeathMessage(player)) {  // 出现错误默认不发送死亡消息
                    // 生成并发送消息给执行者
                    Utils.sendComponentMessage(player,buildPlayerCoordinatesMessage("death.message", player, " X-> ", NamedTextColor.RED));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // 记录死亡日志
            if (config.getBoolean("death.log.enable", false)) {
                String deathMessage = e.getDeathMessage();
                if (deathMessage == null) {  // 被手动设置deathMessage才可能为null吧
                    return;
                }
                DeathLog.insertDeathLog(player, deathMessage, config.getInt("death.log.record-limit", 5));
            }
        });
    }
}
