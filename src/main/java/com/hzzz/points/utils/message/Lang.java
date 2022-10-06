package com.hzzz.points.utils.message;

import com.hzzz.points.Points;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.EnumMap;

import static com.hzzz.points.utils.Utils.logError;
import static org.bukkit.ChatColor.BLUE;

/**
 * 文字管理
 */
public final class Lang {
    private static FileConfiguration langConfig = null;  // 语言配置文件
    private static final EnumMap<MsgKey, String> messageMap = new EnumMap<>(MsgKey.class);

    /**
     * 工具类禁止实例化
     */
    private Lang() {
        throw new IllegalStateException("工具类禁止实例化");
    }

    /**
     * 读取消息
     *
     * @param key 消息键
     * @return 消息
     */
    public static String getMessage(MsgKey key) {
        if (messageMap.isEmpty()) {
            reloadLangConfig();
        }
        if (messageMap.containsKey(key)) {
            return messageMap.get(key);
        } else {
            if (Points.getInstance().getConfig().getBoolean("debug.enable", false)) {
                logError(String.format("文字类在尝试读取不存在键:%s", key));
            }
            return "这是一个bug！如果你看到了这条消息，请在github发送issue并且描述此时的使用场景";
        }
    }

    /**
     * 从lang\*.yml里面读取文字
     *
     * @param path        配置文件中的路径
     * @param defaultText 备选文字
     * @return 读取出的文字
     */
    private static String getLang(String path, String defaultText) {
        if (Points.getInstance().getConfig().getBoolean("debug.enable", false)
                && !defaultText.equals(getLangConfig().getString(path))) {  // debug模式
            logError(String.format("读取%s和默认不一致", path));
            logError(String.format("默认为:%s", defaultText));
            logError(String.format("读取为:%s", getLangConfig().getString(path)));
            logError(String.format("返回为:%s", getLangConfig().getString(path, defaultText)));
        }
        return getLangConfig().getString(path, defaultText);
    }

    /**
     * 获取语言配置文件
     *
     * @return 语言配置文件实例
     */
    public static FileConfiguration getLangConfig() {
        if (langConfig == null) {
            reloadLangConfig();
        }
        return langConfig;
    }

    /**
     * 读取语言配置文件，加载文字
     */
    public static void reloadLangConfig() {
        // 从config.yml读取语言文件名
        File langConfigFile = new File(Points.getInstance().getDataFolder(), String.format("lang/%s.yml", Points.getInstance().getConfig().getString("language.file_name", "zh_cn")));

        // 读取配置文件
        langConfig = YamlConfiguration.loadConfiguration(langConfigFile);

        // 加载文字
        loadText();
    }

