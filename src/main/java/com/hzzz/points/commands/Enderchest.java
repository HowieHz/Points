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

import static com.hzzz.points.commands.utils.Utils.specialCheckPermission;

/**
 * <p>随身潜影箱</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @version 0.2.0
 * @since 2022-10-01 09:24
 */
public final class Enderchest implements TabExecutor {
    private static final Enderchest INSTANCE = new Enderchest();

    /**
     * 获取实例
     *
     * @return Instance of executor
     */
    public static Enderchest getInstance() {
        return INSTANCE;
    }

    private Enderchest() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // 检查执行者
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text.player_only);
            return true;
        }
        switch (args.length) {
            case 0 -> {
                // 权限检查
                if (!specialCheckPermission("enderchest",
                        sender,
                        "points.command.enderchest.self")) {
                    sender.sendMessage(text.no_permission);
                    return true;
                }

                // 开启此玩家的末影箱
                player.openInventory(player.getEnderChest());
                return true;
            }
            case 1 -> {
                // 权限检查
                if (!specialCheckPermission("enderchest",
                        sender,
                        "points.command.enderchest.other",
                        "points.command.enderchest.other.%s",
                        args[0])
                ) {
                    sender.sendMessage(text.no_permission);
                    return true;
                }

                Player target_player = Bukkit.getPlayerExact(args[0]);  // 使用玩家名获取

                if (target_player == null) {  // 检查是否获取到玩家
                    player.sendMessage(text.no_player);
                    return true;
                }

                player.openInventory(target_player.getEnderChest());  // 开启目标玩家的潜影箱
                return true;
            }
            default -> {
                sender.sendMessage(text.help_enderchest);
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
        /* enderchest
         * enderchest <player_name>
         */
        switch (args.length) {
            case 0, 1 -> {
                // 没有参数或者正在输入第一个参数（根指令后面只有一个空格（此时长度为0 /where ），或者第一个参数输入到一半（此时长度为一 /where Ho……））
                if (specialCheckPermission("enderchest",
                        sender,
                        "points.command.enderchest.other",
                        "points.command.enderchest.other.%s",
                        args[0])
                ) {
                    return null;  // 提示玩家名
                }
                return Collections.singletonList("");
            }
            default -> {
                // 前两个参数已经输入完成，不继续提示
                return Collections.singletonList("");
            }
        }
    }
}
