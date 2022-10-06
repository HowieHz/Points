package com.hzzz.points.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static com.hzzz.points.commands.commands_utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.commands.commands_utils.Utils.specialCheckPermission;
import static com.hzzz.points.utils.Text.getMessage;
import static com.hzzz.points.utils.msgKey.*;

/**
 * where指令的执行器以及tab补全
 */
public final class Where implements TabExecutor {
    private static final Where instance = new Where();

    /**
     * 获取实例
     *
     * @return Instance of executor
     */
    public static Where getInstance() {
        return instance;
    }

    /**
     * 单例 无参数
     */
    private Where() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        switch (args.length) {
            case 0 -> {
                // /where
                // 此处sender就是player，只是一个是CommandSender类型一个强转成了Player类型

                // 检查执行者
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(getMessage(player_only));
                    return true;
                }
                // 权限检查
                if (!specialCheckPermission("where",
                        sender,
                        "points.command.where.self")) {
                    sender.sendMessage(getMessage(no_permission));
                    return true;
                }

                // 生成并发送消息给执行者
                sender.sendMessage(builderPlayerCoordinatesMessage("where", player));
                return true;
            }
            case 1 -> {
                // 权限检查
                if (!specialCheckPermission("where",
                        sender,
                        "points.command.where.other",
                        "points.command.where.other.%s",
                        args[0])
                ) {
                    sender.sendMessage(getMessage(no_permission));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayerExact(args[0]);  // 使用玩家名获取

                if (targetPlayer == null) {  // 检查是否获取到玩家
                    sender.sendMessage(getMessage(player_not_online));
                    return true;
                }

                // 生成并发送消息给执行者
                sender.sendMessage(builderPlayerCoordinatesMessage("where", targetPlayer));
                return true;
            }
            default -> {
                sender.sendMessage(getMessage(help_where));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* where
         * where <player_name>
         */
        if ((args.length == 0 || args.length == 1)  // 没有参数或者正在输入第一个参数（根指令后面只有一个空格（此时长度为0 /where ），或者第一个参数输入到一半（此时长度为一 /where Ho……））
                && (specialCheckPermission("where",
                sender,
                "points.command.where.other",
                "points.command.where.other.%s",
                args[0])
        )) {
            return null;  // 提示玩家名
        }
        // 前两个参数已经输入完成，不继续提示
        return Collections.singletonList("");
    }
}
