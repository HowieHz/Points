package com.hzzz.points.data_manager.operations_set;

import com.hzzz.points.data_manager.sqlite.ConfigSQLite;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 有关DeathMessage的数据库操作
 */
public class DeathMessageConfig {
    private static final PreparedStatement ps_insert_death_config = ConfigSQLite.getInstance().ps_insert_death_config;
    private static final PreparedStatement ps_select_death_config = ConfigSQLite.getInstance().ps_select_death_config;
    private static final PreparedStatement ps_update_death_config = ConfigSQLite.getInstance().ps_update_death_config;

    /**
     * 工具类禁止实例化
     */
    private DeathMessageConfig() {
        throw new IllegalStateException("工具类");
    }

    /**
     * 检查是否开启了死亡提示
     *
     * @param player 目标玩家对象
     * @return 是否开启
     */
    public static boolean isEnableDeathMessage(Player player) throws SQLException {
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
    }

    /**
     * 翻转 目标玩家 是否开启死亡提示
     * （切换是否开启死亡提示）
     *
     * @param player 目标玩家对象
     * @return 翻转后的开启状态，如true则为开启
     */
    public static boolean updateDeathMessageConfig(Player player) throws SQLException {
        // 读取并翻转数据
        if (isEnableDeathMessage(player)) {
            ps_update_death_config.setInt(1, 0);
            ps_update_death_config.setString(2, player.getUniqueId().toString());
            ps_update_death_config.execute();
            return false;
        } else {
            ps_update_death_config.setInt(1, 1);
            ps_update_death_config.setString(2, player.getUniqueId().toString());
            ps_update_death_config.execute();
            return true;
        }
    }
}
