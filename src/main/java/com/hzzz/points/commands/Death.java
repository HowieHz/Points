package com.hzzz.points.commands;

import com.hzzz.points.text.text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.SQLException;
import java.util.*;

import static com.hzzz.points.Points.config;
import static com.hzzz.points.commands.utils.Utils.specialCheckPermission;
import static com.hzzz.points.data_manager.operations_set.DeathLog.outputDeathLog;
import static com.hzzz.points.data_manager.operations_set.DeathMessageConfig.updateDeathMessageConfig;
import static com.hzzz.points.utils.Utils.*;

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

                    try {
                        if (updateDeathMessageConfig(player)) {  // 更改数据库config
                            sender.sendMessage(text.enable_death_message);
                        } else {
                            sender.sendMessage(text.disable_death_message);
                        }
                    } catch (SQLException e) {
                        sender.sendMessage(text.database_error);
                        sender.sendMessage(text.disable_death_message);
                        logError(text.database_error);
                        e.printStackTrace();
                    }

                } else {
                    sender.sendMessage(text.disable_module);
                }
            }
            case "log" -> {
                if (config.getBoolean("death.log.enable", false)) {  // 检查子模块是否开启
                    if (args.length == 1) {  // /death log
                        // 权限检查
                        if (!specialCheckPermission("death.log",
                                sender,
                                "points.command.death.log.self")) {
                            sender.sendMessage(text.no_permission);
                            return true;
                        }

                        // 检查执行者
                        if (!(sender instanceof Player player)) {
                            sender.sendMessage(text.player_only);
                            return true;
                        }

                        // 使用频率检查
                        if (checkCommandFrequencyLimit(player)) {
                            player.sendMessage(text.command_frequency_limit);
                            return true;
                        }

                        outputDeathLog(player, player);  // 查看自己的log

                    } else {  // /death log Howie_HzGo
                        // 权限检查
                        if (!specialCheckPermission("death.log",
                                sender,
                                "points.command.death.log.other",
                                "points.command.death.log.other.%s",
                                args[1])
                        ) {
                            sender.sendMessage(text.no_permission);
                            return true;
                        }

                        // 检查执行者 是玩家就进行频率检查
                        if (sender instanceof Player player) {
                            if (checkCommandFrequencyLimit(player)) {
                                player.sendMessage(text.command_frequency_limit);
                                return true;
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

    /**
     * 检查指令使用频率是否超过上限
     *
     * @param player 使用该指令的玩家
     * @return 超限返回true
     */
    private static boolean checkCommandFrequencyLimit(Player player) {
        if (config.getBoolean("death.log.command.frequency-limit.enable", false)) {
            if (last_success_get_death_log_timestamp.containsKey(player.getUniqueId())) {  // 检查是否有记录
                if ((System.currentTimeMillis() - last_success_get_death_log_timestamp.get(player.getUniqueId()))
                        < (config.getInt("death.log.command.frequency-limit.second", 1)
                        / config.getInt("death.log.command.frequency-limit.maximum-usage", 1) * 1000L)) {
                    return true;
                } else {  // 更新
                    last_success_get_death_log_timestamp.put(player.getUniqueId(), System.currentTimeMillis());
                }
            } else {  // 初始化
                last_success_get_death_log_timestamp.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* death message
         * death log <player_name>
         */
        // 检查模块 - 检查权限
        switch (args.length) {
            case 0, 1 -> {
                // 没有参数或者正在输入第一个参数（根指令后面只有一个空格（此时长度为0 /death ），或者第一个参数输入到一半（此时长度为一 /death lo……））
                // 交叉检测，开启哪个模块有哪个模块的补全提示
                // 因为没有第二个参数，所以没法检测 points.command.death.log.other.玩家名 的权限
                List<String> complete_arrays = new ArrayList<>();
                if (config.getBoolean("death.message.enable", false)) {
                    complete_arrays.add("message");
                }
                if (config.getBoolean("death.log.enable", false)) {
                    if (specialCheckPermission("death.log",
                            sender,
                            "points.command.death.log.self")
                            || specialCheckPermission("death.log",
                            sender,
                            "points.command.death.log.other",
                            "other")) {
                        complete_arrays.add("log");
                    }
                }
                return complete_arrays;
            }
            case 2 -> {
                // 正在输入第二个参数（第二个参数输入一半（/death log Ho……））
                if ("log".equals(args[0])) {
                    if (config.getBoolean("death.log.enable", false)) {  // 是否开启模块
                        if (specialCheckPermission("death.log",
                                sender,
                                "points.command.death.log.other",
                                "points.command.death.log.other.%s",
                                args[1])) {
                            // 过权限检查
                            return null;  // death log Ho……提示玩家名
                        }
                        // 没权限 不继续提示
                        return Collections.singletonList("");
                    }
                    // 没开启模块 不继续提示
                    return Collections.singletonList("");
                } // 第一个参数是message或者其他什么奇奇怪怪的东西 不提示
                return Collections.singletonList("");
            }
            default -> {
                // 前两个参数已经输入完成，不继续提示
                return Collections.singletonList("");
            }
        }
    }
}
