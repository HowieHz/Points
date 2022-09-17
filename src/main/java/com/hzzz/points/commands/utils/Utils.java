package com.hzzz.points.commands.utils;

import com.hzzz.points.text.text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Utils {
    public static Component builderPlayerCoordinatesMessage(String config_root, FileConfiguration config, Player player){
        Location player_location = player.getLocation();  // 获取位置

        // 编辑消息
        Component component = Component.text("")
                .append(Component.text(player.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(" -> ").color(NamedTextColor.WHITE))
                .append(Component.text(player.getWorld().getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(String.format(text.coordinates_format, player_location.getX(), player_location.getY(), player_location.getZ())).color(NamedTextColor.YELLOW));

        if (config.getBoolean(String.format("%s.voxelmap-support",config_root), false)) {
            component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text(text.voxelmap_support_hover)))
                    .clickEvent(ClickEvent.suggestCommand(String.format(text.voxelmap_support_command, player_location.getX(), player_location.getY(), player_location.getZ(), player.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.xaeros-support",config_root), false)) {
            component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(Component.text(text.xaeros_support_hover)))
                    .clickEvent(ClickEvent.suggestCommand(String.format(text.xaeros_support_command, player.getName(), player.getName().charAt(0), player_location.getX(), player_location.getY(), player_location.getZ(), player.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.teleport-support",config_root), false)) {
            component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                    .append(Component.text("[tp] ").color(NamedTextColor.RED)
                    .hoverEvent(HoverEvent.showText(Component.text(String.format(text.teleport_support_hover, player_location.getX(), player_location.getY(), player_location.getZ()))))
                    .clickEvent(ClickEvent.suggestCommand(String.format(text.teleport_support_command, player_location.getX(), player_location.getY(), player_location.getZ()))));
        }
        return component;
    }
}
