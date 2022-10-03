package com.hzzz.points.text;

import com.hzzz.points.Points;

import static org.bukkit.ChatColor.*;

/**
 * 文字枚举
 */
public final class Text {
    /**
     * 从lang\*.yml里面读取文字
     *
     * @param path         配置文件中的路径
     * @param default_text 备选文字
     * @return 读取出的文字
     */
    private static String getLang(String path, String default_text) {
        if (default_text.equals(Points.getInstance().getLangConfig().getString(path, default_text))) {
            return Points.getInstance().getLangConfig().getString(path, default_text);
        } else {
            return "???";
        }
//        return Points.getInstance().getLangConfig().getString(path, default_text);
    }

    public static final String plugin_starting = BLUE + getLang("message.plugin.starting", "<Points>插件正在启动");
    public static final String plugin_loading = BLUE + getLang("message.plugin.loading", "<Points>插件正在加载");
    public static final String plugin_disabling = BLUE + getLang("message.plugin.disabling", "<Points>插件正在关闭");
    public static final String plugin_started = BLUE + getLang("message.plugin.started", "<Points>插件已启动");
    public static final String plugin_loaded = BLUE + getLang("message.plugin.loaded", "<Points>插件已加载");
    public static final String plugin_disabled = BLUE + getLang("message.plugin.disabled", "<Points>插件已关闭");
    public static final String create_database_folder_successfully = getLang("message.database.create_folder_successfully", "用于存放数据库的文件夹已初始化");
    public static final String create_database_folder_failed = getLang("message.database.create_folder_failed", "用于存放数据库的文件夹创建失败");
    public static final String player_only = getLang("commands.global.player_only", "此指令仅允许玩家使用");
    public static final String no_permission = getLang("message.no.permission", "你没有使用该指令的权限");
    public static final String no_player = getLang("message.no.player", "该玩家不在线");
    public static final String finished = getLang("message.finished", "已执行");
    public static final String coordinates_format = getLang("commands.global.coordinates_format", " [%.0f, %.0f, %.0f] ");
    public static final String voxelmap_support_hover = getLang("commands.global.voxelmap_support.hover", "§bVoxelmap§r: 点此以高亮坐标点, 或者Ctrl点击添加路径点");
    public static final String voxelmap_support_command = getLang("commands.global.voxelmap_support.command", "/newWaypoint x:%.0f, y:%.0f, z:%.0f, dim:%s");
    public static final String xaeros_support_hover = getLang("commands.global.xaeros_support.hover", "§6Xaeros Minimap§r: 点击添加路径点");
    public static final String xaeros_support_command = getLang("commands.global.xaeros_support.command", "xaero-waypoint:%s's Location:%s:%.0f:%.0f:%.0f:6:false:0:Internal_minecraft:%s_waypoints");
    public static final String teleport_support_hover = getLang("commands.global.teleport_support.hover", "§c点击以传送到 §e[%.0f, %.0f, %.0f]");
    public static final String teleport_support_command = getLang("commands.global.teleport_support.command", "/tp %f %f %f");
    public static final String reload_ready = getLang("message.reload_ready", "插件已完成重载！");
    public static final String wrong_database_type = getLang("message.database.wrong_database_type", "配置文件数据库类型错误，已启动默认数据库sqlite");

    public static final String enable_death_message = getLang("commands.death.message.enable", "已开启死亡信息提示");
    public static final String disable_death_message = getLang("commands.death.message.disable", "已关闭死亡信息提示");
    public static final String disable_module = getLang("commands.global.disable_module", "此模块被关闭");
    public static final String sqlite_ready = getLang("message.database.sqlite.ready", "sqlite数据库 §e%s§f 已连接");
    public static final String sqlite_not_ready = getLang("message.database.sqlite.not_ready", "你sqlite数据库 §e%s§f 炸了，对应的子模块将不会开启");
    public static final String set_executor = getLang("message.executor.set", "已注册 §e%s§f 指令");
    public static final String already_disable_executor = getLang("message.executor.already_disable", "已注销 §e%s§f 指令");
    public static final String all_executor_disabled = getLang("message.executor.all_disabled", "已注销全部指令");
    public static final String register_listeners = getLang("message.listeners.register", "已注册 §e%s§f 监听器");
    public static final String already_disable_listeners = getLang("message.listeners.already_disable", "已注销 §e%s§f 监听器");
    public static final String all_listeners_disabled = getLang("message.listeners.all_disabled", "已注销全部监听器");
    public static final String config_reloaded = getLang("reloaded.config", "配置已重新加载");
    public static final String insert_death_record_fail = getLang("commands.death.log.insert_death_record_fail", "记录玩家 §e%s§f 的死亡信息时失败");
    public static final String no_death_record = getLang("commands.death.log.no_death_record", "玩家 §e%s§f 没有已保存的死亡记录");
    public static final String read_death_record = getLang("commands.death.log.read_death_record", "-------已读取 §e%d§f 条死亡记录-------");
    public static final String division_line = getLang("division_line", "-------------------------------");
    public static final String enter_bed_canceled = getLang("message.canceled.enter_bed", "你尝试进入梦乡，但是被未知力量阻止了");
    public static final String use_respawn_anchor_canceled = getLang("message.canceled.use_respawn_anchor", "你尝试使用重生锚，但是被未知力量阻止了");
    public static final String command_frequency_limit = getLang("commands.global.frequency_limit", "你使用的太快了！ 请稍后再试");
    public static final String read_death_log_result = getLang("commands.death.log.read_death_log_result", "%s 在数据库中记录 %d 条, 限制为 %d 条");
    public static final String database_error = getLang("message.database.error", "数据库发生错误");
    public static final String database_setup_error = getLang("message.database.setup_error", "数据库初始化发生错误");
    public static final String database_driver_error = getLang("message.database.driver_error", "数据库驱动加载发生错误");
    public static final String help_points = getLang("commands.points.help", """

            -------------------------------------------------
            /points : 显示此消息
            /points reload : 重载插件
            /points help : 显示此消息
            /here : 广播自己的坐标并且获得高亮
            /where : 获取自己的坐标
            /where <player_name> : 获取他人的坐标
            /death : 死亡模块相关的指令帮助
            -------------------------------------------------
            """);
    public static final String help_death = getLang("commands.death.help", """

            -------------------------------------------------
            /death : 显示此消息
            /death message : 切换是否在死亡的时候发送死亡坐标
            /death log : 获取死亡日志
            -------------------------------------------------
            """);
    public static final String help_where = getLang("commands.where.help", "使用方法: /where 玩家名 或 /where");
    public static final String help_enderchest = getLang("commands.enderchest.help", "使用方法: /enderchest 玩家名 或 /enderchest");
}