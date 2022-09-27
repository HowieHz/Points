package com.hzzz.points.data_manager.operations_set;

import com.hzzz.points.data_manager.sqlite.ConfigSQLite;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeathMessageConfig {
    private static final PreparedStatement ps_insert_death_config = ConfigSQLite.getInstance().ps_insert_death_config;
    private static final PreparedStatement ps_select_death_config = ConfigSQLite.getInstance().ps_select_death_config;
    private static final PreparedStatement ps_update_death_config = ConfigSQLite.getInstance().ps_update_death_config;


    public static boolean IsEnableDeathMessage(Player player) {  // 检查是否开启死亡提示
        try {
            // 初始化
            ps_insert_death_config.setString(1, player.getUniqueId().toString());
            ps_insert_death_config.setString(2, player.getName());
            ps_insert_death_config.execute();

            // 验证数据
            int enable = 0;
            ps_select_death_config.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps_select_death_config.executeQuery();
            if (rs.next()) {  // 结果集第一个 没有数据(返回false) 还调用getInt就会报错java.sql.SQLException: ResultSet closed
                enable = rs.getInt("enable");
            }
            rs.close();
            return enable == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateDeathMessageConfig(Player player) {  // 切换是否开启死亡提示
        // 读取并翻转数据
        if (IsEnableDeathMessage(player)) {
            try {
                ps_update_death_config.setInt(1, 0);
                ps_update_death_config.setString(2, player.getUniqueId().toString());
                ps_update_death_config.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return false;
        } else {
            try {
                ps_update_death_config.setInt(1, 1);
                ps_update_death_config.setString(2, player.getUniqueId().toString());
                ps_update_death_config.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }
}
