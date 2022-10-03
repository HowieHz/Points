package com.hzzz.points.data_manager.operations_set;

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

import static com.hzzz.points.text.Text.*;
import static com.hzzz.points.utils.Utils.*;

/**
 * 有关DeathLog的数据库操作
 */
public class DeathLog {
    private static final PreparedStatement ps_delete_death_log = DeathLogSQLite.getInstance().ps_delete_death_log;
    private static final PreparedStatement ps_insert_death_log = DeathLogSQLite.getInstance().ps_insert_death_log;
    private static final PreparedStatement ps_select_death_log = DeathLogSQLite.getInstance().ps_select_death_log;

    /**
     * 增加死亡记录的操作
     *
     * @param target_player 目标玩家对象
     * @param death_reason  死亡原因
     */
    public static void insertDeathLog(Player target_player, String death_reason) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        int limit = config.getInt("death.log.record-limit", 5);  // 读取配置
        int count;  // 目前记录条数
        try {
            count = countDeathLog(target_player.getUniqueId());  // 获取目前记录条数
        } catch (SQLException e) {
            logError(getDatabaseError());
            e.printStackTrace();
            return;
        }

        try {
            logDetailedInfo(String.format(getReadDeathLogResult(), target_player.getName(), count, limit));
            if (count >= limit) {  // 达到上限了
                // 删除记录 直到记录数为limit-1 现在有count条，所以要删掉count-(limit-1) = count-limit+1
                ps_delete_death_log.setString(1, target_player.getUniqueId().toString());
                ps_delete_death_log.setInt(2, count - limit + 1);
                ps_delete_death_log.execute();
            }

            // 增加新的
            Location player_location = target_player.getLocation();  // 获取位置

            ps_insert_death_log.setString(1, target_player.getUniqueId().toString());
            ps_insert_death_log.setString(2, target_player.getName());
            ps_insert_death_log.setString(3, death_reason);
            ps_insert_death_log.setString(4, target_player.getWorld().getName());
            ps_insert_death_log.setDouble(5, player_location.getX());
            ps_insert_death_log.setDouble(6, player_location.getY());
            ps_insert_death_log.setDouble(7, player_location.getZ());
            ps_insert_death_log.execute();
        } catch (SQLException e) {
            logError(String.format(getInsertDeathRecordFail(), target_player.getName()));  // 未成功录入死亡信息
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
        ps_select_death_log.setString(1, uuid.toString());
        return ps_select_death_log.executeQuery();
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
        Player target_player = Bukkit.getPlayer(uuid);  // 使用uuid获取

        if (target_player == null) {  // 检查是否获取到玩家
            receiver.sendMessage(getPlayerNotOnline());
            return;
        }
        outputDeathLog(target_player, receiver);
    }

    /**
     * 向接受者发送目标玩家玩家的死亡日志
     *
     * @param player_name 目标玩家的用户名
     * @param receiver    接受者
     */
    public static void outputDeathLog(String player_name, CommandSender receiver) {
        Player target_player = Bukkit.getPlayerExact(player_name);  // 使用玩家名获取

        if (target_player == null) {  // 检查是否获取到玩家
            receiver.sendMessage(getPlayerNotOnline());
            return;
        }
        outputDeathLog(target_player, receiver);
    }

    /**
     * 向接受者发送目标玩家玩家的死亡日志
     *
     * @param target_player 目标玩家对象
     * @param receiver      接受者
     */
    public static void outputDeathLog(Player target_player, CommandSender receiver) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        String player_name = target_player.getName();
        UUID uuid = target_player.getUniqueId();

        try (ResultSet rs = readDeathLog(uuid)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int count = 0;
            while (rs.next()) {
                // 编辑消息
                Component component = Component.text("")
                        .append(Component.text(sdf.format(rs.getInt("deathTime") * 1000L)).color(NamedTextColor.YELLOW))  // 取得时间戳单位是秒, SimpleDateFormat需要毫秒, 所以乘1000L
                        .append(Component.text(" -> ").color(NamedTextColor.WHITE))
                        .append(Component.text(rs.getString("world")).color(NamedTextColor.YELLOW))
                        .append(Component.text(String.format(getCoordinatesFormat(), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"))).color(NamedTextColor.YELLOW));

                if (config.getBoolean("death.log.voxelmap-support", false)) {
                    component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                            .hoverEvent(HoverEvent.showText(Component.text(getVoxelmapSupportHover())))
                            .clickEvent(ClickEvent.suggestCommand(String.format(getVoxelmapSupportCommand(), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getString("world")))));
                }

                if (config.getBoolean("death.log.xaeros-support", false)) {
                    component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                            .hoverEvent(HoverEvent.showText(Component.text(getXaerosSupportHover())))
                            .clickEvent(ClickEvent.suggestCommand(String.format(getXaerosSupportCommand(), player_name, player_name.charAt(0), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getString("world")))));
                }

                if (config.getBoolean("death.log.teleport-support", false)) {
                    component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                            .append(Component.text("[tp] ").color(NamedTextColor.RED)
                                    .hoverEvent(HoverEvent.showText(Component.text(String.format(getTeleportSupportHover(), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")))))
                                    .clickEvent(ClickEvent.suggestCommand(String.format(getTeleportSupportCommand(), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")))));
                }
                count++;
                receiver.sendMessage(component);
            }
            if (count == 0) {  // 没有已经存储的死亡记录
                receiver.sendMessage(String.format(getNoDeathRecord(), player_name));
            } else {
                receiver.sendMessage(String.format(getReadDeathRecord(), count));
            }
        } catch (SQLException e) {
            logInfo(getDatabaseError());
            receiver.sendMessage(getDatabaseError());
            e.printStackTrace();
        }
    }
}
