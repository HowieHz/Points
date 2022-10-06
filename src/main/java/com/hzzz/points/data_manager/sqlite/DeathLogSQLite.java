package com.hzzz.points.data_manager.sqlite;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.hzzz.points.utils.message.Lang.getMessage;
import static com.hzzz.points.utils.Utils.logError;
import static com.hzzz.points.utils.message.MsgKey.DATABASE_SETUP_ERROR;

/**
 * 管理death_log.sqlite
 */
public final class DeathLogSQLite extends BaseSQLite {
    private static DeathLogSQLite instance;

    static {
        try {
            instance = new DeathLogSQLite();
        } catch (SQLException e) {
            logError(getMessage(DATABASE_SETUP_ERROR));
            e.printStackTrace();
        }
    }

    public final PreparedStatement psDeleteDeathLog;
    public final PreparedStatement psInsertDeathLog;
    public final PreparedStatement psSelectDeathLog;

    /**
     * 获取数据库实例
     *
     * @return statement of database
     */
    public static DeathLogSQLite getInstance() {
        return instance;
    }

    /**
     * 单例 无参数 初始化数据库
     */
    private DeathLogSQLite() throws SQLException {
        super("jdbc:sqlite:plugins/Points/database/death_log.sqlite");
        // 创建表
        st.executeUpdate("CREATE TABLE if NOT EXISTS DeathLog(" +
                "uuid CHAR(36) NOT NULL, " +
                "username VARCHAR(255) NOT NULL, " +
                "deathReason VARCHAR(255) NOT NULL, " +
                "world VARCHAR(255) NOT NULL, " +
                "x DOUBLE NOT NULL DEFAULT 0.0, " +
                "y DOUBLE NOT NULL DEFAULT 0.0, " +
                "z DOUBLE NOT NULL DEFAULT 0.0, " +
                "deathTime TIMESTAMP NOT NULL DEFAULT (strftime('%s','now'))" +
                ")");
        // 初始化
        psDeleteDeathLog = con.prepareStatement("DELETE FROM DeathLog WHERE rowid in (SELECT rowid FROM DeathLog " +
                "WHERE uuid=?" +
                "ORDER BY deathTime " +
                "LIMIT ?)");
        psInsertDeathLog = con.prepareStatement("INSERT INTO DeathLog(uuid, username, deathReason, world, x, y, z) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");
        psSelectDeathLog = con.prepareStatement("SELECT * FROM DeathLog WHERE uuid=?");
    }
}
