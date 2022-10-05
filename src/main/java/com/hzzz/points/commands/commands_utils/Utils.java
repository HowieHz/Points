package com.hzzz.points.commands.commands_utils;

import com.hzzz.points.Points;
import com.hzzz.points.utils.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static com.hzzz.points.utils.Utils.checkPermission;

/**
 * 指令执行器工具集
 */
public final class Utils {
    /**
     * 工具类禁止实例化
     */
    private Utils() {
        throw new IllegalStateException("工具类");
    }

    /**
     * 生成一条消息 用于指示位置
     *
     * @param config_root     配置文件根节点
     * @param target_player   目标玩家对象
     * @param separator       分隔符
     * @param separator_color 分隔符颜色
     * @return 生成的消息
     */
    public static Component builderPlayerCoordinatesMessage(String config_root, Player target_player, String separator, NamedTextColor separator_color) {
        Location player_location = target_player.getLocation();  // 获取位置
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件

        // 编辑消息
        Component component = Component.text("")
                .append(Component.text(target_player.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(separator).color(separator_color))
                .append(Component.text(target_player.getWorld().getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(String.format(Text.getCoordinatesFormat(), player_location.getX(), player_location.getY(), player_location.getZ())).color(NamedTextColor.YELLOW));

        // 根据配置文件在末尾追加一些信息
        if (config.getBoolean(String.format("%s.voxelmap-support", config_root), false)) {
            component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text(Text.getVoxelmapSupportHover())))
                    .clickEvent(ClickEvent.suggestCommand(String.format(Text.getVoxelmapSupportCommand(), player_location.getX(), player_location.getY(), player_location.getZ(), target_player.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.xaeros-support", config_root), false)) {
            component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(Component.text(Text.getXaerosSupportHover())))
                    .clickEvent(ClickEvent.suggestCommand(String.format(Text.getXaerosSupportCommand(), target_player.getName(), target_player.getName().charAt(0), player_location.getX(), player_location.getY(), player_location.getZ(), target_player.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.teleport-support", config_root), false)) {
            component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                    .append(Component.text("[tp] ").color(NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(Component.text(String.format(Text.getTeleportSupportHover(), player_location.getX(), player_location.getY(), player_location.getZ()))))
                            .clickEvent(ClickEvent.suggestCommand(String.format(Text.getTeleportSupportCommand(), player_location.getX(), player_location.getY(), player_location.getZ()))));
        }

        return component;
    }

    /**
     * 生成一条消息 用于指示位置<br>
     * 白色箭头分隔符
     *
     * @param config_root   配置文件根节点
     * @param target_player 目标玩家对象
     * @return 生成的消息
     */
    public static Component builderPlayerCoordinatesMessage(String config_root, Player target_player) {
        return builderPlayerCoordinatesMessage(config_root, target_player, " -> ", NamedTextColor.WHITE);
    }

    /**
     * 检查一段字符串末尾是否是指定字符串(通配符检查)<br>如果是就格式化，不是就使用默认字符串进行格式化
     *
     * @param string         一段字符串
     * @param end_string     检查结尾是否是此字符串
     * @param default_string 默认字符串
     * @param args           格式化参数
     * @return 格式化完毕之后的字符串
     */
    public static String stringFormatEnd(String string, String end_string, String default_string, Object... args) {
        if (string == null) {
            return String.format(default_string, args);
        }

        if (string.endsWith(end_string)) {
            return String.format(string, args);
        } else {
            return String.format(default_string, args);
        }
    }

    /**
     * 特殊的权限检查<br>指令目标为指定玩家<br>(enable节点如读取失败默认为true)
     * 特殊的权限检查<br>
     * 检查 config_root.permission.other.enable <br>
     * 读取配置文件 config_root.permission.other.node-other-player 作为权限节点<br>
     * 读取配置文件 config_root.permission.other.node-target-player 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param config_root                配置文件根节点 要求此节点下一节点为permission
     * @param sender                     发送者(被进行权限检查的对象)
     * @param default_other_player_node  默认权限节点(其他玩家)(文件读取失败的使用值)
     * @param default_target_player_node 默认权限节点(指定玩家)(要求结尾为%s)(文件读取失败的使用值)
     * @param target_player_name         目标玩家(用于权限检查)
     * @return 是否通过权限检查 (通过为true)
     */
    public static boolean specialCheckPermission(String config_root,
                                                 CommandSender sender,
                                                 String default_other_player_node,
                                                 String default_target_player_node,
                                                 String target_player_name) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        return !((// 检查是否启用了权限检查
                config.getBoolean(String.format("%s.permission.other.enable", config_root), true))
                && !(checkPermission(sender, config.getString(String.format("%s.permission.other.node-other-player", config_root), default_other_player_node))  // 玩家权限检查 目标为其他玩家
                || checkPermission(sender,
                stringFormatEnd(config.getString(String.format("%s.permission.other.node-target-player", config_root)),
                        "%s", default_target_player_node, target_player_name))));  // 玩家权限检查 目标为指定玩家
    }

    /**
     * 特殊的权限检查<br>
     * 检查config_root.permission.config_middle_node.enable<br>
     * 读取配置文件 config_root.permission.config_middle_node.node 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param config_root             配置文件根节点 要求此节点下一节点为permission
     * @param sender                  发送者(被进行权限检查的对象)
     * @param default_permission_node 默认权限节点(文件读取失败的使用值)
     * @param config_middle_node      配置文件中间节点
     * @return 是否通过权限检查 (通过为true)
     */
    public static boolean specialCheckPermission(String config_root,
                                                 CommandSender sender,
                                                 String default_permission_node,
                                                 String config_middle_node) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        return !(config.getBoolean(String.format("%s.permission.%s.enable", config_root, config_middle_node), true)  // 子项权限管理
                && !checkPermission(sender, config.getString(String.format("%s.permission.%s.node", config_root, config_middle_node),   // 玩家权限检查
                default_permission_node)));
    }

    /**
     * 特殊的权限检查<br>
     * 检查 config_root.permission.self.enable<br>
     * 读取配置文件 config_root.permission.self.node 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param config_root       配置文件根节点 要求此节点下一节点为permission
     * @param sender            发送者(被进行权限检查的对象)
     * @param default_self_node 默认权限节点(目标为自己)(文件读取失败的使用值)
     * @return 是否通过权限检查 (通过为true)
     */
    public static boolean specialCheckPermission(String config_root,
                                                 CommandSender sender,
                                                 String default_self_node) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        return !(config.getBoolean(String.format("%s.permission.self.enable", config_root), true)  // 子项权限管理
                && !checkPermission(sender, config.getString(String.format("%s.permission.self.node", config_root),   // 玩家权限检查
                default_self_node)));
    }

    /**
     * 普通的权限检查<br>
     * 检查 config_root.permission.enable<br>
     * 读取配置文件 config_root.permission.node 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param config_root  配置文件根节点 要求此节点下一节点为permission permission.node permission.enable
     * @param sender       发送者(被进行权限检查的对象)
     * @param default_node 默认权限节点(文件读取失败的使用值)
     * @return 是否通过权限检查 (通过为true)
     */
    public static boolean commonCheckPermission(String config_root,
                                                CommandSender sender,
                                                String default_node) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        return !(config.getBoolean(String.format("%s.permission.enable", config_root), true)  // 总项权限管理
                && !checkPermission(sender, config.getString(String.format("%s.permission.node", config_root), default_node)));
    }
}
