package com.hzzz.points.commands;

import java.lang.String;

import com.hzzz.points.text.text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.utils.Utils.checkPermission;
import static com.hzzz.points.Points.config;

public final class Where implements CommandExecutor {
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

                Player target_player = Bukkit.getPlayer(args[0]);

                if (target_player == null) {  // 检查是否获取到玩家
                    sender.sendMessage(text.no_player);
                    return true;
                }

                // 生成并发送消息给执行者
                sender.sendMessage(builderPlayerCoordinatesMessage("where", config, target_player));
                return true;
            }
            default -> {
                sender.sendMessage("使用方法: /where 玩家名 或 /where");
                return false;
            }
        }
    }
}
