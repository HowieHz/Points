package com.hzzz.points.utils;

import com.hzzz.points.Points;
import org.bukkit.command.CommandSender;

import static com.hzzz.points.Points.config;

public class Utils {
    public static boolean checkPermission(CommandSender sender, String permission_name){
        if (sender.hasPermission(permission_name)){  // 有权限就返回true
            return true;
        }else{
            StringBuilder sb = new StringBuilder(permission_name);
            return sender.hasPermission(sb.replace(permission_name.lastIndexOf(".") + 1, permission_name.length(), "*").toString());  // 检查通配符
        }
    }

    public static void logInfo(String message){
        Points.logger.info(message);
    }
    public static void logDetailInfo(String message){
        if (config.getBoolean("log.more-information", false)){
            Points.logger.info(message);
        }
    }
}
