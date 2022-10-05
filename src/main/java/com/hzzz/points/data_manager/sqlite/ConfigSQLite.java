package com.hzzz.points.data_manager.sqlite;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 管理config.sqlite
 */
public class ConfigSQLite extends BaseSQLite {
    private static final ConfigSQLite instance;

    static {
        try {
            instance = new ConfigSQLite();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public final PreparedStatement ps_insert_death_config;
    public final PreparedStatement ps_select_death_config;
    public final PreparedStatement ps_update_death_config;

    /**
     * 获取数据库实例
     *
     * @return statement of database
     */
    public static ConfigSQLite getInstance() {
        return instance;
    }

    /**
     * 单例 无参数 初始化数据库
     */
    private ConfigSQLite() throws SQLException {
        super("jdbc:sqlite:plugins/Points/database/config.sqlite");
        // 创建表
        st.executeUpdate("CREATE TABLE if NOT EXISTS DeathMessageConfig(" +
                "uuid CHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL, " +
                "enable INTEGER NOT NULL" +
                ")");
        // 初始化
        ps_insert_death_config = con.prepareStatement("INSERT OR IGNORE INTO DeathMessageConfig(uuid, username, enable) VALUES (?, ?, 1)");
        ps_select_death_config = con.prepareStatement("SELECT * FROM DeathMessageConfig WHERE uuid=?");
        ps_update_death_config = con.prepareStatement("UPDATE DeathMessageConfig SET enable=? WHERE uuid=?");
    }
}
