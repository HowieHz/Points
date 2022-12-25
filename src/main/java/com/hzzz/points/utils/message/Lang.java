package com.hzzz.points.utils.message;

import com.hzzz.points.Points;
import com.hzzz.points.utils.data_structure.KeyAndMsgInfo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.EnumMap;

import static com.hzzz.points.utils.Utils.logError;

/**
 * 文字管理<br><br>
 * 初始化和重载时执行顺序<br>
 * 入口reloadLangConfig (Points.getInstance()) -> <br>
 * 调用loadLang得到键-> <br>
 * 字典初始化完毕 -> <br>
 * 结束 <br>因为reloadLangConfig要执行Points.getInstance(), 所以reloadLangConfig要放在Points类里面执行而不是构造函数<br><br>
 * <p>
 * 读取文字执行顺序<br>
 * 入口getMessage (reloadLangConfig初始化之前执行只会返回key)-> <br>
 * 读取字典 返回文字 <br>
 */
public final class Lang {
    private static String prefix = "";
    private static FileConfiguration langConfig = null;  // 语言配置文件
    private static final EnumMap<MsgKey, String> messageMap = new EnumMap<>(MsgKey.class);

    /**
     * 工具类禁止实例化
     */
    private Lang() {
        throw new IllegalStateException("工具类禁止实例化");
    }

    /**
     * 从messageMap读取消息，如无则返回key
     *
     * @param key 消息键
     * @return 消息
     */
    @NotNull
    public static String getMessage(@NotNull MsgKey key) {
        if (messageMap.isEmpty()) {
            // 这里不写 reloadLangConfig() 的原因是，在使用getMessage之前显式调用一次reloadLangConfig在此之前，加载和读取分开，代码逻辑更清楚
            logError("文字类在加载前被读取");
            return String.valueOf(key);
        }
        if (messageMap.containsKey(key)) {
            return messageMap.get(key);
        } else {
            logError("插件在尝试读取不存在的键值对，键为:" + key);
            logError("这是一个bug！如果你看到了这条消息，请在github发送issue并且描述此时的使用场景");
            return String.valueOf(key);
        }
    }

    /**
     * 从lang\*.yml里面读取文字，如无则返回path
     *
     * @param path 配置文件中的路径
     * @return 读取出的文字
     */
    @NotNull
    private static String loadLang(@NotNull String path) {
        String text = langConfig.getString(path);
        if (text == null) {
            logError("插件在加载文字配置文件内不存在的路径，路径为：" + path);
            logError("这是一个bug！如果你看到了这条消息，请在github发送issue并且描述此时的使用场景");
            return path;
        } else {
            return prefix + text;
        }
    }

    /**
     * 获取语言配置文件
     *
     * @return 语言配置文件实例
     */
    @NotNull
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
        // 读取前缀
        // TODO 有些不能用前缀，要分离出来，或者分离语言文件
        prefix = langConfig.getString("prefix");

