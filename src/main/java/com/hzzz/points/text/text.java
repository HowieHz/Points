package com.hzzz.points.text;

import static org.bukkit.ChatColor.*;

// TODO lang.yml
public final class text {
    public static final String plugin_starting = BLUE+"<Points>插件正在启动";
    public static final String plugin_loading = BLUE+"<Points>插件正在加载";
    public static final String plugin_disabling = BLUE+"<Points>插件正在关闭";
    public static final String plugin_started = BLUE+"<Points>插件已启动";
    public static final String plugin_loaded = BLUE+"<Points>插件已加载";
    public static final String plugin_disabled = BLUE+"<Points>插件已关闭";
    public static final String player_only = "此指令仅允许玩家使用";
    public static final String no_permission = "你没有使用该指令的权限";
    public static final String no_player = "该玩家不在线";
    public static final String finished = "已执行";
    public static final String coordinates_format = " [%.0f, %.0f, %.0f] ";
    public static final String voxelmap_support_hover = "§bVoxelmap§r: 点此以高亮坐标点, 或者Ctrl点击添加路径点";
    public static final String voxelmap_support_command = "/newWaypoint x:%.0f, y:%.0f, z:%.0f, dim:%s";
    public static final String xaeros_support_hover = "§6Xaeros Minimap§r: 点击添加路径点";
    public static final String xaeros_support_command = "xaero_waypoint_add:%s's Location:%s:%.0f:%.0f:%.0f:6:false:0:Internal_minecraft:%s_waypoints";
    public static final String teleport_support_hover = "§c点击以传送到 §e[%.0f, %.0f, %.0f]";
    public static final String teleport_support_command = "/tp %f %f %f";
    public static final String reload_ready = "插件已完成重载！";
    public static final String wrong_database_type = "配置文件数据库类型错误，已启动默认数据库sqlite";
    public static final String help = """

-------------------------------------------------
/points : 显示此消息
/points reload : 重载插件
/points help : 显示此消息
/here : 广播自己的坐标并且获得高亮
/where : 获取自己的坐标
/where <player_name> : 获取他人的坐标
/death : 死亡模块相关的指令帮助
-------------------------------------------------
""";
    public static final String help_death = """

-------------------------------------------------
/death : 显示此消息
/death message : 切换是否在死亡的时候发送死亡坐标
/death log : 获取死亡日志
-------------------------------------------------
""";

    public static final String enable_death_message = "已开启死亡信息提示";
    public static final String disable_death_message = "已关闭死亡信息提示";
    public static final String disable_module = "此模块被关闭";
    public static final String sqlite_ready = "sqlite数据库 §e%s§f 已连接";
    public static final String sqlite_not_ready = "你sqlite数据库 §e%s§f 炸了，对应的子模块将不会开启";
    public static final String set_executor = "已注册 §e%s§f 指令";
    public static final String already_disable_executor = "已注销 §e%s§f 指令";
    public static final String all_executor_disabled = "已注销全部指令";
    public static final String register_event = "已注册 §e%s§f 监听器";
    public static final String already_disable_event = "已注销 §e%s§f 监听器";
    public static final String all_event_disabled = "已注销全部监听器";
    public static final String config_reloaded = "配置已重新加载";
    public static final String insert_death_record_fail = "记录玩家 §e%s§f 的死亡信息时失败";
    public static final String no_death_record = "玩家 §e%s§f 没有已保存的死亡记录";
    public static final String read_death_record = "-------已读取 §e%d§f 条死亡记录-------";
    public static final String division_line =     "-------------------------------";
}