package com.hzzz.points.data_manager.sqlite;

import com.hzzz.points.Points;
import com.hzzz.points.data_manager.sqlite.utils.JdbcUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

/**
 * 管理death_log.sqlite
 */
public class DeathLogSQLite {
    private static final DeathLogSQLite INSTANCE = new DeathLogSQLite();
    private final Connection con;  // 连接
    private final Statement st;  // 数据库操作接口

    /**
     * 获取数据库实例
     *
     * @return statement of database
     */
    public static DeathLogSQLite getInstance() {
        return INSTANCE;
    }

    /**
     * 获取操作接口Statement
     *
     * @return statement of sqlite
     */
    public Statement getStatement() {
        return st;
    }

    /**
     * 获取操作接口Connection
     *
     * @return Connection of sqlite
     */
    public Connection getConnection() {
        return con;
    }

    public final PreparedStatement ps_delete_death_log;
    public final PreparedStatement ps_insert_death_log;
    public final PreparedStatement ps_select_death_log;

    private DeathLogSQLite() {
        try {
            // 连接数据库
            con = JdbcUtils.getConnection("jdbc:sqlite:plugins/Points/database/death_log.sqlite");
            st = con.createStatement();
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

            ps_delete_death_log = con.prepareStatement("DELETE FROM DeathLog WHERE rowid in (SELECT rowid FROM DeathLog " +
                    "WHERE uuid=?" +
                    "ORDER BY deathTime " +
                    "LIMIT ?)");

            ps_insert_death_log = con.prepareStatement("INSERT INTO DeathLog(uuid, username, deathReason, world, x, y, z) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");

            ps_select_death_log = con.prepareStatement("SELECT * FROM DeathLog WHERE uuid=?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 异步执行sql语句
     *
     * @param sql sql语句
     */
    private void asyncExecuteUpdate(String sql) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    st.executeUpdate(sql);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(Points.getInstance());
    }

    /**
     * 检查数据库状态是否准备好<br>（检查Statement和Connection是否调用过close方法）<br>
     * 准备好则返回true
     *
     * @return 是否可用
     */
    public boolean isReady() {  // 状态查询
        return (st != null && con != null);
    }
}
