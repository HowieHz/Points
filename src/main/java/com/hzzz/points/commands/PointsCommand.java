package com.hzzz.points.commands;

import com.hzzz.points.Points;
import com.hzzz.points.text.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hzzz.points.commands.commands_utils.Utils.commonCheckPermission;
import static com.hzzz.points.utils.Utils.logInfo;

/**
 * points指令的执行器以及tab补全
 */
public final class PointsCommand implements TabExecutor {
    private static final PointsCommand INSTANCE = new PointsCommand();

    /**
     * 获取实例
     *
     * @return Instance of executor
     */
    public static PointsCommand getInstance() {
        return INSTANCE;
    }

    /**
     * 单例 无参数
     */
    private PointsCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                // 权限检查
                if (!commonCheckPermission("points.reload", sender, "points.reload")) {
                    sender.sendMessage(Text.getNoPermission());
                    return true;
                }
                // 重载的逻辑
                Points.getInstance().onReload();

                // 发消息
                sender.sendMessage(Text.getReloadReady());
                if (sender instanceof Player) {  // 玩家重载 在控制台也输出重载结果
                    logInfo(Text.getReloadReady());

                }
                return true;
            }

            // args[0].equals("help")
            sender.sendMessage(Text.getHelpPoints());
            return true;
        } else {
            sender.sendMessage(Text.getHelpPoints());
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
                if (commonCheckPermission("points.reload", sender, "points.reload")) {
                    return Arrays.asList("help", "reload");
                }
                return Collections.singletonList("help");
            }
            default -> {
                // 前一个参数已经输入完成，不继续提示
                return Collections.singletonList("");
            }
        }
    }
}
