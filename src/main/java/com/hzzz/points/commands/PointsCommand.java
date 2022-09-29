package com.hzzz.points.commands;

import com.hzzz.points.Points;
import com.hzzz.points.text.text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hzzz.points.Points.config;
import static com.hzzz.points.utils.Utils.checkPermission;
import static com.hzzz.points.utils.Utils.logInfo;

public final class PointsCommand implements TabExecutor {
    private static final PointsCommand INSTANCE = new PointsCommand();

    public static PointsCommand getInstance() {
        return INSTANCE;
    }

    private PointsCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                // 权限检查
                if (config.getBoolean("points.reload.permission.enable", true)
                        && !checkPermission(sender, config.getString("points.reload.permission.node", "points.reload"))) {
                    sender.sendMessage(text.no_permission);
                    return true;
                }
                // 重载的逻辑
                Points.getInstance().onReload();

                // 发消息
                sender.sendMessage(text.reload_ready);
                if (sender instanceof Player) {  // 玩家重载 在控制台也输出重载结果
                    logInfo(text.reload_ready);

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

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* points
         * points help
         * points reload
         */
        switch (args.length) {
            case 0, 1 -> {
                // 没有参数或者正在输入第一个参数（根指令后面只有一个空格（此时长度为0 /points ），或者第一个参数输入到一半（此时长度为一 /points he……））
                return Arrays.asList("help", "reload");
            }
            default -> {
                // 前一个参数已经输入完成，不继续提示
                return Collections.singletonList("");
            }
        }
    }
}
