package com.hzzz.points.data_manager.operations_utils;

import com.hzzz.points.Points;
import com.hzzz.points.data_manager.sqlite.DeathLogSQLite;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static com.hzzz.points.utils.message.Lang.*;
import static com.hzzz.points.utils.Utils.*;
import static com.hzzz.points.utils.message.MsgKey.*;

/**
 * 有关DeathLog的数据库操作
 */
public final class DeathLog {
    private static final PreparedStatement psDeleteDeathLog = DeathLogSQLite.getInstance().psDeleteDeathLog;
    private static final PreparedStatement psInsertDeathLog = DeathLogSQLite.getInstance().psInsertDeathLog;
    private static final PreparedStatement psSelectDeathLog = DeathLogSQLite.getInstance().psSelectDeathLog;

    /**
     * 工具类禁止实例化
     */
    private DeathLog() {
        throw new IllegalStateException("工具类");
    }

    /**
     * 增加死亡记录的操作
     *
     * @param targetPlayer 目标玩家对象
     * @param deathReason  死亡原因
     */
    public static void insertDeathLog(Player targetPlayer, String deathReason) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        int limit = config.getInt("death.log.record-limit", 5);  // 读取配置
        int count;  // 目前记录条数
        try {
            count = countDeathLog(targetPlayer.getUniqueId());  // 获取目前记录条数
        } catch (SQLException e) {
            logError(getMessage(database_error));
            e.printStackTrace();
            return;
        }

        try {
            logDetailedInfo(String.format(getMessage(read_death_log_result), targetPlayer.getName(), count, limit));
            if (count >= limit) {  // 达到上限了
                // 删除记录 直到记录数为limit-1 现在有count条，所以要删掉count-(limit-1) = count-limit+1
                psDeleteDeathLog.setString(1, targetPlayer.getUniqueId().toString());
                psDeleteDeathLog.setInt(2, count - limit + 1);
                psDeleteDeathLog.execute();
            }

            // 增加新的
            Location playerLocation = targetPlayer.getLocation();  // 获取位置

            psInsertDeathLog.setString(1, targetPlayer.getUniqueId().toString());
            psInsertDeathLog.setString(2, targetPlayer.getName());
            psInsertDeathLog.setString(3, deathReason);
            psInsertDeathLog.setString(4, targetPlayer.getWorld().getName());
            psInsertDeathLog.setDouble(5, playerLocation.getX());
            psInsertDeathLog.setDouble(6, playerLocation.getY());
            psInsertDeathLog.setDouble(7, playerLocation.getZ());
            psInsertDeathLog.execute();
        } catch (SQLException e) {
            logError(String.format(getMessage(insert_death_record_fail), targetPlayer.getName()));  // 未成功录入死亡信息
            e.printStackTrace();
        }
    }

    /**
     * 读取目标玩家的死亡日志
     *
     * @param uuid 目标玩家的uuid
     * @return 读取到的记录集
     */
    public static ResultSet readDeathLog(UUID uuid) throws SQLException {
        // 查询死亡记录的操作
        psSelectDeathLog.setString(1, uuid.toString());
        return psSelectDeathLog.executeQuery();
    }

    /**
     * 计算目标玩家在数据库中的死亡记录数
     *
     * @param uuid 目标玩家的uuid
     * @return 记录数
     */
    public static int countDeathLog(UUID uuid) throws SQLException {
        // 查询死亡记录数量的操作
        int count = 0;  // 结果行数
        try (ResultSet rs = readDeathLog(uuid)) {
            // 计算结果行数
            while (rs.next()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 向接受者发送目标玩家玩家的死亡日志
     *
     * @param uuid     目标玩家的uuid
     * @param receiver 接受者
     */
    public static void outputDeathLog(UUID uuid, CommandSender receiver) {
        Player targetPlayer = Bukkit.getPlayer(uuid);  // 使用uuid获取

        if (targetPlayer == null) {  // 检查是否获取到玩家
            receiver.sendMessage(getMessage(player_not_online));
            return;
        }
        outputDeathLog(targetPlayer, receiver);
    }

    /**
     * 向接受者发送目标玩家玩家的死亡日志
     *
     * @param playerName 目标玩家的用户名
     * @param receiver    接受者
     */
    public static void outputDeathLog(String playerName, CommandSender receiver) {
        Player targetPlayer = Bukkit.getPlayerExact(playerName);  // 使用玩家名获取

        if (targetPlayer == null) {  // 检查是否获取到玩家
            receiver.sendMessage(getMessage(player_not_online));
            return;
        }
        outputDeathLog(targetPlayer, receiver);
    }

    /**
     * 向接受者发送目标玩家玩家的死亡日志
     *
     * @param targetPlayer 目标玩家对象
     * @param receiver      接受者
     */
    public static void outputDeathLog(Player targetPlayer, CommandSender receiver) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        String player_name = targetPlayer.getName();
        UUID uuid = targetPlayer.getUniqueId();

        try (ResultSet rs = readDeathLog(uuid)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int count = 0;
            while (rs.next()) {
                // 编辑消息
                Component component = Component.text("")
                        .append(Component.text(sdf.format(rs.getInt("deathTime") * 1000L)).color(NamedTextColor.YELLOW))  // 取得时间戳单位是秒, SimpleDateFormat需要毫秒, 所以乘1000L
                        .append(Component.text(" -> ").color(NamedTextColor.WHITE))
                        .append(Component.text(rs.getString("world")).color(NamedTextColor.YELLOW))
                        .append(Component.text(String.format(getMessage(coordinates_format), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"))).color(NamedTextColor.YELLOW));

                if (config.getBoolean("death.log.voxelmap-support", false)) {
                    component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                            .hoverEvent(HoverEvent.showText(Component.text(getMessage(voxelmap_support_hover))))
                            .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(voxelmap_support_command), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getString("world")))));
                }

                if (config.getBoolean("death.log.xaeros-support", false)) {
                    component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                            .hoverEvent(HoverEvent.showText(Component.text(getMessage(xaeros_support_hover))))
                            .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(xaeros_support_command), player_name, player_name.charAt(0), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getString("world")))));
                }

                if (config.getBoolean("death.log.teleport-support", false)) {
                    component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                            .append(Component.text("[tp] ").color(NamedTextColor.RED)
                                    .hoverEvent(HoverEvent.showText(Component.text(String.format(getMessage(teleport_support_hover), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")))))
                                    .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(teleport_support_command), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")))));
                }
                count++;
                receiver.sendMessage(component);
            }
            if (count == 0) {  // 没有已经存储的死亡记录
                receiver.sendMessage(String.format(getMessage(no_death_record), player_name));
            } else {
                receiver.sendMessage(String.format(getMessage(read_death_record), count));
            }
        } catch (SQLException e) {
            logInfo(getMessage(database_error));
            receiver.sendMessage(getMessage(database_error));
            e.printStackTrace();
        }
    }
}
