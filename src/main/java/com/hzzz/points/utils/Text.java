package com.hzzz.points.utils;

import com.hzzz.points.Points;

import static com.hzzz.points.utils.Utils.logError;
import static org.bukkit.ChatColor.*;

/**
 * 文字枚举
 */
public final class Text {  // TODO 重构这个类 可以是map
    private static String plugin_starting;
    private static String plugin_loading;
    private static String plugin_disabling;
    private static String plugin_started;
    private static String plugin_loaded;
    private static String plugin_disabled;
    private static String create_database_folder_successfully;
    private static String create_database_folder_failed;
    private static String player_only;
    private static String no_permission;
    private static String player_not_online;
    private static String finished;
    private static String coordinates_format;
    private static String voxelmap_support_hover;
    private static String voxelmap_support_command;
    private static String xaeros_support_hover;
    private static String xaeros_support_command;
    private static String teleport_support_hover;
    private static String teleport_support_command;
    private static String reload_ready;
    private static String wrong_database_type;
    private static String enable_death_message;
    private static String disable_death_message;
    private static String disable_module;
    private static String sqlite_ready;
    private static String sqlite_not_ready;
    private static String set_executor;
    private static String already_disable_executor;
    private static String all_executor_disabled;
    private static String register_listeners;
    private static String already_disable_listeners;
    private static String all_listeners_disabled;
    private static String config_reloaded;
    private static String insert_death_record_fail;
    private static String no_death_record;
    private static String read_death_record;
    private static String division_line;
    private static String enter_bed_canceled;
    private static String use_respawn_anchor_canceled;
    private static String command_frequency_limit;
    private static String read_death_log_result;
    private static String database_error;
    private static String database_setup_error;
    private static String database_driver_error;
    private static String help_points;
    private static String help_death;
    private static String help_where;
    private static String help_enderchest;

    /**
     * 从lang\*.yml里面读取文字
     *
     * @param path         配置文件中的路径
     * @param default_text 备选文字
     * @return 读取出的文字
     */
    private static String getLang(String path, String default_text) {
        if (Points.getInstance().getConfig().getBoolean("debug.enable", false)
                && !default_text.equals(Points.getInstance().getLangConfig().getString(path))) {  // debug模式
            logError(String.format("读取%s和默认不一致", path));
            logError(String.format("默认为:%s", default_text));
            logError(String.format("读取为:%s", Points.getInstance().getLangConfig().getString(path)));
            logError(String.format("返回为:%s", Points.getInstance().getLangConfig().getString(path, default_text)));
        }
        return Points.getInstance().getLangConfig().getString(path, default_text);
    }

