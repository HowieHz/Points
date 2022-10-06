package com.hzzz.points.utils;

import com.hzzz.points.Points;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * 工具类
 */
public final class Utils {
    /**
     * 工具类禁止实例化
     */
    private Utils() {
        throw new IllegalStateException("工具类");
    }

    /**
     * 检查是否对象有权限(支持*通配符)
     *
     * @param sender         被检查权限的对象
     * @param permissionName 权限节点名
     * @return 是否有权限，有权限为ture，无权限为false
     */
    public static boolean checkPermission(CommandSender sender, String permissionName) {
        if (sender.hasPermission(permissionName)) {  // 有权限就返回true
            return true;
        } else {
            StringBuilder sb = new StringBuilder(permissionName);
            return sender.hasPermission(sb.replace(permissionName.lastIndexOf(".") + 1, permissionName.length(), "*").toString());  // 检查通配符
        }
    }

    /**
     * 向控制台发送消息
     *
     * @param message 消息内容
     */
    public static void logInfo(String message) {
        Points.pluginLogger.info(message);
    }

    /**
     * 向控制台发送错误
     *
     * @param message 消息内容
     */
    public static void logError(String message) {
        Points.pluginLogger.severe(message);
    }

    /**
     * 向控制台发送debug消息
     *
     * @param message 消息内容
     */
    public static void logDebug(String message) {
        if (isDebug()) {
            Points.pluginLogger.severe(message);
        }
    }

    /**
     * 检查debug是否开启
     */
    public static boolean isDebug(){
        return Points.getInstance().getConfig().getBoolean("debug.enable", false);
    }

    /**
     * 向控制台发送消息(需要config.yml -> log.more-information 为true)
     *
     * @param message 消息内容
     */
    public static void logDetailedInfo(String message) {
        if (Points.getInstance().getConfig().getBoolean("log.more-information", false)) {
            Points.pluginLogger.info(message);
        }
    }

    public static void runTaskAsynchronously(@NotNull Runnable task){
        Bukkit.getScheduler().runTaskAsynchronously(Points.getInstance(), task);
    }
}
