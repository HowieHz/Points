package com.hzzz.points.commands;

import com.hzzz.points.Points;
import com.hzzz.points.text.text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.hzzz.points.Points.config;
import static com.hzzz.points.utils.Utils.checkPermission;

public final class PointsCommand implements CommandExecutor {
    private static final PointsCommand INSTANCE = new PointsCommand();

    public static PointsCommand getInstance() {
        return INSTANCE;
    }

    private PointsCommand() {}
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                // 权限检查
                if (config.getBoolean("points.reload.permission.enable", true)
                        && !checkPermission(sender,config.getString("points.reload.permission.node", "points.reload"))) {
                    sender.sendMessage(text.no_permission);
                    return true;
                }
                // 重载的逻辑
                Points.getInstance().onReload();

                // 发消息
                sender.sendMessage(text.reload_ready);
                if (sender instanceof Player) {  // 玩家重载 在控制台也输出重载结果
                    Points.logger.info(text.reload_ready);

                }
                return true;
            }

            // args[0].equals("help")
            sender.sendMessage(text.help);
            return true;
        } else {
            sender.sendMessage(text.help);
        }
        return true;
    }
}