    /**
     * 加载文字
     */
    public static void loadText() {
        plugin_starting = BLUE + getLang("message.plugin.starting", "<Points>插件正在启动");
        plugin_loading = BLUE + getLang("message.plugin.loading", "<Points>插件正在加载");
        plugin_disabling = BLUE + getLang("message.plugin.disabling", "<Points>插件正在关闭");
        plugin_started = BLUE + getLang("message.plugin.started", "<Points>插件已启动");
        plugin_loaded = BLUE + getLang("message.plugin.loaded", "<Points>插件已加载");
        plugin_disabled = BLUE + getLang("message.plugin.disabled", "<Points>插件已关闭");
        create_database_folder_successfully = getLang("message.database.create_folder_successfully", "用于存放数据库的文件夹已初始化");
        create_database_folder_failed = getLang("message.database.create_folder_failed", "用于存放数据库的文件夹创建失败");
        player_only = getLang("commands.global.player_only", "此指令仅允许玩家使用");
        no_permission = getLang("message.no-permission", "你没有使用该指令的权限");
        player_not_online = getLang("message.player-not-online", "该玩家不在线");
        finished = getLang("message.finished", "已执行");
        coordinates_format = getLang("commands.global.coordinates_format", " [%.0f, %.0f, %.0f] ");
        voxelmap_support_hover = getLang("commands.global.voxelmap_support.hover", "§bVoxelmap§r: 点此以高亮坐标点, 或者Ctrl点击添加路径点");
        voxelmap_support_command = getLang("commands.global.voxelmap_support.command", "/newWaypoint x:%.0f, y:%.0f, z:%.0f, dim:%s");
        xaeros_support_hover = getLang("commands.global.xaeros_support.hover", "§6Xaeros Minimap§r: 点击添加路径点");
        xaeros_support_command = getLang("commands.global.xaeros_support.command", "xaero-waypoint:%s's Location:%s:%.0f:%.0f:%.0f:6:false:0:Internal_minecraft:%s_waypoints");
        teleport_support_hover = getLang("commands.global.teleport_support.hover", "§c点击以传送到 §e[%.0f, %.0f, %.0f]");
        teleport_support_command = getLang("commands.global.teleport_support.command", "/tp %f %f %f");
        reload_ready = getLang("message.reload_ready", "插件已完成重载！");
        wrong_database_type = getLang("message.database.wrong_database_type", "配置文件数据库类型错误，已启动默认数据库sqlite");

        enable_death_message = getLang("commands.death.message.enable", "已开启死亡信息提示");
        disable_death_message = getLang("commands.death.message.disable", "已关闭死亡信息提示");
        disable_module = getLang("commands.global.disable_module", "此模块被关闭");
        sqlite_ready = getLang("message.database.sqlite.ready", "sqlite数据库 §e%s§f 已连接");
        sqlite_not_ready = getLang("message.database.sqlite.not_ready", "你sqlite数据库 §e%s§f 炸了，对应的子模块将不会开启");
        set_executor = getLang("message.executor.set", "已注册 §e%s§f 指令");
        already_disable_executor = getLang("message.executor.already_disable", "已注销 §e%s§f 指令");
        all_executor_disabled = getLang("message.executor.all_disabled", "已注销全部指令");
        register_listeners = getLang("message.listeners.register", "已注册 §e%s§f 监听器");
        already_disable_listeners = getLang("message.listeners.already_disable", "已注销 §e%s§f 监听器");
        all_listeners_disabled = getLang("message.listeners.all_disabled", "已注销全部监听器");
        config_reloaded = getLang("message.reloaded.config", "配置已重新加载");
        insert_death_record_fail = getLang("commands.death.log.insert_death_record_fail", "记录玩家 §e%s§f 的死亡信息时失败");
        no_death_record = getLang("commands.death.log.no_death_record", "玩家 §e%s§f 没有已保存的死亡记录");
        read_death_record = getLang("commands.death.log.read_death_record", "§6========§f已读取 §e%d §f条死亡记录§6========");
        division_line = getLang("division_line", "§6================================");
        enter_bed_canceled = getLang("message.canceled.enter_bed", "你尝试进入梦乡，但是被未知力量阻止了");
        use_respawn_anchor_canceled = getLang("message.canceled.use_respawn_anchor", "你尝试使用重生锚，但是被未知力量阻止了");
        command_frequency_limit = getLang("commands.global.frequency_limit", "你使用的太快了！ 请稍后再试");
        read_death_log_result = getLang("commands.death.log.read_death_log_result", "%s 在数据库中记录 %d 条, 限制为 %d 条");
        database_error = getLang("message.database.error", "数据库发生错误");
        database_setup_error = getLang("message.database.setup_error", "数据库初始化发生错误");
        database_driver_error = getLang("message.database.driver_error", "数据库驱动加载发生错误");
        help_points = getLang("commands.points.help", """
                §6================================
                §e/points §f: 显示此消息
                §e/points reload §f: 重载插件
                §e/points help §f: 显示此消息
                §e/here §f: 广播自己的坐标并且获得高亮
                §e/where §f: 获取自己的坐标
                §e/where <player_name> §f: 获取他人的坐标
                §e/death §f: 死亡模块相关的指令帮助
                §6================================
                """);
        help_death = getLang("commands.death.help", """
                §6================================
                §e/death §f: 显示此消息
                §e/death message §f: 切换是否在死亡的时候发送死亡坐标
                §e/death log §f: 获取死亡日志
                §6================================
                """);
        help_where = getLang("commands.where.help", "使用方法: /where 玩家名 或 /where");
        help_enderchest = getLang("commands.enderchest.help", "使用方法: /enderchest 玩家名 或 /enderchest");
    }

