package com.hzzz.points.utils.data_structure;

import com.hzzz.points.utils.data_structure.tuple.Tuple2;
import org.bukkit.entity.EntityType;

/**
 * 防爆需要检查的EntityType以及对应在配置文件里面的节点
 */
public class AntiBoomInfo extends Tuple2<EntityType, String> {
    public final EntityType type;
    public final String configParentNode;

    /**
     * @param type       需要检查的EntityType
     * @param configPath 需要检查的configPath
     */
    public AntiBoomInfo(EntityType type, String configParentNode) {
        super(type, configParentNode);
        this.type = type;
        this.configParentNode = configParentNode;
    }
}
