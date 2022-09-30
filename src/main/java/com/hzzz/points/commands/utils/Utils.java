package com.hzzz.points.commands.utils;

import com.hzzz.points.text.text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * 指令执行器工具集
 */
public final class Utils {
    /**
     * 生成一条消息 用于指示位置
     *
     * @param config_root     配置文件根节点
     * @param config          配置文件实例
     * @param target_player   目标玩家对象
     * @param separator       分隔符
     * @param separator_color 分隔符颜色
     * @return 生成的消息
     */
    public static Component builderPlayerCoordinatesMessage(String config_root, FileConfiguration config, Player target_player, String separator, NamedTextColor separator_color) {
        Location player_location = target_player.getLocation();  // 获取位置

        // 编辑消息
        Component component = Component.text("")
                .append(Component.text(target_player.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(separator).color(separator_color))
                .append(Component.text(target_player.getWorld().getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(String.format(text.coordinates_format, player_location.getX(), player_location.getY(), player_location.getZ())).color(NamedTextColor.YELLOW));

        if (config.getBoolean(String.format("%s.voxelmap-support", config_root), false)) {
            component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text(text.voxelmap_support_hover)))
                    .clickEvent(ClickEvent.suggestCommand(String.format(text.voxelmap_support_command, player_location.getX(), player_location.getY(), player_location.getZ(), target_player.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.xaeros-support", config_root), false)) {
            component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(Component.text(text.xaeros_support_hover)))
                    .clickEvent(ClickEvent.suggestCommand(String.format(text.xaeros_support_command, target_player.getName(), target_player.getName().charAt(0), player_location.getX(), player_location.getY(), player_location.getZ(), target_player.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.teleport-support", config_root), false)) {
            component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                    .append(Component.text("[tp] ").color(NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(Component.text(String.format(text.teleport_support_hover, player_location.getX(), player_location.getY(), player_location.getZ()))))
                            .clickEvent(ClickEvent.suggestCommand(String.format(text.teleport_support_command, player_location.getX(), player_location.getY(), player_location.getZ()))));
        }
        return component;
    }

    /**
     * 生成一条消息 用于指示位置
     *
     * @param config_root   配置文件根节点
     * @param config        配置文件实例
     * @param target_player 目标玩家对象
     * @return 生成的消息
     */
    public static Component builderPlayerCoordinatesMessage(String config_root, FileConfiguration config, Player target_player) {
        Location player_location = target_player.getLocation();  // 获取位置

        // 编辑消息
        Component component = Component.text("")
                .append(Component.text(target_player.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(" -> ").color(NamedTextColor.WHITE))
                .append(Component.text(target_player.getWorld().getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(String.format(text.coordinates_format, player_location.getX(), player_location.getY(), player_location.getZ())).color(NamedTextColor.YELLOW));

        if (config.getBoolean(String.format("%s.voxelmap-support", config_root), false)) {
            component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text(text.voxelmap_support_hover)))
                    .clickEvent(ClickEvent.suggestCommand(String.format(text.voxelmap_support_command, player_location.getX(), player_location.getY(), player_location.getZ(), target_player.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.xaeros-support", config_root), false)) {
            component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(Component.text(text.xaeros_support_hover)))
                    .clickEvent(ClickEvent.suggestCommand(String.format(text.xaeros_support_command, target_player.getName(), target_player.getName().charAt(0), player_location.getX(), player_location.getY(), player_location.getZ(), target_player.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.teleport-support", config_root), false)) {
            component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                    .append(Component.text("[tp] ").color(NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(Component.text(String.format(text.teleport_support_hover, player_location.getX(), player_location.getY(), player_location.getZ()))))
                            .clickEvent(ClickEvent.suggestCommand(String.format(text.teleport_support_command, player_location.getX(), player_location.getY(), player_location.getZ()))));
        }
        return component;
    }
}