    public static String getPluginStarting() {
        return plugin_starting;
    }

    public static String getPluginLoading() {
        return plugin_loading;
    }

    public static String getPluginDisabling() {
        return plugin_disabling;
    }

    public static String getPluginStarted() {
        return plugin_started;
    }

    public static String getPluginLoaded() {
        return plugin_loaded;
    }

    public static String getPluginDisabled() {
        return plugin_disabled;
    }

    public static String getCreateDatabaseFolderSuccessfully() {
        return create_database_folder_successfully;
    }

    public static String getCreateDatabaseFolderFailed() {
        return create_database_folder_failed;
    }

    public static String getPlayerOnly() {
        return player_only;
    }

    public static String getNoPermission() {
        return no_permission;
    }

    public static String getPlayerNotOnline() {
        return player_not_online;
    }

    public static String getFinished() {
        return finished;
    }

    public static String getCoordinatesFormat() {
        return coordinates_format;
    }

    public static String getVoxelmapSupportHover() {
        return voxelmap_support_hover;
    }

    public static String getVoxelmapSupportCommand() {
        return voxelmap_support_command;
    }

    public static String getXaerosSupportHover() {
        return xaeros_support_hover;
    }

    public static String getXaerosSupportCommand() {
        return xaeros_support_command;
    }

    public static String getTeleportSupportHover() {
        return teleport_support_hover;
    }

    public static String getTeleportSupportCommand() {
        return teleport_support_command;
    }

    public static String getReloadReady() {
        return reload_ready;
    }

    public static String getWrongDatabaseType() {
        return wrong_database_type;
    }

    public static String getEnableDeathMessage() {
        return enable_death_message;
    }

    public static String getDisableDeathMessage() {
        return disable_death_message;
    }

    public static String getDisableModule() {
        return disable_module;
    }

    public static String getSqliteReady() {
        return sqlite_ready;
    }

    public static String getSqliteNotReady() {
        return sqlite_not_ready;
    }

    public static String getSetExecutor() {
        return set_executor;
    }

    public static String getAlreadyDisableExecutor() {
        return already_disable_executor;
    }

    public static String getAllExecutorDisabled() {
        return all_executor_disabled;
    }

    public static String getRegisterListeners() {
        return register_listeners;
    }

    public static String getAlreadyDisableListeners() {
        return already_disable_listeners;
    }

    public static String getAllListenersDisabled() {
        return all_listeners_disabled;
    }

    public static String getConfigReloaded() {
        return config_reloaded;
    }

    public static String getInsertDeathRecordFail() {
        return insert_death_record_fail;
    }

    public static String getNoDeathRecord() {
        return no_death_record;
    }

    public static String getReadDeathRecord() {
        return read_death_record;
    }

    public static String getDivisionLine() {
        return division_line;
    }

    public static String getEnterBedCanceled() {
        return enter_bed_canceled;
    }

    public static String getUseRespawnAnchorCanceled() {
        return use_respawn_anchor_canceled;
    }

    public static String getCommandFrequencyLimit() {
        return command_frequency_limit;
    }

    public static String getReadDeathLogResult() {
        return read_death_log_result;
    }

    public static String getDatabaseError() {
        return database_error;
    }

    public static String getDatabaseSetupError() {
        return database_setup_error;
    }

    public static String getDatabaseDriverError() {
        return database_driver_error;
    }

    public static String getHelpPoints() {
        return help_points;
    }

    public static String getHelpDeath() {
        return help_death;
    }

    public static String getHelpWhere() {
        return help_where;
    }

    public static String getHelpEnderchest() {
        return help_enderchest;
    }
}