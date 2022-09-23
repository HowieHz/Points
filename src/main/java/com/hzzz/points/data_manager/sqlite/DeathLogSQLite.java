package com.hzzz.points.data_manager.sqlite;

import com.hzzz.points.Points;
import com.hzzz.points.data_manager.sqlite.utils.JdbcUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class DeathLogSQLite {
    private static final DeathLogSQLite INSTANCE = new DeathLogSQLite();
//    private final FileConfiguration CONFIG = Points.config;  // 读取配置
    private Connection con;  // 连接
    private Statement st = null;  // 数据库操作接口

    public static DeathLogSQLite getInstance() {
        return INSTANCE;
    }

    public Statement getStatement() {
        return st;
    }

    private DeathLogSQLite() {
        setup();
    }

    private void setup() {  // 初始化数据库连接
        try {
            // 连接数据库
            con = JdbcUtils.getConnection("jdbc:sqlite:plugins/Points/death_log.sqlite");
            st = con.createStatement();
            // 创建表
            st.executeUpdate("CREATE TABLE if NOT EXISTS DeathLog(" +
                    "id INT PRIMARY KEY NOT NULL,"+
                    "uuid CHAR(36) NOT NULL, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "deathReason VARCHAR(255) NOT NULL, " +
                    "world VARCHAR(255) NOT NULL, " +
                    "x DOUBLE NOT NULL DEFAULT 0.0, " +
                    "y DOUBLE NOT NULL DEFAULT 0.0, " +
                    "z DOUBLE NOT NULL DEFAULT 0.0, " +
                    "deathTime TIMESTAMP" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void executeUpdate(String sql) {
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

    public boolean state() {  // 状态查询
        return (st != null && con != null);
    }
}