    /**
     * 加载文字
     */
    public static void loadText() {
        messageMap.put(MsgKey.pluginStarting, BLUE + getLang("message.plugin.starting", "<Points>插件正在启动"));
        messageMap.put(MsgKey.pluginLoading, BLUE + getLang("message.plugin.loading", "<Points>插件正在加载"));
        messageMap.put(MsgKey.pluginDisabling, BLUE + getLang("message.plugin.disabling", "<Points>插件正在关闭"));
        messageMap.put(MsgKey.pluginStarted, BLUE + getLang("message.plugin.started", "<Points>插件已启动"));
        messageMap.put(MsgKey.pluginLoaded, BLUE + getLang("message.plugin.loaded", "<Points>插件已加载"));
        messageMap.put(MsgKey.pluginDisabled, BLUE + getLang("message.plugin.disabled", "<Points>插件已关闭"));
        messageMap.put(MsgKey.createDatabaseFolderSuccessfully, getLang("message.database.create_folder_successfully", "用于存放数据库的文件夹已初始化"));
        messageMap.put(MsgKey.createDatabaseFolderFailed, getLang("message.database.create_folder_failed", "用于存放数据库的文件夹创建失败"));
        messageMap.put(MsgKey.playerOnly, getLang("commands.global.player_only", "此指令仅允许玩家使用"));
        messageMap.put(MsgKey.noPermission, getLang("message.no-permission", "你没有使用该指令的权限"));
        messageMap.put(MsgKey.playerNotOnline, getLang("message.player-not-online", "该玩家不在线"));
        messageMap.put(MsgKey.finished, getLang("message.finished", "已执行"));
        messageMap.put(MsgKey.coordinatesFormat, getLang("commands.global.coordinates_format", " [%.0f, %.0f, %.0f] "));
        messageMap.put(MsgKey.voxelmapSupportHover, getLang("commands.global.voxelmap_support.hover", "§bVoxelmap§r: 点此以高亮坐标点, 或者Ctrl点击添加路径点"));
        messageMap.put(MsgKey.voxelmapSupportCommand, getLang("commands.global.voxelmap_support.command", "/newWaypoint x:%.0f, y:%.0f, z:%.0f, dim:%s"));
        messageMap.put(MsgKey.xaerosSupportHover, getLang("commands.global.xaeros_support.hover", "§6Xaeros Minimap§r: 点击添加路径点"));
        messageMap.put(MsgKey.xaerosSupportCommand, getLang("commands.global.xaeros_support.command", "xaero-waypoint:%s's Location:%s:%.0f:%.0f:%.0f:6:false:0:Internal_minecraft:%s_waypoints"));
        messageMap.put(MsgKey.teleportSupportHover, getLang("commands.global.teleport_support.hover", "§c点击以传送到 §e[%.0f, %.0f, %.0f]"));
        messageMap.put(MsgKey.teleportSupportCommand, getLang("commands.global.teleport_support.command", "/tp %f %f %f"));
        messageMap.put(MsgKey.reloadReady, getLang("message.reload_ready", "插件已完成重载！"));
        messageMap.put(MsgKey.wrongDatabaseType, getLang("message.database.wrong_database_type", "配置文件数据库类型错误，已启动默认数据库sqlite"));

        messageMap.put(MsgKey.enableDeathMessage, getLang("commands.death.message.enable", "已开启死亡信息提示"));
        messageMap.put(MsgKey.disableDeathMessage, getLang("commands.death.message.disable", "已关闭死亡信息提示"));
        messageMap.put(MsgKey.disableModule, getLang("commands.global.disable_module", "此模块被关闭"));
        messageMap.put(MsgKey.sqliteReady, getLang("message.database.sqlite.ready", "sqlite数据库 §e%s§f 已连接"));
        messageMap.put(MsgKey.sqliteNotReady, getLang("message.database.sqlite.not_ready", "你sqlite数据库 §e%s§f 炸了，对应的子模块将不会开启"));
        messageMap.put(MsgKey.setExecutor, getLang("message.executor.set", "已注册 §e%s§f 指令"));
        messageMap.put(MsgKey.alreadyDisableExecutor, getLang("message.executor.already_disable", "已注销 §e%s§f 指令"));
        messageMap.put(MsgKey.allExecutorDisabled, getLang("message.executor.all_disabled", "已注销全部指令"));
        messageMap.put(MsgKey.registerListeners, getLang("message.listeners.register", "已注册 §e%s§f 监听器"));
        messageMap.put(MsgKey.alreadyDisableListeners, getLang("message.listeners.already_disable", "已注销 §e%s§f 监听器"));
        messageMap.put(MsgKey.allListenersDisabled, getLang("message.listeners.all_disabled", "已注销全部监听器"));
        messageMap.put(MsgKey.configReloaded, getLang("message.reloaded.config", "配置已重新加载"));
        messageMap.put(MsgKey.insertDeathRecordFail, getLang("commands.death.log.insert_death_record_fail", "记录玩家 §e%s§f 的死亡信息时失败"));
        messageMap.put(MsgKey.noDeathRecord, getLang("commands.death.log.no_death_record", "玩家 §e%s§f 没有已保存的死亡记录"));
        messageMap.put(MsgKey.readDeathRecord, getLang("commands.death.log.read_death_record", "§6========§f已读取 §e%d §f条死亡记录§6========"));
        messageMap.put(MsgKey.divisionLine, getLang("division_line", "§6================================"));
        messageMap.put(MsgKey.enterBedCanceled, getLang("message.canceled.enter_bed", "你尝试进入梦乡，但是被未知力量阻止了"));
        messageMap.put(MsgKey.useRespawnAnchorCanceled, getLang("message.canceled.use_respawn_anchor", "你尝试使用重生锚，但是被未知力量阻止了"));
        messageMap.put(MsgKey.commandFrequencyLimit, getLang("commands.global.frequency_limit", "你使用的太快了！ 请稍后再试"));
        messageMap.put(MsgKey.readDeathLogResult, getLang("commands.death.log.read_death_log_result", "%s 在数据库中记录 %d 条, 限制为 %d 条"));
        messageMap.put(MsgKey.databaseError, getLang("message.database.error", "数据库发生错误"));
        messageMap.put(MsgKey.databaseSetupError, getLang("message.database.setup_error", "数据库初始化发生错误"));
        messageMap.put(MsgKey.databaseDriverError, getLang("message.database.driver_error", "数据库驱动加载发生错误"));
        messageMap.put(MsgKey.helpPoints, getLang("commands.points.help", """
                §6================================
                §e/points §f: 显示此消息
                §e/points reload §f: 重载插件
                §e/points help §f: 显示此消息
                §e/here §f: 广播自己的坐标并且获得高亮
                §e/where §f: 获取自己的坐标
                §e/where <player_name> §f: 获取他人的坐标
                §e/death §f: 死亡模块相关的指令帮助
                §6================================
                """));
        messageMap.put(MsgKey.helpDeath, getLang("commands.death.help", """
                §6================================
                §e/death §f: 显示此消息
                §e/death message §f: 切换是否在死亡的时候发送死亡坐标
                §e/death log §f: 获取死亡日志
                §6================================
                """));
        messageMap.put(MsgKey.helpWhere, getLang("commands.where.help", "使用方法: /where 玩家名 或 /where"));
        messageMap.put(MsgKey.helpEnderchest, getLang("commands.enderchest.help", "使用方法: /enderchest 玩家名 或 /enderchest"));
    }
}