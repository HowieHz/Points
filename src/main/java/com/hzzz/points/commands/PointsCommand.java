package com.hzzz.points.commands;

import com.hzzz.points.Points;
import com.hzzz.points.commands.base_executor.HowieUtilsExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hzzz.points.utils.Utils.logInfo;
import static com.hzzz.points.utils.message.Lang.getMessage;
import static com.hzzz.points.utils.message.MsgKey.*;

/**
 * points指令的执行器以及tab补全
 */
public final class PointsCommand extends HowieUtilsExecutor {
    private static final String PARENT_CONFIG_NODE_RELOAD = "points.reload";
    private static final String PARENT_CONFIG_NODE_HELP = "points.help";
    private static final String DEFAULT_PERMISSION_RELOAD = "points.command.reload";
    private static final String DEFAULT_PERMISSION_HELP = "points.command.help";
    private static final PointsCommand instance = new PointsCommand();

    /**
     * 获取实例
     *
     * @return Instance of executor
     */
    public static PointsCommand getInstance() {
        return instance;
    }

    /**
     * 单例 无参数
     */
    private PointsCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equals("reload")) {
            // 权限检查
            if (!checkPermissionOneConfigNode(sender, PARENT_CONFIG_NODE_RELOAD, DEFAULT_PERMISSION_RELOAD)) {
                sender.sendMessage(getMessage(NO_PERMISSION));
                return true;
            }
            // 重载的逻辑
            Points.getInstance().onReload();
            // 发消息
            sender.sendMessage(getMessage(RELOAD_READY));
            if (sender instanceof Player) {  // 玩家重载 在控制台也输出重载结果
                logInfo(getMessage(RELOAD_READY));
            }
            return true;
        }
        // args[0].equals("help")
        sender.sendMessage(getMessage(HELP_POINTS));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* points
         * points help
         * points reload
         */
        List<String> completeArrays = new ArrayList<>();
        if (args.length == 0 || args.length == 1) {
            // 没有参数或者正在输入第一个参数（根指令后面只有一个空格（此时长度为0 /points ），或者第一个参数输入到一半（此时长度为一 /points he……））
            if (checkPermissionOneConfigNode(sender, PARENT_CONFIG_NODE_RELOAD, DEFAULT_PERMISSION_RELOAD)) {
                completeArrays.add("reload");
            }
            if (checkPermissionOneConfigNode(sender, PARENT_CONFIG_NODE_HELP, DEFAULT_PERMISSION_HELP)) {
                completeArrays.add("help");
            }
        }
        return completeArrays;
    }
}
