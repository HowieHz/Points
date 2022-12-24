package com.hzzz.points.listeners;

import com.hzzz.points.listeners.base_listener.HowieUtilsListener;
import com.hzzz.points.utils.data_structure.AntiBoomInfo;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

import static com.hzzz.points.utils.message.Lang.getMessage;
import static com.hzzz.points.utils.message.MsgKey.ENTER_BED_CANCELED;
import static com.hzzz.points.utils.message.MsgKey.USE_RESPAWN_ANCHOR_CANCELED;
import static org.bukkit.Material.*;

/**
 * 枚举世界类型
 */
enum WorldName {
    WORLD,
    NETHER,
    THE_END,
}

/**
 * 防爆监听器
 */
public final class AntiBoomListener extends HowieUtilsListener {
    private static final AntiBoomListener instance = new AntiBoomListener();
    private static final String NAME = "防爆";
    private static final AntiBoomInfo[] antiBoomInfo = {
            new AntiBoomInfo(EntityType.ENDER_CRYSTAL, "anti-boom.ender-crystal"),
            new AntiBoomInfo(EntityType.PRIMED_TNT, "anti-boom.tnt"),
            new AntiBoomInfo(EntityType.MINECART_TNT, "anti-boom.minecart-tnt"),

            new AntiBoomInfo(EntityType.CREEPER, "anti-boom.creeper"),
            new AntiBoomInfo(EntityType.WITHER, "anti-boom.wither.spawn"),

            new AntiBoomInfo(EntityType.WITHER_SKULL, "anti-boom.wither.skull"),
            new AntiBoomInfo(EntityType.FIREBALL, "anti-boom.ghast"),
    };
    private static final Material[] beds = {
            WHITE_BED,
            ORANGE_BED,
            MAGENTA_BED,
            LIGHT_BLUE_BED,
            YELLOW_BED,
            LIME_BED,
            PINK_BED,
            GRAY_BED,
            LIGHT_GRAY_BED,
            CYAN_BED,
            PURPLE_BED,
            BLUE_BED,
            BROWN_BED,
            GREEN_BED,
            RED_BED,
            BLACK_BED,
    };

    /**
     * 获取监听器实例
     *
     * @return 监听器实例
     */
    public static AntiBoomListener getInstance() {
        return instance;
    }

    /**
     * 获取监听器名字
     *
     * @return 监听器名字
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 单例 无参数
     */
    private AntiBoomListener() {
    }

    /**
     * <p>实体爆炸监听器(EntityExplodeEvent)</p>
     * 末影水晶
     * tnt
     * 矿车tnt
     * 苦力怕
     * 凋零生成的爆炸
     * 凋零骷髅头
     * 恶魂发射的火球
     *
     * @param e 事件
     */
    @EventHandler
    public void onBoom(EntityExplodeEvent e) {
        final String worldName = e.getEntity().getWorld().getName();  // 事件发生的世界

        for (AntiBoomInfo info : antiBoomInfo) {  // 遍历
            if (e.getEntity().getType().equals(info.type)) {  // 检查类型
                if ((config.getBoolean(info.configParentNode + ".enable", false)) // anti-boom.类型.enable
                        && (config.getBoolean(info.configParentNode + ".world", false)  // anti-boom.类型.enable.world
                        && checkWordName(worldName, WorldName.WORLD))
                        || (config.getBoolean(info.configParentNode + ".world-nether", false)  // anti-boom.类型.enable.world-nether
                        && checkWordName(worldName, WorldName.NETHER))
                        || (config.getBoolean(info.configParentNode + ".world-the-end", false)  // anti-boom.类型.enable.world-the-end
                        && checkWordName(worldName, WorldName.THE_END))) {
                    e.setCancelled(true);
                }
                break;
            }
        }
    }

