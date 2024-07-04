package top.howiehz.points.listeners;

import com.google.common.collect.ImmutableList;
import top.howiehz.points.listeners.base_listener.HowieUtilsListener;
import top.howiehz.points.utils.data_structure.AntiBoomInfo;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import top.howiehz.points.utils.message.MsgKey;

import java.util.Objects;

import static top.howiehz.points.utils.message.Lang.getMessage;
import static org.bukkit.Material.*;

/**
 * 防爆监听器
 */
public final class AntiBoomListener extends HowieUtilsListener {
    private static final AntiBoomListener instance = new AntiBoomListener();
    private static final String NAME = "防爆";
    private static final ImmutableList<AntiBoomInfo> antiBoomInfo = ImmutableList.of(
            new AntiBoomInfo(EntityType.ENDER_CRYSTAL, "anti-boom.ender-crystal"),
            new AntiBoomInfo(EntityType.PRIMED_TNT, "anti-boom.tnt"),
            new AntiBoomInfo(EntityType.MINECART_TNT, "anti-boom.minecart-tnt"),
            new AntiBoomInfo(EntityType.CREEPER, "anti-boom.creeper"),
            new AntiBoomInfo(EntityType.WITHER, "anti-boom.wither.spawn"),
            new AntiBoomInfo(EntityType.WITHER_SKULL, "anti-boom.wither.skull"),
            new AntiBoomInfo(EntityType.FIREBALL, "anti-boom.ghast")
    );
    private static final ImmutableList<Material> bedsList = ImmutableList.of(
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
            BLACK_BED);

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
     * 检查在配置文件有没有开启对应世界的防爆
     *
     * @param e          事件
     * @param configPath 配置文件
     * @param worldName  事件发生的世界名
     */
    private boolean checkWorldConfig(Cancellable e, String configPath, String worldName) {
        if (config.getBoolean(configPath + ".enable", false)) {
            if (config.getBoolean(configPath + ".whitelist", false)) {
                if (config.getStringList(configPath + ".world-list").contains(worldName)) {
                    e.setCancelled(true);
                    return true;
                }
            } else {
                if (!config.getStringList(configPath + ".world-list").contains(worldName)) {
                    e.setCancelled(true);
                    return true;
                }
            }
        }
        return false;
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
        for (AntiBoomInfo info : antiBoomInfo) {  // 遍历
            if (e.getEntity().getType().equals(info.type)) {  // 检查类型
                checkWorldConfig(e, info._2, e.getEntity().getWorld().getName());
                return;
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
    public void onWitherDestroyBlocks(@NotNull EntityChangeBlockEvent e) {
        if (e.getEntity().getType().equals(EntityType.WITHER)) {  // 检查类型
            checkWorldConfig(e, "anti-boom.wither.body", e.getEntity().getWorld().getName());
        }
    }

    /**
     * <p>阻止床爆炸(玩家右键床的时候 当右键点的到方块换成null)</p>
     * <p>阻止重生锚爆炸(玩家右键重生锚的时候 当右键点的到方块换成null)</p>
     *
     * @param e 事件
     */
    @EventHandler
    public void onBadOrRespawnAnchor(@NotNull PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        final String worldName = player.getWorld().getName();

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { // 是不是右手
            if (Objects.requireNonNull(e.getClickedBlock()).getType().equals(RESPAWN_ANCHOR)) { // 是不是重生锚
                if (checkWorldConfig(e, "anti-boom.respawn-anchor", worldName)) {
                    player.sendMessage(getMessage(MsgKey.USE_RESPAWN_ANCHOR_CANCELED));
                }
            } else if (bedsList.contains(e.getClickedBlock().getType())) {  // 是不是床
                if (checkWorldConfig(e, "anti-boom.bed", worldName)) {
                    player.sendMessage(getMessage(MsgKey.ENTER_BED_CANCELED));
                }
            }
        }
    }
}
