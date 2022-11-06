package com.hzzz.points.utils.message;

import com.hzzz.points.Points;
import com.hzzz.points.utils.data_structure.KeyAndMsgInfo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.EnumMap;

import static com.hzzz.points.utils.Utils.logError;
import static org.bukkit.ChatColor.BLUE;

/**
 * 文字管理<br><br>
 * 初始化和重载时执行顺序<br>
 * 入口reloadLangConfig (Points.getInstance()) -> <br>
 * getLang -> <br>
 * getLangConfig (langConfig != null 否则 reloadLangConfig 这里看来其实不可能为null，但是为了防止未来换掉了YamlConfiguration.loadConfiguration的实现，导致可能返回null，故保留) -> <br>
 * getLangConfig 返回 -> <br>
 * getLang 剩下部分 -> <br>
 * getLang 返回 -> <br>
 * 初始化好字典 -> <br>
 * 结束 <br>因为要执行Points.getInstance(), 所以不能写构造函数<br><br>
 * <p>
 * 读取文字执行顺序<br>
 * 入口getMessage (!messageMap.isEmpty() 否则 reloadLangConfig 所以这个比reloadLangConfig可以先执行，但是仍然受到Points.getInstance()的限制) -> <br>
 * 读取字典 返回文字 <br>
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
     * 读取消息，如无则返回""
     *
     * @param key 消息键
     * @return 消息
     */
    @NotNull
    public static String getMessage(@NotNull MsgKey key) {
        if (messageMap.isEmpty()) {
            reloadLangConfig();
        }
        if (messageMap.containsKey(key)) {
            return messageMap.get(key);
        } else {
            logError("文字类在尝试读取不存在键:" + key);
            logError("这是一个bug！如果你看到了这条消息，请在github发送issue并且描述此时的使用场景");
            return "";
        }
    }

    /**
     * 从lang\*.yml里面读取文字
     *
     * @param path 配置文件中的路径
     * @return 读取出的文字
     */
    @Nullable
    private static String getLang(@NotNull String path) {
        String text = getLangConfig().getString(path);
        if (text == null) {
            logError("A Error when read " + path);
            return null;
        } else {
            return text;
        }
    }

    /**
     * 获取语言配置文件
     *
     * @return 语言配置文件实例
     */
    @NotNull
    private static FileConfiguration getLangConfig() {
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
        final KeyAndMsgInfo[] keyAndMsgInfos = {
                new KeyAndMsgInfo(MsgKey.PLUGIN_STARTING, BLUE + getLang("message.plugin.starting")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_LOADING, BLUE + getLang("message.plugin.loading")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_DISABLING, BLUE + getLang("message.plugin.disabling")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_STARTED, BLUE + getLang("message.plugin.started")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_LOADED, BLUE + getLang("message.plugin.loaded")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_DISABLED, BLUE + getLang("message.plugin.disabled")),
                new KeyAndMsgInfo(MsgKey.CREATE_DATABASE_FOLDER_SUCCESSFULLY, getLang("message.database.create_folder_successfully")),
                new KeyAndMsgInfo(MsgKey.CREATE_DATABASE_FOLDER_FAILED, getLang("message.database.create_folder_failed")),
                new KeyAndMsgInfo(MsgKey.PLAYER_ONLY, getLang("commands.global.player_only")),
                new KeyAndMsgInfo(MsgKey.NO_PERMISSION, getLang("message.no-permission")),
                new KeyAndMsgInfo(MsgKey.PLAYER_NOT_ONLINE, getLang("message.player-not-online")),
                new KeyAndMsgInfo(MsgKey.FINISHED, getLang("message.finished")),
                new KeyAndMsgInfo(MsgKey.COORDINATES_FORMAT, getLang("commands.global.coordinates_format")),
                new KeyAndMsgInfo(MsgKey.VOXELMAP_SUPPORT_HOVER, getLang("commands.global.voxelmap_support.hover")),
                new KeyAndMsgInfo(MsgKey.VOXELMAP_SUPPORT_COMMAND, getLang("commands.global.voxelmap_support.command")),
                new KeyAndMsgInfo(MsgKey.XAEROS_SUPPORT_HOVER, getLang("commands.global.xaeros_support.hover")),
                new KeyAndMsgInfo(MsgKey.XAEROS_SUPPORT_COMMAND, getLang("commands.global.xaeros_support.command")),
                new KeyAndMsgInfo(MsgKey.TELEPORT_SUPPORT_HOVER, getLang("commands.global.teleport_support.hover")),
                new KeyAndMsgInfo(MsgKey.TELEPORT_SUPPORT_COMMAND, getLang("commands.global.teleport_support.command")),
                new KeyAndMsgInfo(MsgKey.RELOAD_READY, getLang("message.reload_ready")),
                new KeyAndMsgInfo(MsgKey.WRONG_DATABASE_TYPE, getLang("message.database.wrong_database_type")),
                new KeyAndMsgInfo(MsgKey.ENABLE_DEATH_MESSAGE, getLang("commands.death.message.enable")),
                new KeyAndMsgInfo(MsgKey.DISABLE_DEATH_MESSAGE, getLang("commands.death.message.disable")),
                new KeyAndMsgInfo(MsgKey.DISABLE_MODULE, getLang("commands.global.disable_module")),
                new KeyAndMsgInfo(MsgKey.SQLITE_READY, getLang("message.database.sqlite.ready")),
                new KeyAndMsgInfo(MsgKey.SQLITE_NOT_READY, getLang("message.database.sqlite.not_ready")),
                new KeyAndMsgInfo(MsgKey.SET_EXECUTOR, getLang("message.executor.set")),
                new KeyAndMsgInfo(MsgKey.ALREADY_DISABLE_EXECUTOR, getLang("message.executor.already_disable")),
                new KeyAndMsgInfo(MsgKey.ALL_EXECUTOR_DISABLED, getLang("message.executor.all_disabled")),
                new KeyAndMsgInfo(MsgKey.REGISTER_LISTENERS, getLang("message.listeners.register")),
                new KeyAndMsgInfo(MsgKey.ALREADY_DISABLE_LISTENERS, getLang("message.listeners.already_disable")),
                new KeyAndMsgInfo(MsgKey.ALL_LISTENERS_DISABLED, getLang("message.listeners.all_disabled")),
                new KeyAndMsgInfo(MsgKey.CONFIG_RELOADED, getLang("message.reloaded.config")),
                new KeyAndMsgInfo(MsgKey.INSERT_DEATH_RECORD_FAIL, getLang("commands.death.log.insert_death_record_fail")),
                new KeyAndMsgInfo(MsgKey.NO_DEATH_RECORD, getLang("commands.death.log.no_death_record")),
                new KeyAndMsgInfo(MsgKey.READ_DEATH_RECORD, getLang("commands.death.log.read_death_record")),
                new KeyAndMsgInfo(MsgKey.DIVISION_LINE, getLang("division_line")),
                new KeyAndMsgInfo(MsgKey.ENTER_BED_CANCELED, getLang("message.canceled.enter_bed")),
                new KeyAndMsgInfo(MsgKey.USE_RESPAWN_ANCHOR_CANCELED, getLang("message.canceled.use_respawn_anchor")),
                new KeyAndMsgInfo(MsgKey.COMMAND_FREQUENCY_LIMIT, getLang("commands.global.frequency_limit")),
                new KeyAndMsgInfo(MsgKey.READ_DEATH_LOG_RESULT, getLang("commands.death.log.read_death_log_result")),
                new KeyAndMsgInfo(MsgKey.DATABASE_ERROR, getLang("message.database.error")),
                new KeyAndMsgInfo(MsgKey.DATABASE_SETUP_ERROR, getLang("message.database.setup_error")),
                new KeyAndMsgInfo(MsgKey.DATABASE_DRIVER_ERROR, getLang("message.database.driver_error")),
                new KeyAndMsgInfo(MsgKey.HELP_POINTS, getLang("commands.points.help")),
                new KeyAndMsgInfo(MsgKey.HELP_DEATH, getLang("commands.death.help")),
                new KeyAndMsgInfo(MsgKey.HELP_WHERE, getLang("commands.where.help")),
                new KeyAndMsgInfo(MsgKey.HELP_ENDERCHEST, getLang("commands.enderchest.help")),
                new KeyAndMsgInfo(MsgKey.HELP_FAIR_PVP, getLang("commands.fair-pvp.help")),
                new KeyAndMsgInfo(MsgKey.ENABLE_FAIR_PVP, getLang("commands.fair-pvp.enable")),
                new KeyAndMsgInfo(MsgKey.DISABLE_FAIR_PVP, getLang("commands.fair-pvp.disable")),
                new KeyAndMsgInfo(MsgKey.NO_DEPEND, getLang("message.no-depend")),
                new KeyAndMsgInfo(MsgKey.LOADED_DEPEND, getLang("message.loaded-depend")),
        };
        for (KeyAndMsgInfo info :
                keyAndMsgInfos) {
            messageMap.put(info.key, info.value);
        }
    }
}