        // 加载文字
        final KeyAndMsgInfo[] keyAndMsgInfos = {
                new KeyAndMsgInfo(MsgKey.PLUGIN_STARTING, loadLang("plugin.starting")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_LOADING, loadLang("plugin.loading")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_DISABLING, loadLang("plugin.disabling")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_STARTED, loadLang("plugin.started")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_LOADED, loadLang("plugin.loaded")),
                new KeyAndMsgInfo(MsgKey.PLUGIN_DISABLED, loadLang("plugin.disabled")),
                new KeyAndMsgInfo(MsgKey.CREATE_DATABASE_FOLDER_SUCCESSFULLY, loadLang("database.create_folder_successfully")),
                new KeyAndMsgInfo(MsgKey.CREATE_DATABASE_FOLDER_FAILED, loadLang("database.create_folder_failed")),
                new KeyAndMsgInfo(MsgKey.PLAYER_ONLY, loadLang("player_only")),
                new KeyAndMsgInfo(MsgKey.NO_PERMISSION, loadLang("no-permission")),
                new KeyAndMsgInfo(MsgKey.PLAYER_NOT_ONLINE, loadLang("player-not-online")),
                new KeyAndMsgInfo(MsgKey.FINISHED, loadLang("finished")),
                new KeyAndMsgInfo(MsgKey.COORDINATES_FORMAT, loadLang("coordinates_format")),
                new KeyAndMsgInfo(MsgKey.VOXELMAP_SUPPORT_HOVER, loadLang("voxelmap_support.hover")),
                new KeyAndMsgInfo(MsgKey.VOXELMAP_SUPPORT_COMMAND, loadLang("voxelmap_support.command")),
                new KeyAndMsgInfo(MsgKey.XAEROS_SUPPORT_HOVER, loadLang("xaeros_support.hover")),
                new KeyAndMsgInfo(MsgKey.XAEROS_SUPPORT_COMMAND, loadLang("xaeros_support.command")),
                new KeyAndMsgInfo(MsgKey.TELEPORT_SUPPORT_HOVER, loadLang("teleport_support.hover")),
                new KeyAndMsgInfo(MsgKey.TELEPORT_SUPPORT_COMMAND, loadLang("teleport_support.command")),
                new KeyAndMsgInfo(MsgKey.RELOAD_READY, loadLang("reload_ready")),
                new KeyAndMsgInfo(MsgKey.WRONG_DATABASE_TYPE, loadLang("database.wrong_database_type")),
                new KeyAndMsgInfo(MsgKey.ENABLE_DEATH_MESSAGE, loadLang("death.message.enable")),
                new KeyAndMsgInfo(MsgKey.DISABLE_DEATH_MESSAGE, loadLang("death.message.disable")),
                new KeyAndMsgInfo(MsgKey.DISABLE_MODULE, loadLang("disable_module")),
                new KeyAndMsgInfo(MsgKey.SQLITE_READY, loadLang("database.sqlite.ready")),
                new KeyAndMsgInfo(MsgKey.SQLITE_NOT_READY, loadLang("database.sqlite.not_ready")),
                new KeyAndMsgInfo(MsgKey.SET_EXECUTOR, loadLang("executor.set")),
                new KeyAndMsgInfo(MsgKey.ALREADY_DISABLE_EXECUTOR, loadLang("executor.already_disable")),
                new KeyAndMsgInfo(MsgKey.ALL_EXECUTOR_DISABLED, loadLang("executor.all_disabled")),
                new KeyAndMsgInfo(MsgKey.REGISTER_LISTENERS, loadLang("listeners.register")),
                new KeyAndMsgInfo(MsgKey.ALREADY_DISABLE_LISTENERS, loadLang("listeners.already_disable")),
                new KeyAndMsgInfo(MsgKey.ALL_LISTENERS_DISABLED, loadLang("listeners.all_disabled")),
                new KeyAndMsgInfo(MsgKey.CONFIG_RELOADED, loadLang("reloaded.config")),
                new KeyAndMsgInfo(MsgKey.INSERT_DEATH_RECORD_FAIL, loadLang("death.log.insert_death_record_fail")),
                new KeyAndMsgInfo(MsgKey.NO_DEATH_RECORD, loadLang("death.log.no_death_record")),
                new KeyAndMsgInfo(MsgKey.READ_DEATH_RECORD, loadLang("death.log.read_death_record")),
                new KeyAndMsgInfo(MsgKey.DIVISION_LINE, loadLang("division_line")),
                new KeyAndMsgInfo(MsgKey.ENTER_BED_CANCELED, loadLang("canceled.enter_bed")),
                new KeyAndMsgInfo(MsgKey.USE_RESPAWN_ANCHOR_CANCELED, loadLang("canceled.use_respawn_anchor")),
                new KeyAndMsgInfo(MsgKey.COMMAND_FREQUENCY_LIMIT, loadLang("frequency_limit")),
                new KeyAndMsgInfo(MsgKey.READ_DEATH_LOG_RESULT, loadLang("death.log.read_death_log_result")),
                new KeyAndMsgInfo(MsgKey.DATABASE_ERROR, loadLang("database.error")),
                new KeyAndMsgInfo(MsgKey.DATABASE_SETUP_ERROR, loadLang("database.setup_error")),
                new KeyAndMsgInfo(MsgKey.DATABASE_DRIVER_ERROR, loadLang("database.driver_error")),
                new KeyAndMsgInfo(MsgKey.HELP_POINTS, loadLang("points.help")),
                new KeyAndMsgInfo(MsgKey.HELP_DEATH, loadLang("death.help")),
                new KeyAndMsgInfo(MsgKey.HELP_WHERE, loadLang("where.help")),
                new KeyAndMsgInfo(MsgKey.HELP_ENDERCHEST, loadLang("enderchest.help")),
                new KeyAndMsgInfo(MsgKey.HELP_FAIR_PVP, loadLang("fair-pvp.help")),
                new KeyAndMsgInfo(MsgKey.ENABLE_FAIR_PVP, loadLang("fair-pvp.enable")),
                new KeyAndMsgInfo(MsgKey.DISABLE_FAIR_PVP, loadLang("fair-pvp.disable")),
                new KeyAndMsgInfo(MsgKey.NO_DEPEND, loadLang("no-depend")),
                new KeyAndMsgInfo(MsgKey.LOADED_DEPEND, loadLang("loaded-depend")),
                new KeyAndMsgInfo(MsgKey.UPDATE_CHECKER_START, loadLang("update_checker.start")),
                new KeyAndMsgInfo(MsgKey.UPDATE_CHECKER_FAIL, loadLang("update_checker.fail")),
                new KeyAndMsgInfo(MsgKey.UPDATE_CHECKER_IS_LATEST, loadLang("update_checker.is_latest")),
                new KeyAndMsgInfo(MsgKey.UPDATE_CHECKER_NEED_UPDATE, loadLang("update_checker.need_update")),
        };
        for (KeyAndMsgInfo info :
                keyAndMsgInfos) {
            messageMap.put(info.key, info.value);
        }
    }
}