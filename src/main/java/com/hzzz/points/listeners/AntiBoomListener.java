package com.hzzz.points.listeners;

import com.hzzz.points.data_structure.AntiBoomInfo;
import com.hzzz.points.interfaces.NamedListener;
import com.hzzz.points.text.text;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

import static com.hzzz.points.Points.config;
import static org.bukkit.Material.*;

/**
 * 防爆监听器
 */
public class AntiBoomListener implements NamedListener {
    private static final AntiBoomListener INSTANCE = new AntiBoomListener();
    private static final String name = "防爆";
    private static final AntiBoomInfo[] anti_boom_info = {
            new AntiBoomInfo(EntityType.ENDER_CRYSTAL, "anti-boom.ender-crystal.%s"),
            new AntiBoomInfo(EntityType.PRIMED_TNT, "anti-boom.tnt.%s"),
            new AntiBoomInfo(EntityType.MINECART_TNT, "anti-boom.minecart-tnt.%s"),

            new AntiBoomInfo(EntityType.CREEPER, "anti-boom.creeper.%s"),
            new AntiBoomInfo(EntityType.WITHER, "anti-boom.wither.spawn.%s"),

            new AntiBoomInfo(EntityType.WITHER_SKULL, "anti-boom.wither.skull.%s"),
            new AntiBoomInfo(EntityType.FIREBALL, "anti-boom.ghast.%s"),
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
        return INSTANCE;
    }

    /**
     * 获取监听器名字
     *
     * @return 监听器名字
     */
    @Override
    public String getName() {
        return name;
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
        String world_name = e.getEntity().getWorld().getName();  // 事件发生的世界

        for (AntiBoomInfo info : anti_boom_info) {  // 遍历
            if (e.getEntity().getType().equals(info.type)) {  // 检查类型
                if ((config.getBoolean(String.format(info.config_path, "enable"), false)) // anti-boom.类型.enable
                        && (config.getBoolean(String.format(info.config_path, "world"), false)  // anti-boom.类型.enable.world
                        && world_name.equals(config.getString("anti-boom.world-name.world", "world")))
                        || (config.getBoolean(String.format(info.config_path, "world-nether"), false)  // anti-boom.类型.enable.world-nether
                        && world_name.equals(config.getString("anti-boom.world-name.world-nether", "world_nether")))
                        || (config.getBoolean(String.format(info.config_path, "world-the-end"), false)  // anti-boom.类型.enable.world-the-end
                        && world_name.equals(config.getString("anti-boom.world-name.world-the-end", "world_the_end")))) {
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
        String world_name = e.getEntity().getWorld().getName();  // 事件发生的世界

        if ((e.getEntity().getType().equals(EntityType.WITHER)  // 检查类型
                && config.getBoolean("anti-boom.wither.body.enable", false)) // anti-boom.类型.enable
                && (config.getBoolean("anti-boom.wither.body.world", false)  // anti-boom.类型.enable.world
                && world_name.equals(config.getString("anti-boom.world-name.world", "world")))
                || (config.getBoolean("anti-boom.wither.body.world-nether", false)  // anti-boom.类型.enable.world-nether
                && world_name.equals(config.getString("anti-boom.world-name.world-nether", "world_nether")))
                || (config.getBoolean("anti-boom.wither.body.world-the-end", false)  // anti-boom.类型.enable.world-the-end
                && world_name.equals(config.getString("anti-boom.world-name.world-the-end", "world_the_end")))) {
            e.setCancelled(true);
        }
    }

    /**
     * <p>阻止床爆炸(玩家右键床的时候 当右键点的到方块换成null)</p>
     *
     * @param e 事件
     */
    @EventHandler
    public void onBad(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        String world_name = player.getWorld().getName();

        if (config.getBoolean("anti-boom.bed.enable", false)) {  // anti-boom.bed.enable
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {  // 是不是右手
                for (Material bed : beds) {  // 遍历
                    if (Objects.requireNonNull(e.getClickedBlock()).getType().equals(bed)) {  // 是不是床
                        if ((config.getBoolean("anti-boom.bed.world", false)  // 主世界睡觉
                                && world_name.equals(config.getString("anti-boom.world-name.world", "world")))
                                || (config.getBoolean("anti-boom.bed.world-nether", false)  // 下界睡觉
                                && world_name.equals(config.getString("anti-boom.world-name.world-nether", "world_nether")))
                                || (config.getBoolean("anti-boom.bed.world-the-end", false)  // 末地睡觉
                                && world_name.equals(config.getString("anti-boom.world-name.world-the-end", "world_the_end")))) {
                            e.setCancelled(true);
                            player.sendMessage(text.enter_bed_canceled);
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * <p>阻止重生锚爆炸(玩家右键重生锚的时候 当右键点的到方块换成null)</p>
     *
     * @param e 事件
     */
    @EventHandler
    public void onRespawnAnchor(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        String world_name = player.getWorld().getName();

        if ((config.getBoolean("anti-boom.respawn-anchor.enable", false))  // anti-boom.respawn-anchor.enable
                && (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) // 是不是右手
                && (Objects.requireNonNull(e.getClickedBlock()).getType().equals(RESPAWN_ANCHOR)) // 是不是重生锚
                && (config.getBoolean("anti-boom.respawn-anchor.world", false)  // 主世界使用
                && world_name.equals(config.getString("anti-boom.world-name.world", "world"))
                || (config.getBoolean("anti-boom.respawn-anchor.world-nether", false)  // 下界使用
                && world_name.equals(config.getString("anti-boom.world-name.world-nether", "world_nether")))
                || (config.getBoolean("anti-boom.respawn-anchor.world-the-end", false)  // 末地使用
                && world_name.equals(config.getString("anti-boom.world-name.world-the-end", "world_the_end"))))) {
            e.setCancelled(true);
            player.sendMessage(text.use_respawn_anchor_canceled);
        }
    }
}
