package com.hzzz.points.utils.data_structure;

import org.bukkit.entity.EntityType;

/**
 * 防爆需要检查的EntityType以及对应在配置文件里面的节点
 */
public class AntiBoomInfo {
    public final EntityType type;
    public final String configPath;

    /**
     * @param type        需要检查的EntityType
     * @param configPath 需要检查的config_path
     */
    public AntiBoomInfo(EntityType type, String configPath) {
        this.type = type;
        this.configPath = configPath;
    }
}
