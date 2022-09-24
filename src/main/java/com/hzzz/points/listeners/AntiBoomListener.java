package com.hzzz.points.listeners;

import com.hzzz.points.data_structure.AntiBoomInfo;
import com.hzzz.points.interfaces.NamedListener;
import com.hzzz.points.text.text;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;

import static com.hzzz.points.Points.config;

public class AntiBoomListener implements NamedListener {
    private static final AntiBoomListener INSTANCE = new AntiBoomListener();
    private static final String name = "防爆";

    private static final AntiBoomInfo[] anti_boom_array = {
            new AntiBoomInfo(EntityType.ENDER_CRYSTAL, "anti-boom.ender_crystal.%s"),
            new AntiBoomInfo(EntityType.PRIMED_TNT, "anti-boom.tnt.%s"),
            new AntiBoomInfo(EntityType.MINECART_TNT, "anti-boom.minecart_tnt.%s"),

            new AntiBoomInfo(EntityType.CREEPER, "anti-boom.creeper.%s"),
            new AntiBoomInfo(EntityType.WITHER, "anti-boom.wither.spawn.%s"),

            new AntiBoomInfo(EntityType.WITHER_SKULL, "anti-boom.wither.skull.%s"),
            new AntiBoomInfo(EntityType.FIREBALL, "anti-boom.ghast.%s"),
    };

    public static AntiBoomListener getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return name;
    }

    private AntiBoomListener() {
    }

    @EventHandler
    public void onBoom(EntityExplodeEvent e) {
        String world_name = e.getEntity().getWorld().getName();  // 事件发生的世界

        for (AntiBoomInfo info : anti_boom_array) {  // 遍历
            if (e.getEntity().getType().equals(info.type)  // 检查类型
                    && config.getBoolean(String.format(info.config_path, "enable"), false)) {  // anti-boom.类型.enable

                if (config.getBoolean(String.format(info.config_path, "world"), false)  // anti-boom.类型.enable.world
                        && world_name.equals(config.getString("anti-boom.world-name.world", "world"))) {
                    e.setCancelled(true);

                } else if (config.getBoolean(String.format(info.config_path, "world-nether"), false)  // anti-boom.类型.enable.world-nether
                        && world_name.equals(config.getString("anti-boom.world-name.world-nether", "world_nether"))) {
                    e.setCancelled(true);

                } else if (config.getBoolean(String.format(info.config_path, "world-the-end"), false)  // anti-boom.类型.enable.world-the-end
                        && world_name.equals(config.getString("anti-boom.world-name.world-the-end", "world_the_end"))) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onBad(PlayerBedEnterEvent e) {  // 阻止睡觉
        Player player = e.getPlayer();
        String world_name = player.getWorld().getName();

        if (config.getBoolean("anti-boom.bed.enable", false)) {  // anti-boom.bed.enable
            if (config.getBoolean("anti-boom.bed.world", false)  // 主世界睡觉
                    && world_name.equals(config.getString("anti-boom.world-name.world", "world"))) {
                e.setCancelled(true);
                player.sendMessage(text.enter_bed_canceled);

            } else if (config.getBoolean("anti-boom.bed.world-nether", false)  // 下界睡觉
                    && world_name.equals(config.getString("anti-boom.world-name.world-nether", "world_nether"))) {
                e.setCancelled(true);
                player.sendMessage(text.enter_bed_canceled);

            } else if (config.getBoolean("anti-boom.bed.world-the-end", false)  // 末地睡觉
                    && world_name.equals(config.getString("anti-boom.world-name.world-the-end", "world_the_end"))) {
                e.setCancelled(true);
                player.sendMessage(text.enter_bed_canceled);
            }
        }
    }
}
