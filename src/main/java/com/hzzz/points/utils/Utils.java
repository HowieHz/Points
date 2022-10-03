package com.hzzz.points.utils;

import com.hzzz.points.Points;
import org.bukkit.command.CommandSender;

/**
 * 工具类
 */
public class Utils {
    /**
     * 工具类禁止实例化
     */
    private Utils() {
        throw new IllegalStateException("工具类");
    }

    /**
     * 检查是否对象有权限(支持*通配符)
     *
     * @param sender          被检查权限的对象
     * @param permission_name 权限节点名
     * @return 是否有权限，有权限为ture，无权限为false
     */
    public static boolean checkPermission(CommandSender sender, String permission_name) {
        if (sender.hasPermission(permission_name)) {  // 有权限就返回true
            return true;
        } else {
            StringBuilder sb = new StringBuilder(permission_name);
            return sender.hasPermission(sb.replace(permission_name.lastIndexOf(".") + 1, permission_name.length(), "*").toString());  // 检查通配符
        }
    }

    /**
     * 向控制台发送消息
     *
     * @param message 消息内容
     */
    public static void logInfo(String message) {
        Points.logger.info(message);
    }

    /**
     * 向控制台发送错误
     *
     * @param message 消息内容
     */
    public static void logError(String message) {
        Points.logger.severe(message);
    }

    /**
     * 向控制台发送消息(需要config.yml -> log.more-information 为true)
     *
     * @param message 消息内容
     */
    public static void logDetailedInfo(String message) {
        if (Points.getInstance().getConfig().getBoolean("log.more-information", false)) {
            Points.logger.info(message);
        }
    }
}
