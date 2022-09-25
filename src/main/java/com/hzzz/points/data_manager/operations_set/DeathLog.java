package com.hzzz.points.data_manager.operations_set;

import com.hzzz.points.data_manager.sqlite.DeathLogSQLite;
import com.hzzz.points.text.text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import static com.hzzz.points.Points.config;
import static com.hzzz.points.text.text.*;
import static com.hzzz.points.utils.Utils.logDetailInfo;

public class DeathLog {
    private static final Statement st = DeathLogSQLite.getInstance().getStatement();  // 获取操作接口

    public static boolean insertDeathLog(Player player, String death_reason) {
        // 增加死亡记录的操作 返回true则为记录成功
        int limit = config.getInt("death.log.record-limit", 5);  // 读取配置
        int count = countDeathLog(player.getName());  // 获取目前记录条数

        try (ResultSet rs = readDeathLog(player.getName())) {
            logDetailInfo(String.format("%s 在数据库中记录 %d 条, 限制为 %d 条", player.getName(), count, limit));
            if (count >= limit) {  // 达到上限了
                for (int i = 0; i < count + 1 - limit; i++) {  // 删除记录 直到记录数为limit-1
                    rs.next();
                    // TODO 用一个数字做主键来插件，而不是用uuid和deathtime
                    st.executeUpdate(String.format("DELETE FROM DeathLog WHERE username = '%s' AND deathTime = %d", rs.getString("username"), rs.getInt("deathTime")));
                }
            }

            // 增加新的
            Location player_location = player.getLocation();  // 获取位置
            st.executeUpdate(String.format("INSERT INTO DeathLog(uuid, username, deathReason, world, x, y, z) " +
                            "VALUES ('%s', '%s', '%s', '%s', %f, %f, %f)",
                    player.getUniqueId(),
                    player.getName(),
                    death_reason,
                    player.getWorld().getName(),
                    player_location.getX(),
                    player_location.getY(),
                    player_location.getZ()));
            return true;
        } catch (SQLException e) {
            logDetailInfo(String.format(insert_death_record_fail, player.getName()));  // 详细log 未成功录入死亡信息
            throw new RuntimeException(e);
        }
    }

    public static ResultSet readDeathLog(String player_name) {
        // 查询死亡记录的操作
        try {
            return st.executeQuery(String.format("SELECT * FROM DeathLog WHERE username = '%s'", player_name));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int countDeathLog(String player_name) {
        // 查询死亡记录数量的操作
        int count = 0;  // 结果行数
        try (ResultSet rs = readDeathLog(player_name)) {
            // 计算结果行数
            while (rs.next()) {
                count++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public static void outputDeathLog(String player_name, CommandSender receiver) {
        try (ResultSet rs = readDeathLog(player_name)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int count = 0;
            while (rs.next()) {
                // 编辑消息
                Component component = Component.text("")
                        .append(Component.text(sdf.format(rs.getInt("deathTime")*1000L)).color(NamedTextColor.YELLOW))  // 取得时间戳单位是秒, SimpleDateFormat需要毫秒, 所以乘1000L
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
