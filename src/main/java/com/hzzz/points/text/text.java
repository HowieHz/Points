package com.hzzz.points.text;

public final class text {
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
    public static final String sqlite_ready = "sqlite数据库 %s 已连接";
    public static final String sqlite_not_ready = "你sqlite数据库 %s 炸了，对应的子模块将不会开启";
}