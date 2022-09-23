package com.hzzz.points.data_manager.operations_set;

import com.hzzz.points.data_manager.sqlite.ConfigSQLite;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DeathMessageConfig {
    private static final Statement st = ConfigSQLite.getInstance().getStatement();  // 获取操作接口
    public static boolean IsEnableDeathMessage(Player player) {  // 检查是否开启死亡提示
        try {
            // 初始化
            st.executeUpdate(String.format("INSERT OR IGNORE INTO DeathMessageConfig(uuid, username, enable) VALUES ('%s', '%s', 1)", player.getUniqueId(), player.getName()));

            // 验证数据
            int enable = 0;
            ResultSet rs = st.executeQuery(String.format("SELECT * FROM DeathMessageConfig WHERE uuid = '%s'", player.getUniqueId()));
            if (rs.next()) {  // 结果集第一个 没有数据(返回false) 还调用getInt就会报错java.sql.SQLException: ResultSet closed
                enable = rs.getInt("enable");
            }
            rs.close();
            return enable == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateDeathMessageConfig(Player player) {  // 切换是否开启死亡提示
        // 读取并翻转数据
        if (IsEnableDeathMessage(player)) {
            try {
                st.executeUpdate(String.format("update DeathMessageConfig set enable = 0 where uuid = '%s'", player.getUniqueId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            try {
                st.executeUpdate(String.format("update DeathMessageConfig set enable = 1 where uuid = '%s'", player.getUniqueId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
