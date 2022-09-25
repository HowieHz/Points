package com.hzzz.points.data_structure;

import org.bukkit.entity.EntityType;

public class AntiBoomInfo {  // 检查的EntityType 检查的config_path
    public final EntityType type;
    public final String config_path;

    public AntiBoomInfo(EntityType type, String config_path) {
        this.type = type;
        this.config_path = config_path;
    }
}
