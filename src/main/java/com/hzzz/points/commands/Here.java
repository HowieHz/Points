package com.hzzz.points.commands;

import com.hzzz.points.text.text;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

import static com.hzzz.points.Points.config;
import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.utils.Utils.checkPermission;

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

    private Here() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // 检查执行者
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text.player_only);
            return true;
        }

        // 权限检查
        if (config.getBoolean("here.permission.enable", false)
                && !checkPermission(sender, config.getString("here.permission.node", "points.command.here"))) {
            sender.sendMessage(text.no_permission);
            return true;
        }

        // 生成消息并在在公屏发送
        Bukkit.broadcast(builderPlayerCoordinatesMessage("here", config, player), Server.BROADCAST_CHANNEL_USERS);

        // 给发送者附上发光效果
        if (config.getBoolean("here.glowing.enable", false)) {
            PotionEffect pe = new PotionEffect(PotionEffectType.GLOWING, config.getInt("here.glowing.time", 1200) * 20, 1);  // 20tick*60s=1200
            pe.apply(player);
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
        /* here
         */
        // 不提示
        return Collections.singletonList("");
    }
}