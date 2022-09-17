package com.hzzz.points.commands;

import com.hzzz.points.Points;
import com.hzzz.points.text.text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class PointsCommand implements CommandExecutor {
//    private final FileConfiguration config;
    private static PointsCommand instance;

    public static PointsCommand getInstance() {
        if (instance == null) {
            instance = new PointsCommand();
        }
        return instance;
    }

    private PointsCommand() {
//        config = Points.config;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("help")) {
                sender.sendMessage(text.help);
                return true;
            }
            if (args[0].equals("reload")) {
                // 权限检查
                if (!sender.hasPermission("points.reload")) {
                    sender.sendMessage(text.no_permission);
                    return true;
                }
                // 重载的逻辑
                Points plugin_instance = Points.getInstance();
                plugin_instance.onReload();
                sender.sendMessage(text.reload_ready);
                return true;
            }
            sender.sendMessage(text.help);
            return true;
        } else {
            sender.sendMessage(text.help);
        }
        return true;
    }
}
