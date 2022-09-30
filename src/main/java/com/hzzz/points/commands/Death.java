package com.hzzz.points.commands;

import com.hzzz.points.text.text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static com.hzzz.points.Points.config;
import static com.hzzz.points.data_manager.operations_set.DeathLog.outputDeathLog;
import static com.hzzz.points.data_manager.operations_set.DeathMessageConfig.updateDeathMessageConfig;
import static com.hzzz.points.utils.Utils.checkPermission;

/**
 * death指令的执行器以及tab补全
 */
public final class Death implements TabExecutor {
    private static final Death INSTANCE = new Death();

    private static final HashMap<UUID, Long> last_success_get_death_log_timestamp = new HashMap<>();  // 储存玩家上次成功使用 death log的时间戳 用于限制玩家使用频率

    /**
     * 获取实例
     *
     * @return Instance of executor
     */
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
                        sender.sendMessage(text.help_death);
                        return true;
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

                        outputDeathLog(player, player);  // 查看自己的log

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

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* death         *  message
         * death log         *  <player_name>
         */
        switch (args.length) {
            case 0, 1 -> {
                // 没有参数或者正在输入第一个参数（根指令后面只有一个空格（此时长度为0 /death ），或者第一个参数输入到一半（此时长度为一 /death lo……））
                return Arrays.asList("message", "log");
            }
            case 2 -> {
                // 正在输入第二个参数（第二个参数输入一半（/death log Ho……））
                if (args[0].equals("message")) {
                    // 不继续提示
                    return Collections.singletonList("");
                }
                return null;  // death log Ho……提示玩家名
            }
            default -> {
                // 前两个参数已经输入完成，不继续提示
                return Collections.singletonList("");
            }
        }
    }
}
