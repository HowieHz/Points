package com.hzzz.points.commands;

import com.hzzz.points.Points;
import com.hzzz.points.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static com.hzzz.points.commands.commands_utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.commands.commands_utils.Utils.specialCheckPermission;

/**
 * here指令的执行器以及tab补全
 */
public final class Here implements TabExecutor {
    private static final Here INSTANCE = new Here();

    /**
     * 获取实例
     *
     * @return Instance of executor
     */
    public static Here getInstance() {
        return INSTANCE;
    }

    /**
     * 单例 无参数
     */
    private Here() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件

        // 检查执行者
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Text.getPlayerOnly());
            return true;
        }

        // 权限检查
        if (!specialCheckPermission("here", player, "points.command.here")) {
            sender.sendMessage(Text.getNoPermission());
            return true;
        }

        // 生成消息并在在公屏发送
        Bukkit.broadcast(builderPlayerCoordinatesMessage("here", player), Server.BROADCAST_CHANNEL_USERS);

        // 给发送者附上发光效果
        if (config.getBoolean("here.glowing.enable", false)) {
            PotionEffect pe = new PotionEffect(PotionEffectType.GLOWING, config.getInt("here.glowing.time", 1200) * 20, 1);  // 20tick*60s=1200
            pe.apply(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* here
         */
        // 不提示
        return Collections.singletonList("");
    }
}