    /**
     * <p>实体破坏方块(EntityChangeBlockEvent)</p>
     * <p>防止凋零身体移动的破坏</p>
     *
     * @param e 事件
     */
    @EventHandler
    public void onWitherDestroyBlocks(EntityChangeBlockEvent e) {
        final String worldName = e.getEntity().getWorld().getName();  // 事件发生的世界
        final String configParentNode = "anti-boom.wither.body";

        if ((e.getEntity().getType().equals(EntityType.WITHER)  // 检查类型
                && config.getBoolean(configParentNode + ".enable", false)) // anti-boom.类型.enable
                && (config.getBoolean(configParentNode + ".world", false)  // anti-boom.类型.enable.world
                && checkWordName(worldName, WorldName.WORLD))
                || (config.getBoolean(configParentNode + ".world-nether", false)  // anti-boom.类型.enable.world-nether
                && checkWordName(worldName, WorldName.NETHER))
                || (config.getBoolean(configParentNode + ".world-the-end", false)  // anti-boom.类型.enable.world-the-end
                && checkWordName(worldName, WorldName.THE_END))) {
            e.setCancelled(true);
        }
    }

    /**
     * <p>阻止床爆炸(玩家右键床的时候 当右键点的到方块换成null)</p>
     * <p>阻止重生锚爆炸(玩家右键重生锚的时候 当右键点的到方块换成null)</p>
     *
     * @param e 事件
     */
    @EventHandler
    public void onBadOrRespawnAnchor(PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final String worldName = player.getWorld().getName();
        final String configRespawnParentNode = "anti-boom.respawn-anchor";
        final String configBedParentNode = "anti-boom.bed";

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { // 是不是右手
            // anti-boom.respawn-anchor.enable
            if (config.getBoolean(configRespawnParentNode + ".enable", false)
                    && Objects.requireNonNull(e.getClickedBlock()).getType().equals(RESPAWN_ANCHOR) // 是不是重生锚
                    && (config.getBoolean(configRespawnParentNode + ".world", false)  // 主世界使用
                    && checkWordName(worldName, WorldName.WORLD))
                    || (config.getBoolean(configRespawnParentNode + ".world-nether", false)  // 下界使用
                    && checkWordName(worldName, WorldName.NETHER))
                    || (config.getBoolean(configRespawnParentNode + ".world-the-end", false)  // 末地使用
                    && checkWordName(worldName, WorldName.THE_END))) {
                e.setCancelled(true);
                player.sendMessage(getMessage(USE_RESPAWN_ANCHOR_CANCELED));
            }

            // anti-boom.bed.enable
            if (config.getBoolean("anti-boom.bed.enable", false)) {
                for (Material bed : beds) {  // 遍历
                    if (Objects.requireNonNull(e.getClickedBlock()).getType().equals(bed)) {  // 是不是床
                        if ((config.getBoolean(configBedParentNode + ".world", false)  // 主世界睡觉
                                && checkWordName(worldName, WorldName.WORLD))
                                || (config.getBoolean(configBedParentNode + ".world-nether", false)  // 下界睡觉
                                && checkWordName(worldName, WorldName.NETHER))
                                || (config.getBoolean(configBedParentNode + ".world-the-end", false)  // 末地睡觉
                                && checkWordName(worldName, WorldName.THE_END))) {
                            e.setCancelled(true);
                            player.sendMessage(getMessage(ENTER_BED_CANCELED));
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 检查世界名是否符合配置文件中对应世界类型的世界名
     *
     * @param worldName  世界名
     * @param whichWorld 世界类型
     * @return 符合返回true
     */
    private boolean checkWordName(String worldName, WorldName whichWorld) {
        final String configWorldNameParentNode = "anti-boom.world-name";

        switch (whichWorld) {
            case WORLD -> {
                return worldName.equals(config.getString(configWorldNameParentNode + ".world", "world"));
            }
            case NETHER -> {
                return worldName.equals(config.getString(configWorldNameParentNode + ".world-nether", "world_nether"));
            }
            case THE_END -> {
                return worldName.equals(config.getString(configWorldNameParentNode + ".world-the-end", "world_the_end"));
            }
        }
        return true;
    }
}
