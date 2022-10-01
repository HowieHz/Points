package com.hzzz.points.data_manager.sqlite;

import com.hzzz.points.Points;
import com.hzzz.points.data_manager.sqlite.utils.JdbcUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class ConfigSQLite {
    private static final ConfigSQLite INSTANCE = new ConfigSQLite();
    private final Connection con;  // 连接
    private final Statement st;  // 数据库操作接口

    /**
     * 获取数据库实例
     *
     * @return statement of database
     */
    public static ConfigSQLite getInstance() {
        return INSTANCE;
    }

    /**
     * 获取数据库操作接口 Statement
     *
     * @return statement of database
     */
    public Statement getStatement() {
        return st;
    }

    /**
     * 获取数据库连接 Connection
     *
     * @return Connection of database
     */
    public Connection getConnection() {
        return con;
    }

    public final PreparedStatement ps_insert_death_config;
    public final PreparedStatement ps_select_death_config;
    public final PreparedStatement ps_update_death_config;

    private ConfigSQLite() {
        try {
            // 连接数据库
            con = JdbcUtils.getConnection("jdbc:sqlite:plugins/Points/database/config.sqlite");
            st = con.createStatement();
            // 创建表
            st.executeUpdate("CREATE TABLE if NOT EXISTS DeathMessageConfig(" +
                    "uuid CHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "enable INTEGER NOT NULL" +
                    ")");

            ps_insert_death_config = con.prepareStatement("INSERT OR IGNORE INTO DeathMessageConfig(uuid, username, enable) VALUES (?, ?, 1)");
            ps_select_death_config = con.prepareStatement("SELECT * FROM DeathMessageConfig WHERE uuid=?");
            ps_update_death_config = con.prepareStatement("UPDATE DeathMessageConfig SET enable=? WHERE uuid=?");
        } catch (SQLException e) {
            e.printStackTrace();
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
                    e.printStackTrace();
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
