package com.hzzz.points.data_manager.sqlite;

import com.hzzz.points.Points;
import com.hzzz.points.data_manager.sqlite.utils.JdbcUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class ConfigSQLite {
    private static final ConfigSQLite INSTANCE = new ConfigSQLite();
    private Connection con;  // 连接
    private Statement st;  // 数据库操作接口

    public static ConfigSQLite getInstance() {
        return INSTANCE;
    }
    public Statement getStatement() {
        return st;
    }

    public Connection getConnection() {
        return con;
    }

    private ConfigSQLite() {
        setup();
    }

    private void setup() {  // 初始化数据库连接
        try {
            // 连接数据库
            con = JdbcUtils.getConnection("jdbc:sqlite:plugins/Points/config.sqlite");
            st = con.createStatement();
            // 创建表
            st.executeUpdate("CREATE TABLE if NOT EXISTS DeathMessageConfig(" +
                    "uuid CHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "enable INTEGER NOT NULL" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


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

    public boolean state() {  // 状态查询
        return (st != null && con != null);
    }
}
