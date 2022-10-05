package com.hzzz.points.data_manager.sqlite;

import com.hzzz.points.utils.Text;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.hzzz.points.utils.Utils.logError;

/**
 * 管理death_log.sqlite
 */
public class DeathLogSQLite extends BaseSQLite {
    private static DeathLogSQLite instance;

    static {
        try {
            instance = new DeathLogSQLite();
        } catch (SQLException e) {
            logError(Text.getDatabaseSetupError());
            e.printStackTrace();
        }
    }

    public final PreparedStatement ps_delete_death_log;
    public final PreparedStatement ps_insert_death_log;
    public final PreparedStatement ps_select_death_log;

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
        ps_delete_death_log = con.prepareStatement("DELETE FROM DeathLog WHERE rowid in (SELECT rowid FROM DeathLog " +
                "WHERE uuid=?" +
                "ORDER BY deathTime " +
                "LIMIT ?)");
        ps_insert_death_log = con.prepareStatement("INSERT INTO DeathLog(uuid, username, deathReason, world, x, y, z) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps_select_death_log = con.prepareStatement("SELECT * FROM DeathLog WHERE uuid=?");
    }
}
