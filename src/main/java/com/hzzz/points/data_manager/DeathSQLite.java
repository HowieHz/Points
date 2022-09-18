package com.hzzz.points.data_manager;

import com.hzzz.points.Points;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class DeathSQLite {
    private static final DeathSQLite INSTANCE = new DeathSQLite();
    private final FileConfiguration CONFIG = Points.config;  // 读取配置
    private Connection con;  // 连接
    private Statement st = null;  // 数据库操作接口
    private boolean ready_flag = false;  // 是否准备好的标志

    public static DeathSQLite getInstance() {
        return INSTANCE;
    }

    private DeathSQLite() {
        setup();
    }

    private void setup() {  // 初始化数据库连接
        try {
            // 连接数据库
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:plugins/Points/death.sqlite");
            st = con.createStatement();
            // 创建表
            st.executeUpdate("CREATE TABLE if NOT EXISTS DeathMessageConfig(" +
                    "UUID CHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
                    "Name VARCHAR(255) NOT NULL, " +
                    "Enable INTEGER NOT NULL" +
                    ")");
            st.executeUpdate("CREATE TABLE if NOT EXISTS DeathLog(" +
                    "UUID CHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
                    "Name VARCHAR(255) NOT NULL, " +
                    "DeathReason VARCHAR(255) NOT NULL, " +
                    "DeathTime TIMESTAMP NOT NULL" +
                    ")");
            ready_flag = true;
        } catch (SQLException | ClassNotFoundException e) {
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
        return (st != null && con != null && ready_flag) ;
    }

    // 以下是用法组合

    // TODO 增加死亡信息的操作

    public final boolean IsEnableDeathMessage(Player player) {  // 检查是否开启死亡提示
        try {
            // 初始化
            st.executeUpdate(String.format("INSERT OR IGNORE INTO DeathMessageConfig(UUID, Name, Enable) VALUES ('%s', '%s', 1)", player.getUniqueId(), player.getName()));

            // 验证数据
            int enable = 0;
            ResultSet rs = st.executeQuery(String.format("SELECT * FROM DeathMessageConfig WHERE UUID = '%s'", player.getUniqueId()));
            if (rs.next()) {  // 结果集第一个 没有数据(返回false) 还调用getInt就会报错java.sql.SQLException: ResultSet closed
                enable = rs.getInt("Enable");
            }
            rs.close();
            return enable == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public final boolean updateDeathMessageConfig(Player player) {  // 切换是否开启死亡提示
        // 读取并翻转数据
        if (IsEnableDeathMessage(player)) {
            try {
                st.executeUpdate(String.format("update DeathMessageConfig set Enable = 0 where UUID = '%s'", player.getUniqueId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            try {
                st.executeUpdate(String.format("update DeathMessageConfig set Enable = 1 where UUID = '%s'", player.getUniqueId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
