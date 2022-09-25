package com.hzzz.points.commands;

import com.hzzz.points.text.text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import static com.hzzz.points.data_manager.operations_set.DeathLog.outputDeathLog;
import static com.hzzz.points.utils.Utils.checkPermission;
import static com.hzzz.points.data_manager.operations_set.DeathMessageConfig.updateDeathMessageConfig;
import static com.hzzz.points.Points.config;

public final class Death implements CommandExecutor {
    private static final Death INSTANCE = new Death();

    private static final HashMap<UUID, Long> last_success_get_death_log_timestamp = new HashMap<>();  // 储存玩家上次成功使用 death log的时间戳 用于限制玩家使用频率

    public static Death getInstance() {
        return INSTANCE;
    }

    private Death() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            // /death
            sender.sendMessage(text.help_death);
            return true;
        }

        switch (args[0]) {
            case "message" -> {
                if (config.getBoolean("death.message.enable", false)) {  // 检查子模块是否开启
                    // 检查执行者
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(text.player_only);
                        return true;
                    }
                    // 权限检查
                    if (config.getBoolean("death.message.command-permission.enable", false)
                            && !checkPermission(sender, config.getString("death.message.command-permission.node", "points.command.death.message"))) {
                        sender.sendMessage(text.no_permission);
                        return true;
                    }
                    if (args.length > 1) {  // 参数过多语法错误
                        return false;
                    }

                    if (updateDeathMessageConfig(player)) {  // 更改数据库config
                        sender.sendMessage(text.enable_death_message);
                    } else {
                        sender.sendMessage(text.disable_death_message);
                    }

                } else {
                    sender.sendMessage(text.disable_module);
                }
            }
            case "log" -> {
                if (config.getBoolean("death.log.enable", false)) {  // 检查子模块是否开启
                    if (args.length == 1) {  // /death log
                        // 权限检查
                        if (config.getBoolean("death.log.permission.enable", false)
                                && !checkPermission(sender, config.getString("death.log.permission.node.self", "points.command.death.log.self"))) {
                            sender.sendMessage(text.no_permission);
                            return true;
                        }

                        // 检查执行者
                        if (!(sender instanceof Player player)) {
                            sender.sendMessage(text.player_only);
                            return true;
                        }

                        // 使用频率检查
                        if (config.getBoolean("death.log.command.frequency-limit.enable", false)) {
                            if (last_success_get_death_log_timestamp.containsKey(player.getUniqueId())) {  // 检查是否有记录
                                if ((System.currentTimeMillis() - last_success_get_death_log_timestamp.get(player.getUniqueId()))
                                        < (config.getInt("death.log.command.frequency-limit.second", 1)
                                        / config.getInt("death.log.command.frequency-limit.maximum-usage", 1) * 1000L)) {
                                    sender.sendMessage(text.command_frequency_limit);
                                    return true;
                                } else {  // 更新
                                    last_success_get_death_log_timestamp.put(player.getUniqueId(), System.currentTimeMillis());
                                }
                            } else {  // 初始化
                                last_success_get_death_log_timestamp.put(player.getUniqueId(), System.currentTimeMillis());
                            }
                        }

                        outputDeathLog(player.getName(), player);  // 查看自己的log

                    } else {  // /death log Howie_HzGo
                        // 权限检查
                        if (config.getBoolean("death.log.permission.enable", false)
                                && !checkPermission(sender, config.getString("death.log.permission.node.other", "points.command.death.log.other"))
                                && !checkPermission(sender, String.format(config.getString("death.log.permission.node.player", "points.command.death.log.%s"), args[1]))) {
                            sender.sendMessage(text.no_permission);
                            return true;
                        }

                        // 检查执行者 是玩家就进行频率检查
                        if (sender instanceof Player player) {
                            if (config.getBoolean("death.log.command.frequency-limit.enable", false)) {
                                if (last_success_get_death_log_timestamp.containsKey(player.getUniqueId())) {  // 检查是否有记录
                                    if ((System.currentTimeMillis() - last_success_get_death_log_timestamp.get(player.getUniqueId()))
                                            < (config.getInt("death.log.command.frequency-limit.second", 1)
                                            / config.getInt("death.log.command.frequency-limit.maximum-usage", 1) * 1000L)) {
                                        sender.sendMessage(text.command_frequency_limit);
                                        return true;
                                    } else {  // 更新
                                        last_success_get_death_log_timestamp.put(player.getUniqueId(), System.currentTimeMillis());
                                    }
                                } else {  // 初始化
                                    last_success_get_death_log_timestamp.put(player.getUniqueId(), System.currentTimeMillis());
                                }
                            }
                        }

                        outputDeathLog(args[1], sender);  // 查看玩家的log
                    }

                } else {
                    sender.sendMessage(text.disable_module);
                }
            }
            default -> sender.sendMessage(text.help_death);
        }
        return true;
    }
}
