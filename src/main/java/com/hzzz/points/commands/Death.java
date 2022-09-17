package com.hzzz.points.commands;

import com.hzzz.points.Points;
import com.hzzz.points.text.text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class Death implements CommandExecutor {
    private static final Death instance = new Death();

    public static Death getInstance() {
        return instance;
    }

    private Death() {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final FileConfiguration config = Points.config;  // 读取配置

        switch (args.length) {
            case 0 -> {
                // /death
                sender.sendMessage(text.help_death);
                return true;
            }
            case 1 -> {
                // 检查执行者
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(text.player_only);
                    return true;
                }
                // 权限检查
                return true;
            }
        }
        return true;
    }
}
