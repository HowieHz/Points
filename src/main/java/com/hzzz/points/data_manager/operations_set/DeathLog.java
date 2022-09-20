package com.hzzz.points.data_manager.operations_set;

import com.hzzz.points.data_manager.ConfigSQLite;
import org.bukkit.entity.Player;

import java.sql.Statement;

public class DeathLog {
    private static final Statement st = ConfigSQLite.getInstance().getStatement();  // 获取操作接口
    public static boolean insertDeathLog(Player player){
         // TODO 增加死亡记录的操作
        return false;
    }
    public static boolean readDeathLog(Player player){
         // TODO 查询死亡记录的操作
        return false;
    }
}
