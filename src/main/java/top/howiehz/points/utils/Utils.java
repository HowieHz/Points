package top.howiehz.points.utils;

import top.howiehz.points.Points;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

    /**
     * 发送一条Component消息
     *
     * @param sender  目标
     * @param message 消息
     */
    public static void sendComponentMessage(CommandSender sender, Component message) {
        Points.getAdventure().sender(sender).sendMessage(message);
    }

    /**
     * 发送一条Component消息
     *
     * @param player  目标
     * @param message 消息
     */
    public static void sendComponentMessage(Player player, Component message) {
        Points.getAdventure().player(player).sendMessage(message);
    }

    /**
     * 发送一条Component消息 给全部玩家
     *
     * @param message 消息
     */
    public static void sendComponentMessageToPlayers(Component message) {
        Points.getAdventure().players().sendMessage(message);
    }
}
