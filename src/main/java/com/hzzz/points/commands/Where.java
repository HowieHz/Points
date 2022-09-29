package com.hzzz.points.commands;

import com.hzzz.points.text.text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

import static com.hzzz.points.Points.config;
import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.utils.Utils.checkPermission;

public final class Where implements TabExecutor {
    private static final Where INSTANCE = new Where();

    public static Where getInstance() {
        return INSTANCE;
    }

    public Where() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        switch (args.length) {
            case 0 -> {
                // /where
                // 此处sender就是player，只是一个是CommandSender类型一个强转成了Player类型

                // 检查执行者
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(text.player_only);
                    return true;
                }
                // 权限检查
                if (config.getBoolean("where.permission.enable", false)
                        && !checkPermission(sender, config.getString("where.permission.node.self", "points.command.where.self"))) {
                    sender.sendMessage(text.no_permission);
                    return true;
                }

                // 生成并发送消息给执行者
                sender.sendMessage(builderPlayerCoordinatesMessage("where", config, player));
                return true;
            }
            case 1 -> {
                // 权限检查
                if (config.getBoolean("where.permission.enable", false)
                        && !checkPermission(sender, config.getString("where.permission.node.other", "points.command.where.other"))
                        && !checkPermission(sender, String.format(config.getString("where.permission.node.player", "points.command.where.%s"), args[0]))) {
                    sender.sendMessage(text.no_permission);
                    return true;
                }

                Player target_player = Bukkit.getPlayerExact(args[0]);

                if (target_player == null) {  // 检查是否获取到玩家
                    sender.sendMessage(text.no_player);
                    return true;
                }

                // 生成并发送消息给执行者
                sender.sendMessage(builderPlayerCoordinatesMessage("where", config, target_player));
                return true;
            }
            default -> {
                sender.sendMessage(text.help_where);
                return true;
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* where
         * where <player_name>
         */
        switch (args.length) {
            case 0, 1 -> {
                // 没有参数或者正在输入第一个参数（根指令后面只有一个空格（此时长度为0 /where ），或者第一个参数输入到一半（此时长度为一 /where Ho……））
                return null;  // 提示玩家名
            }
            default -> {
                // 前两个参数已经输入完成，不继续提示
                return Collections.singletonList("");
            }
        }
    }
}
