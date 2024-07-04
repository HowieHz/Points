package top.howiehz.points.data_manager.operations_utils;

import top.howiehz.points.data_manager.sqlite.DeathLogSQLite;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static top.howiehz.points.utils.Utils.*;
import static top.howiehz.points.utils.message.Lang.getMessage;
import static top.howiehz.points.utils.message.MsgKey.*;

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
     * 读取目标玩家的死亡日志
     *
     * @param uuid 目标玩家的uuid
     * @return 读取到的记录集
     */
    private static ResultSet readDeathLog(@NotNull UUID uuid) throws SQLException {
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
    private static int countDeathLog(UUID uuid) throws SQLException {
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
     * 增加死亡记录的操作
     *
     * @param targetPlayer 目标玩家对象
     * @param deathReason  死亡原因
     * @param recordLimit  死亡记录上限
     */
    public static void insertDeathLog(@NotNull Player targetPlayer, String deathReason, int recordLimit) {
        int count;  // 目前记录条数
        try {
            count = countDeathLog(targetPlayer.getUniqueId());  // 获取目前记录条数
        } catch (SQLException e) {
            logError(getMessage(DATABASE_ERROR));
            e.printStackTrace();
            return;
        }

        try {
            logDebug(String.format(getMessage(READ_DEATH_LOG_RESULT), targetPlayer.getName(), count, recordLimit));
            if (count >= recordLimit) {  // 达到上限了
                // 删除记录 直到记录数为recordLimit-1 现在有count条，所以要删掉count-(recordLimit-1) = count-recordLimit+1
                psDeleteDeathLog.setString(1, targetPlayer.getUniqueId().toString());
                psDeleteDeathLog.setInt(2, count - recordLimit + 1);
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
            logError(String.format(getMessage(INSERT_DEATH_RECORD_FAIL), targetPlayer.getName()));  // 未成功录入死亡信息
            e.printStackTrace();
        }
    }

    /**
     * 向接受者发送目标玩家玩家的死亡日志
     *
     * @param playerName      目标玩家的用户名
     * @param receiver        接受者
     * @param voxelmapSupport voxelmap mod支持
     * @param xaerosSupport   xaeros mod支持
     * @param teleportSupport tp支持
     */
    public static void outputDeathLog(String playerName, CommandSender receiver, boolean voxelmapSupport, boolean xaerosSupport, boolean teleportSupport) {
        Player targetPlayer = Bukkit.getPlayerExact(playerName);  // 使用玩家名获取

        if (targetPlayer == null) {  // 检查是否获取到玩家
            receiver.sendMessage(getMessage(PLAYER_NOT_ONLINE));
            return;
        }
        outputDeathLog(targetPlayer, receiver, voxelmapSupport, xaerosSupport, teleportSupport);
    }

    /**
     * 向接受者发送目标玩家玩家的死亡日志
     *
     * @param targetPlayer    目标玩家对象
     * @param receiver        接受者
     * @param voxelmapSupport voxelmap mod支持
     * @param xaerosSupport   xaeros mod支持
     * @param teleportSupport tp支持
     */
    public static void outputDeathLog(@NotNull Player targetPlayer, CommandSender receiver, boolean voxelmapSupport, boolean xaerosSupport, boolean teleportSupport) {
        String playerName = targetPlayer.getName();
        UUID uuid = targetPlayer.getUniqueId();

        try (ResultSet rs = readDeathLog(uuid)) {
            readAndOutputDeathRecord(rs, receiver, playerName, voxelmapSupport, xaerosSupport, teleportSupport);
        } catch (SQLException e) {
            logInfo(getMessage(DATABASE_ERROR));
            receiver.sendMessage(getMessage(DATABASE_ERROR));
            e.printStackTrace();
        }
    }

    /**
     * 处理rs数据集，发送死亡记录
     *
     * @param rs              结果集
     * @param receiver        接受者
     * @param playerName      玩家名
     * @param voxelmapSupport voxelmap mod支持
     * @param xaerosSupport   xaeros mod支持
     * @param teleportSupport tp支持
     * @throws SQLException sql错误
     */
    private static void readAndOutputDeathRecord(@NotNull ResultSet rs, CommandSender receiver, String playerName, boolean voxelmapSupport, boolean xaerosSupport, boolean teleportSupport) throws SQLException {
        int count = 0;
        while (rs.next()) {
            sendComponentMessage(receiver, buildDeathLogMessage(rs, playerName, voxelmapSupport, xaerosSupport, teleportSupport));
            count++;
        }
        if (count == 0) {  // 没有已经存储的死亡记录
            receiver.sendMessage(String.format(getMessage(NO_DEATH_RECORD), playerName));
        } else {
            receiver.sendMessage(String.format(getMessage(READ_DEATH_RECORD), count));
        }
    }

    /**
     * 创建死亡日志消息
     *
     * @param rs              结果集
     * @param playerName      玩家名
     * @param voxelmapSupport voxelmap mod支持
     * @param xaerosSupport   xaeros mod支持
     * @param teleportSupport tp支持
     * @return 生成好的消息
     * @throws SQLException sql错误
     */
    private static Component buildDeathLogMessage(ResultSet rs, String playerName, boolean voxelmapSupport, boolean xaerosSupport, boolean teleportSupport) throws SQLException {
        // TODO sdf这个加入配置文件
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 编辑消息
        Component component = Component.text("")
                .append(Component.text(sdf.format(rs.getInt("deathTime") * 1000L)).color(NamedTextColor.YELLOW))  // 取得时间戳单位是秒, SimpleDateFormat需要毫秒, 所以乘1000L
                .append(Component.text(" -> ").color(NamedTextColor.WHITE))
                .append(Component.text(rs.getString("world")).color(NamedTextColor.YELLOW))
                .append(Component.text(String.format(getMessage(COORDINATES_FORMAT), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"))).color(NamedTextColor.YELLOW));

        if (voxelmapSupport) {
            component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text(getMessage(VOXELMAP_SUPPORT_HOVER))))
                    .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(VOXELMAP_SUPPORT_COMMAND), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getString("world")))));
        }

        if (xaerosSupport) {
            component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(Component.text(getMessage(XAEROS_SUPPORT_HOVER))))
                    .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(XAEROS_SUPPORT_COMMAND), playerName, playerName.charAt(0), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getString("world")))));
        }

        if (teleportSupport) {
            component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                    .append(Component.text("[tp] ").color(NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(Component.text(String.format(getMessage(TELEPORT_SUPPORT_HOVER), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")))))
                            .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(TELEPORT_SUPPORT_COMMAND), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")))));
        }
        return component;
    }

}
