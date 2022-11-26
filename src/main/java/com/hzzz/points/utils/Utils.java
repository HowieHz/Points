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
     * @param sender 被检查权限的对象
     * @param name   权限节点名
     * @return 是否有权限，有权限为ture，无权限为false
     */
    public static boolean checkPermission(CommandSender sender, String name) {
        if (sender.hasPermission(name)) {  // 有权限就返回true
            return true;
        } else {
            StringBuilder sb = new StringBuilder(name);
            return sender.hasPermission(sb.replace(name.lastIndexOf(".") + 1, name.length(), "*").toString());  // 检查通配符
        }
    }

    // TODO 重构通配符模式

    /**
     * 检查一段字符串末尾是否是指定字符串(通配符检查)<br>如果是就格式化，不是就使用默认字符串进行格式化
     *
     * @param string        一段字符串
     * @param endString     检查结尾是否是此字符串
     * @param defaultString 默认字符串
     * @param args          格式化参数
     * @return 格式化完毕之后的字符串
     */
    public static String stringFormatEnd(String string, String endString, String defaultString, Object... args) {
        if (string == null) {
            return String.format(defaultString, args);
        }

        if (string.endsWith(endString)) {
            return String.format(string, args);
        } else {
            return String.format(defaultString, args);
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
    public static boolean isDebug() {
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

    /**
     * 主线程调度一个异步任务
     *
     * @param task 任务
     */
    public static void runTaskAsynchronously(@NotNull Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(Points.getInstance(), task);
    }

    /**
     * 以控制台的角度执行指令
     *
     * @param command 要执行的指令
     * @return 若指令未找到返回false
     */
    public static boolean executeCommand(@NotNull String command) {
        return Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
