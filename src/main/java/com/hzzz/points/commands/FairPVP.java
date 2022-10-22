package com.hzzz.points.commands;

import com.hzzz.points.Points;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hzzz.points.commands.commands_utils.Utils.specialCheckPermission;
import static com.hzzz.points.utils.Utils.executeCommand;
import static com.hzzz.points.utils.message.Lang.getMessage;
import static com.hzzz.points.utils.message.MsgKey.*;

/**
 * <p>fair pvp</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @version 0.2.4
 * @since 2022-10-22 22:26
 */
public class FairPVP implements TabExecutor {
    private static final String PERMISSION_PARENT_NODE = "fair-pvp";
    private static final FairPVP instance = new FairPVP();

    private final String[] words = {
            "health", "strength", "regeneration", "luck", "wisdom", "toughness"
    };

    /**
     * 获取实例
     *
     * @return Instance of executor
     */
    public static FairPVP getInstance() {
        return instance;
    }

    /**
     * 单例 无参数
     */
    private FairPVP() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        switch (args.length) {
            case 1 -> {
                // 检查执行者
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(getMessage(PLAYER_ONLY));
                    return true;
                }
                // 权限检查
                if (!specialCheckPermission(PERMISSION_PARENT_NODE,
                        sender,
                        "points.command.fair-pvp.self")) {
                    sender.sendMessage(getMessage(NO_PERMISSION));
                    return true;
                }
                if (args[0].equals("on")) {
                    for (String word : words) {
                        executeCommand(PlaceholderAPI.setPlaceholders(player.getPlayer(), "sk modifier add %player_name% " + word + " fair_pvp_" + word + " -%aureliumskills_" + word + "%"));
                    }
                    player.sendMessage(getMessage(ENABLE_FAIR_PVP));
                } else if (args[0].equals("off")) {
                    for (String word : words) {
                        executeCommand(PlaceholderAPI.setPlaceholders(player.getPlayer(), "sk modifier remove %player_name%  fair_pvp_" + word));
                    }
                    player.sendMessage(getMessage(DISABLE_FAIR_PVP));
                } else {
                    sender.sendMessage(getMessage(HELP_FAIR_PVP));
                }
                return true;
            }
            case 2 -> {
                // 权限检查
                if (!specialCheckPermission(PERMISSION_PARENT_NODE,
                        sender,
                        "points.command.fair-pvp.other",
                        "points.command.fair-pvp.other.%s",
                        args[1])
                ) {
                    sender.sendMessage(getMessage(NO_PERMISSION));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayerExact(args[1]);  // 使用玩家名获取

                if (targetPlayer == null) {  // 检查是否获取到玩家
                    sender.sendMessage(getMessage(PLAYER_NOT_ONLINE));
                    return true;
                }

                if (args[0].equals("on")) {
                    for (String word : words) {
                        executeCommand(PlaceholderAPI.setPlaceholders(targetPlayer.getPlayer(), "sk modifier add %player_name% " + word + " fair_pvp_" + word + " -%aureliumskills_" + word + "%"));
                    }
                    targetPlayer.sendMessage(getMessage(ENABLE_FAIR_PVP));
                    sender.sendMessage(targetPlayer.getName() + " " + getMessage(ENABLE_FAIR_PVP));
                } else if (args[0].equals("off")) {
                    for (String word : words) {
                        executeCommand(PlaceholderAPI.setPlaceholders(targetPlayer.getPlayer(), "sk modifier remove %player_name%  fair_pvp_" + word));
                    }
                    targetPlayer.sendMessage(getMessage(DISABLE_FAIR_PVP));
                    sender.sendMessage(targetPlayer.getName() + " " + getMessage(DISABLE_FAIR_PVP));
                } else {
                    sender.sendMessage(getMessage(HELP_FAIR_PVP));
                }

                return true;
            }
            default -> {
                sender.sendMessage(getMessage(HELP_FAIR_PVP));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* fair-pvp [on, off]
         * fair-pvp [on, off] <player_name>
         */
        if (args.length == 0 || args.length == 1) {
            // 没有参数或者正在输入第一个参数（根指令后面只有一个空格（此时长度为0 /points ），或者第一个参数输入到一半（此时长度为一 /points o……））
            if (specialCheckPermission("fair-pvp",
                    sender,
                    "points.command.fair-pvp.self")
                    || specialCheckPermission("fair-pvp",
                    sender,
                    "points.command.fair-pvp.other",
                    "other")) {
                return Arrays.asList("on", "off");
            }
        } else if (args.length == 2) {
            if (specialCheckPermission("fair-pvp",
                    sender,
                    "points.command.fair-pvp.other",
                    "points.command.fair-pvp.other.%s",
                    args[1])) {
                // 过权限检查
                return null;  // death log Ho……提示玩家名
            }
        }
        return Collections.singletonList("");
    }
}