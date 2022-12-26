package com.hzzz.points.utils.base_utils_class;

import com.hzzz.points.Points;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static com.hzzz.points.utils.Utils.checkPermission;
import static com.hzzz.points.utils.message.Lang.getMessage;
import static com.hzzz.points.utils.message.MsgKey.*;

/**
 * <p>权限检查的封装 消息生成的封装</p>
 * <p>放一起的好是因为config只要读一次</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @since 2022-11-26 20:46
 */
public abstract class BaseUtilsClass {
    static protected FileConfiguration config = Points.getInstance().getConfig();  // 读取配置文件

    /**
     * 重新读取配置文件
     */
    static public void reloadConfig() {
        config = Points.getInstance().getConfig();  // 读取配置文件
    }

    /**
     * 生成一条消息 用于指示位置<br>
     * 以下配置文件位置将会被检查：<br>
     * rootConfigNode.voxelmap-support<br>
     * rootConfigNode.xaeros-support<br>
     * rootConfigNode.teleport-support<br>
     *
     * @param rootConfigNode     配置文件根节点
     * @param targetPlayerObject 目标玩家对象
     * @param separator          分隔符
     * @param separatorColor     分隔符颜色
     * @return 生成的消息
     */
    public Component buildPlayerCoordinatesMessage(String rootConfigNode, Player targetPlayerObject, String separator, NamedTextColor separatorColor) {
        Location playerLocation = targetPlayerObject.getLocation();  // 获取位置

        // 编辑消息
        Component component = Component.text("")
                .append(Component.text(targetPlayerObject.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(separator).color(separatorColor))
                .append(Component.text(targetPlayerObject.getWorld().getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(String.format(getMessage(COORDINATES_FORMAT), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ())).color(NamedTextColor.YELLOW));

        // 根据配置文件在末尾追加一些信息
        if (config.getBoolean(rootConfigNode + ".voxelmap-support", false)) {
            String command = String.format(getMessage(VOXELMAP_SUPPORT_COMMAND), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), targetPlayerObject.getWorld().getName());
            component = component.append(Component.text("[+V] ").color(NamedTextColor.AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text(getMessage(VOXELMAP_SUPPORT_HOVER))))
                    .clickEvent(ClickEvent.runCommand(command))
                    .insertion(command));  // shift-clicked
        }

        if (config.getBoolean(rootConfigNode + ".xaeros-support", false)) {
            String command = String.format(getMessage(XAEROS_SUPPORT_COMMAND), targetPlayerObject.getName(), targetPlayerObject.getName().charAt(0), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), targetPlayerObject.getWorld().getName());
            component = component.append(Component.text("[+X] ").color(NamedTextColor.GOLD)
                    .hoverEvent(HoverEvent.showText(Component.text(getMessage(XAEROS_SUPPORT_HOVER))))
                    .clickEvent(ClickEvent.suggestCommand(command))
                    .insertion(command));  // shift-clicked
        }

        if (config.getBoolean(rootConfigNode + ".teleport-support", false)) {
            String command = String.format(getMessage(TELEPORT_SUPPORT_COMMAND), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
            component = component.append(Component.text("-> ").color(NamedTextColor.WHITE))
                    .append(Component.text("[tp] ").color(NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(Component.text(String.format(getMessage(TELEPORT_SUPPORT_HOVER), playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()))))
                            .clickEvent(ClickEvent.runCommand(command))
                            .insertion(command));  // shift-clicked
        }

        return component;
    }

    /**
     * 生成一条消息 用于指示位置<br>
     * 白色箭头分隔符<br>
     * 以下配置文件位置将会被检查：<br>
     * rootConfigNode.voxelmap-support<br>
     * rootConfigNode.xaeros-support<br>
     * rootConfigNode.teleport-support<br>
     *
     * @param rootConfigNode     配置文件根节点
     * @param targetPlayerObject 目标玩家对象
     * @return 生成的消息
     */
    public Component buildPlayerCoordinatesMessage(String rootConfigNode, Player targetPlayerObject) {
        return buildPlayerCoordinatesMessage(rootConfigNode, targetPlayerObject, " -> ", NamedTextColor.WHITE);
    }


