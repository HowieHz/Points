package com.hzzz.points.data_structure;

import org.bukkit.entity.EntityType;

/**
 * 防爆需要检查的EntityType以及对应在配置文件里面的节点
 */
public class AntiBoomInfo {
    public final EntityType type;
    public final String config_path;

    /**
     * @param type        需要检查的EntityType
     * @param config_path 需要检查的config_path
     */
    public AntiBoomInfo(EntityType type, String config_path) {
        this.type = type;
        this.config_path = config_path;
    }
}
