package com.hzzz.points.data_manager.sqlite;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.hzzz.points.utils.message.Lang.getMessage;
import static com.hzzz.points.utils.Utils.logError;
import static com.hzzz.points.utils.message.MsgKey.database_setup_error;

/**
 * 管理config.sqlite
 */
public final class ConfigSQLite extends BaseSQLite {
    private static ConfigSQLite instance;

    static {
        try {
            instance = new ConfigSQLite();
        } catch (SQLException e) {
            logError(getMessage(database_setup_error));
            e.printStackTrace();
        }
    }

    public final PreparedStatement psInsertDeathConfig;
    public final PreparedStatement psSelectDeathConfig;
    public final PreparedStatement psUpdateDeathConfig;

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
        psInsertDeathConfig = con.prepareStatement("INSERT OR IGNORE INTO DeathMessageConfig(uuid, username, enable) VALUES (?, ?, 1)");
        psSelectDeathConfig = con.prepareStatement("SELECT * FROM DeathMessageConfig WHERE uuid=?");
        psUpdateDeathConfig = con.prepareStatement("UPDATE DeathMessageConfig SET enable=? WHERE uuid=?");
    }
}