    /**
     * 指令目标为指定玩家, 检查是否有权限 (用于完整输入指令后，运行时检查) <br>
     * 意图是：检查有没有对<big>其他玩家</big>使用该指令的权限，先检查通用权限，再检查指定玩家<br><br>
     * (enable节点如读取失败默认为true)<br><br>
     * 权限检查顺序<br>
     * 1. 权限管理有没有开启 (检查 parentConfigNode.permission.other.enable)<br>
     * 2. 检查有没有这个权限 other通用节点 (读取配置文件 parentConfigNode.permission.other.node-other-player 作为权限节点)<br>
     * 3. 把玩家名格式化带入 检查有没有这个权限 other特定玩家节点 (读取配置文件 parentConfigNode.permission.other.node-target-player 作为权限节点)<br>
     * (enable节点如读取失败默认为true)
     * <br>
     * 注：当仅仅需要检查"其他玩家"不检查"指定玩家"的时候常常把targetPlayerName设为""，比如：指令补全的时候的权限检查
     *
     * @param sender                                  发送者(被进行权限检查的对象)
     * @param parentConfigNode                        配置文件根节点 要求此节点下一节点为permission
     * @param targetPlayerName                        目标玩家(用于权限检查)
     * @param defaultPermissionNodeTargetOtherPlayers 默认权限节点(其他玩家)(文件读取失败的使用值)
     * @param defaultPermissionNodeTargetSingerPlayer 默认权限节点(指定玩家)(要求结尾为%s)(文件读取失败的使用值)
     * @return 是否通过权限检查 (通过为true)
     */
    public boolean checkPermissionTargetOther(CommandSender sender,
                                              String parentConfigNode,
                                              String targetPlayerName,
                                              String defaultPermissionNodeTargetOtherPlayers,
                                              String defaultPermissionNodeTargetSingerPlayer) {
        if (!config.getBoolean(parentConfigNode + ".permission.other.enable", true)) {  // 权限管理有没有开启
            return true;
        }
        if (checkPermission(sender, config.getString(parentConfigNode + ".permission.other.node-other-player", defaultPermissionNodeTargetOtherPlayers))) {  // 检查有没有这个权限 other通用节点
            return true;
        }

        String permission = config.getString(parentConfigNode + ".permission.other.node-target-player");

        if (permission == null){
            permission = defaultPermissionNodeTargetSingerPlayer;
        }

        // 把玩家名格式化带入 检查有没有这个权限 other特定玩家节点
        // TODO 需要重构，自定义权限要拉到单独一个文件里面比较好
        return checkPermission(sender, permission.replace("%s", targetPlayerName));
    }

    /**
     * 指定节点检查 配置文件结构parentConfigNode.permission.self.node<br>
     * <p>
     * 指令目标为自己, <br>
     * 意图是：检查有没有对<big>自己</big>使用该指令的权限<br><br>
     * (enable节点如读取失败默认为true)<br><br>
     * 权限检查顺序<br>
     * 1. 权限管理有没有开启 (检查parentConfigNode.permission.self.enable)<br>
     * 2. 检查有没有这个权限 (读取配置文件 parentConfigNode.permission.self.node 作为权限节点)<br>
     *
     * @param sender                发送者(被进行权限检查的对象)
     * @param parentConfigNode      配置文件根节点 要求此节点下一节点为permission
     * @param defaultPermissionNode 默认权限节点(目标为自己)(文件读取失败的使用值)
     * @return 是否通过权限检查 (通过为true)
     */
    public boolean checkPermissionTargetSelf(CommandSender sender,
                                             String parentConfigNode,
                                             String defaultPermissionNode) {
        if (!config.getBoolean(parentConfigNode + ".permission.self.enable", true)) {  // 权限管理有没有开启
            return true;
        }
        final String ConfigNode = parentConfigNode + ".permission.self.node";
        // 玩家权限检查
        return checkPermission(sender, config.getString(ConfigNode, defaultPermissionNode));
    }

    /**
     * 指定权限检查 配置文件结构parentConfigNode.permission.node<br>
     * <p>
     * 权限检查顺序<br>
     * 1. 权限管理有没有开启 (检查 parentConfigNode.permission.enable)<br>
     * 2. 检查有没有这个权限 (读取配置文件 parentConfigNode.permission.node) 作为权限节点<br>
     * (enable节点如读取失败默认为true)
     *
     * @param sender                发送者(被进行权限检查的对象)
     * @param parentConfigNode      配置文件根节点 要求此节点下一节点为permission permission.node permission.enable
     * @param defaultPermissionNode 默认权限节点(文件读取失败的使用值)
     * @return 是否通过权限检查 (通过为true)
     */
    public boolean checkPermissionOneConfigNode(CommandSender sender,
                                                String parentConfigNode,
                                                String defaultPermissionNode) {
        if (!config.getBoolean(parentConfigNode + ".permission.enable", true)) {  // 权限管理有没有开启
            return true;
        }
        final String ConfigNode = parentConfigNode + ".permission.node";
        // 玩家权限检查
        return checkPermission(sender, config.getString(ConfigNode, defaultPermissionNode));
    }
}
