package com.hzzz.points.commands;

import java.lang.String;

import com.hzzz.points.Points;
import com.hzzz.points.text.text;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static com.hzzz.points.commands.utils.Utils.builderPlayerCoordinatesMessage;
import static com.hzzz.points.commands.utils.Utils.checkPermission;

public final class Here implements CommandExecutor {
    private static final Here instance = new Here();

    public static Here getInstance() {
        return instance;
    }

    private Here() {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        final FileConfiguration config = Points.config;  // 读取配置

        // 检查执行者
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text.player_only);
            return true;
        }

        // 权限检查
        if (config.getBoolean("here.permission", false) && !checkPermission(sender, "points.here")) {
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
}