package com.hzzz.points.commands;

import com.hzzz.points.Points;
import com.hzzz.points.text.text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.hzzz.points.commands.utils.Utils.checkPermission;
import com.hzzz.points.data_manager.DeathSQLite;

public final class Death implements CommandExecutor {
    private static final Death INSTANCE = new Death();

    public static Death getInstance() {
        return INSTANCE;
    }

    private Death() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final FileConfiguration config = Points.config;  // 读取配置

        if (args.length == 0) {
            // /death
            sender.sendMessage(text.help_death);
            return true;
        }

        // 检查执行者
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text.player_only);
            return true;
        }

        switch (args[0]) {
            case "message" -> {
                if (config.getBoolean("death.message.enable", false)) {  // 检查子模块是否开启
                    // 权限检查
                    if (config.getBoolean("death.message.permission", false) && !checkPermission(sender, "points.death.message")) {
                        sender.sendMessage(text.no_permission);
                        return true;
                    }
                    if (args.length > 1) {  // 参数过多语法错误
                        return false;
                    }

                    if (DeathSQLite.getInstance().updateDeathMessageConfig(player)) {  // 更改数据库config
                        sender.sendMessage(text.enable_death_message);
                    } else {
                        sender.sendMessage(text.disable_death_message);
                    }

                }else{
                    sender.sendMessage(text.disable_module);
                }
            }
            case "log" -> {
                if (config.getBoolean("death.log.enable", false)) {  // 检查子模块是否开启
                    if (args.length == 1) {  // /death log
                        // 权限检查
                        if (config.getBoolean("death.log.permission", false) && !checkPermission(sender, "points.death.log.self")) {
                            sender.sendMessage(text.no_permission);
                            return true;
                        }
                        // TODO 查看自己的log

                    }else {  // /death log Howie_HzGo
                        // TODO 查看玩家的log
                    }

                }else{
                    sender.sendMessage(text.disable_module);
                }
            }
            default -> sender.sendMessage(text.help_death);
        }
        return true;
    }
}
