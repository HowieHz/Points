package com.hzzz.points.commands.commands_utils;

import com.hzzz.points.Points;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static com.hzzz.points.utils.Text.getMessage;
import static com.hzzz.points.utils.Utils.checkPermission;
import static com.hzzz.points.utils.msgKey.*;

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
     * @param configRoot     配置文件根节点
     * @param targetPlayer   目标玩家对象
     * @param separator       分隔符
     * @param separatorColor 分隔符颜色
     * @return 生成的消息
     */
    public static Component builderPlayerCoordinatesMessage(String configRoot, Player targetPlayer, String separator, NamedTextColor separatorColor) {
        Location playerLocation = targetPlayer.getLocation();  // 获取位置
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件

        // 编辑消息
        Component component = Component.text("")
                .append(Component.text(targetPlayer.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(separator).color(separatorColor))
                .append(Component.text(targetPlayer.getWorld().getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(String.format(getMessage(coordinates_format), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ())).color(NamedTextColor.YELLOW));

        // 根据配置文件在末尾追加一些信息
        if (config.getBoolean(String.format("%s.voxelmap-support", configRoot), false)) {
            component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text(getMessage(voxelmap_support_hover))))
                    .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(voxelmap_support_command), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), targetPlayer.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.xaeros-support", configRoot), false)) {
            component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(Component.text(getMessage(xaeros_support_hover))))
                    .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(xaeros_support_command), targetPlayer.getName(), targetPlayer.getName().charAt(0), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), targetPlayer.getWorld().getName()))));
        }

        if (config.getBoolean(String.format("%s.teleport-support", configRoot), false)) {
            component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                    .append(Component.text("[tp] ").color(NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(Component.text(String.format(getMessage(teleport_support_hover), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()))))
                            .clickEvent(ClickEvent.suggestCommand(String.format(getMessage(teleport_support_command), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()))));
        }

        return component;
    }

    /**
     * 生成一条消息 用于指示位置<br>
     * 白色箭头分隔符
     *
     * @param configRoot   配置文件根节点
     * @param targetPlayer 目标玩家对象
     * @return 生成的消息
     */
    public static Component builderPlayerCoordinatesMessage(String configRoot, Player targetPlayer) {
        return builderPlayerCoordinatesMessage(configRoot, targetPlayer, " -> ", NamedTextColor.WHITE);
    }

    /**
     * 检查一段字符串末尾是否是指定字符串(通配符检查)<br>如果是就格式化，不是就使用默认字符串进行格式化
     *
     * @param string         一段字符串
     * @param endString     检查结尾是否是此字符串
     * @param defaultString 默认字符串
     * @param args           格式化参数
     * @return 格式化完毕之后的字符串
     */
    public static String stringFormatEnd(String string, String endString, String defaultString, Object... args) {
        if (string == null) {
            return String.format(defaultString, args);
        }

        if (string.endsWith(endString)) {
            return String.format(string, args);
        } else {
            return String.format(defaultString, args);
        }
    }

    /**
     * 特殊的权限检查<br>指令目标为指定玩家<br>(enable节点如读取失败默认为true)
     * 特殊的权限检查<br>
     * 检查 configRoot.permission.other.enable <br>
     * 读取配置文件 configRoot.permission.other.node-other-player 作为权限节点<br>
     * 读取配置文件 configRoot.permission.other.node-target-player 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param configRoot                配置文件根节点 要求此节点下一节点为permission
     * @param sender                     发送者(被进行权限检查的对象)
     * @param defaultOtherPlayerNode  默认权限节点(其他玩家)(文件读取失败的使用值)
     * @param defaultTargetPlayerNode 默认权限节点(指定玩家)(要求结尾为%s)(文件读取失败的使用值)
     * @param targetPlayerName         目标玩家(用于权限检查)
     * @return 是否通过权限检查 (通过为true)
     */
    public static boolean specialCheckPermission(String configRoot,
                                                 CommandSender sender,
                                                 String defaultOtherPlayerNode,
                                                 String defaultTargetPlayerNode,
                                                 String targetPlayerName) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        return !((// 检查是否启用了权限检查
                config.getBoolean(String.format("%s.permission.other.enable", configRoot), true))
                && !(checkPermission(sender, config.getString(String.format("%s.permission.other.node-other-player", configRoot), defaultOtherPlayerNode))  // 玩家权限检查 目标为其他玩家
                || checkPermission(sender,
                stringFormatEnd(config.getString(String.format("%s.permission.other.node-target-player", configRoot)),
                        "%s", defaultTargetPlayerNode, targetPlayerName))));  // 玩家权限检查 目标为指定玩家
    }

    /**
     * 特殊的权限检查<br>
     * 检查config_root.permission.configMiddleNode.enable<br>
     * 读取配置文件 configRoot.permission.configMiddleNode.node 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param configRoot             配置文件根节点 要求此节点下一节点为permission
     * @param sender                  发送者(被进行权限检查的对象)
     * @param defaultPermissionNode 默认权限节点(文件读取失败的使用值)
     * @param configMiddleNode      配置文件中间节点
     * @return 是否通过权限检查 (通过为true)
     */
    public static boolean specialCheckPermission(String configRoot,
                                                 CommandSender sender,
                                                 String defaultPermissionNode,
                                                 String configMiddleNode) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        return !(config.getBoolean(String.format("%s.permission.%s.enable", configRoot, configMiddleNode), true)  // 子项权限管理
                && !checkPermission(sender, config.getString(String.format("%s.permission.%s.node", configRoot, configMiddleNode),   // 玩家权限检查
                defaultPermissionNode)));
    }

    /**
     * 特殊的权限检查<br>
     * 检查 configRoot.permission.self.enable<br>
     * 读取配置文件 configRoot.permission.self.node 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param configRoot       配置文件根节点 要求此节点下一节点为permission
     * @param sender            发送者(被进行权限检查的对象)
     * @param defaultSelfNode 默认权限节点(目标为自己)(文件读取失败的使用值)
     * @return 是否通过权限检查 (通过为true)
     */
    public static boolean specialCheckPermission(String configRoot,
                                                 CommandSender sender,
                                                 String defaultSelfNode) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        return !(config.getBoolean(String.format("%s.permission.self.enable", configRoot), true)  // 子项权限管理
                && !checkPermission(sender, config.getString(String.format("%s.permission.self.node", configRoot),   // 玩家权限检查
                defaultSelfNode)));
    }

    /**
     * 普通的权限检查<br>
     * 检查 configRoot.permission.enable<br>
     * 读取配置文件 configRoot.permission.node 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param configRoot  配置文件根节点 要求此节点下一节点为permission permission.node permission.enable
     * @param sender       发送者(被进行权限检查的对象)
     * @param defaultNode 默认权限节点(文件读取失败的使用值)
     * @return 是否通过权限检查 (通过为true)
     */
    public static boolean commonCheckPermission(String configRoot,
                                                CommandSender sender,
                                                String defaultNode) {
        FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件
        return !(config.getBoolean(String.format("%s.permission.enable", configRoot), true)  // 总项权限管理
                && !checkPermission(sender, config.getString(String.format("%s.permission.node", configRoot), defaultNode)));
    }
}
