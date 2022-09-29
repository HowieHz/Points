package com.hzzz.points.data_manager.operations_set;

import com.hzzz.points.data_manager.sqlite.DeathLogSQLite;
import com.hzzz.points.text.text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static com.hzzz.points.Points.config;
import static com.hzzz.points.text.text.*;
import static com.hzzz.points.utils.Utils.logDetailInfo;

public class DeathLog {
    private static final PreparedStatement ps_delete_death_log = DeathLogSQLite.getInstance().ps_delete_death_log;
    private static final PreparedStatement ps_insert_death_log = DeathLogSQLite.getInstance().ps_insert_death_log;
    private static final PreparedStatement ps_select_death_log = DeathLogSQLite.getInstance().ps_select_death_log;

    public static void insertDeathLog(Player player, String death_reason) {
        // 增加死亡记录的操作
        int limit = config.getInt("death.log.record-limit", 5);  // 读取配置
        int count = countDeathLog(player.getUniqueId());  // 获取目前记录条数

        try {
            logDetailInfo(String.format(read_death_log_result, player.getName(), count, limit));
            if (count >= limit) {  // 达到上限了
                // 删除记录 直到记录数为limit-1 现在有count条，所以要删掉count-(limit-1) = count-limit+1
                ps_delete_death_log.setString(1,player.getUniqueId().toString());
                ps_delete_death_log.setInt(2,count - limit + 1);
                ps_delete_death_log.execute();
            }

            // 增加新的
            Location player_location = player.getLocation();  // 获取位置

            ps_insert_death_log.setString(1,player.getUniqueId().toString());
            ps_insert_death_log.setString(2,player.getName());
            ps_insert_death_log.setString(3,death_reason);
            ps_insert_death_log.setString(4,player.getWorld().getName());
            ps_insert_death_log.setDouble(5,player_location.getX());
            ps_insert_death_log.setDouble(6,player_location.getY());
            ps_insert_death_log.setDouble(7,player_location.getZ());
            ps_insert_death_log.execute();
        } catch (SQLException e) {
            logDetailInfo(String.format(insert_death_record_fail, player.getName()));  // 详细log 未成功录入死亡信息
            throw new RuntimeException(e);
        }
    }

    public static ResultSet readDeathLog(UUID uuid) {
        // 查询死亡记录的操作
        try {
            ps_select_death_log.setString(1, uuid.toString());
            return ps_select_death_log.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int countDeathLog(UUID uuid) {
        // 查询死亡记录数量的操作
        int count = 0;  // 结果行数
        try (ResultSet rs = readDeathLog(uuid)) {
            // 计算结果行数
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public static void outputDeathLog(UUID uuid, CommandSender receiver) {
        Player target_player = Bukkit.getPlayer(uuid);

        if (target_player == null) {  // 检查是否获取到玩家
            receiver.sendMessage(text.no_player);
            return;
        }
        outputDeathLog(target_player, receiver);
    }

    public static void outputDeathLog(String player_name, CommandSender receiver) {
        Player target_player = Bukkit.getPlayerExact(player_name);

        if (target_player == null) {  // 检查是否获取到玩家
            receiver.sendMessage(text.no_player);
            return;
        }
        outputDeathLog(target_player, receiver);
    }

    public static void outputDeathLog(Player target_player, CommandSender receiver) {
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
                        .append(Component.text(String.format(text.coordinates_format, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"))).color(NamedTextColor.YELLOW));

                if (config.getBoolean("death.log.voxelmap-support", false)) {
                    component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                            .hoverEvent(HoverEvent.showText(Component.text(text.voxelmap_support_hover)))
                            .clickEvent(ClickEvent.suggestCommand(String.format(text.voxelmap_support_command, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getString("world")))));
                }

                if (config.getBoolean("death.log.xaeros-support", false)) {
                    component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                            .hoverEvent(HoverEvent.showText(Component.text(text.xaeros_support_hover)))
                            .clickEvent(ClickEvent.suggestCommand(String.format(text.xaeros_support_command, player_name, player_name.charAt(0), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getString("world")))));
                }

                if (config.getBoolean("death.log.teleport-support", false)) {
                    component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                            .append(Component.text("[tp] ").color(NamedTextColor.RED)
                                    .hoverEvent(HoverEvent.showText(Component.text(String.format(text.teleport_support_hover, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")))))
                                    .clickEvent(ClickEvent.suggestCommand(String.format(text.teleport_support_command, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")))));
                }
                count++;
                receiver.sendMessage(component);
            }
            if (count == 0) {  // 没有已经存储的死亡记录
                receiver.sendMessage(String.format(no_death_record, player_name));
            } else {
                receiver.sendMessage(String.format(read_death_record, count));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
