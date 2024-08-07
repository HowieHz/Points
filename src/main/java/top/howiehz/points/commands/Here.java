package top.howiehz.points.commands;

import top.howiehz.points.commands.base_executor.HowieUtilsExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

import static top.howiehz.points.utils.Utils.sendComponentMessageToPlayers;
import static top.howiehz.points.utils.message.Lang.getMessage;
import static top.howiehz.points.utils.message.MsgKey.NO_PERMISSION;
import static top.howiehz.points.utils.message.MsgKey.PLAYER_ONLY;

/**
 * here指令的执行器以及tab补全
 */
public final class Here extends HowieUtilsExecutor {
    private static final Here instance = new Here();

    /**
     * 获取实例
     *
     * @return Instance of executor
     */
    public static Here getInstance() {
        return instance;
    }

    /**
     * 单例 无参数
     */
    private Here() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // 检查执行者
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getMessage(PLAYER_ONLY));
            return true;
        }

        // 权限检查
        if (!checkPermissionTargetSelf(player, "here", "points.command.here")) {
            sender.sendMessage(getMessage(NO_PERMISSION));
            return true;
        }

        // 生成消息并在在公屏发送
        sendComponentMessageToPlayers(buildPlayerCoordinatesMessage("here", player));

        // 给发送者附上发光效果
        if (config.getBoolean("here.glowing.enable", false)) {
            PotionEffect pe = new PotionEffect(PotionEffectType.GLOWING, config.getInt("here.glowing.time", 1200) * 20, 1);  // 20tick*60s=1200
            if (player.hasPotionEffect(pe.getType())) {  // 先检查有没有效果，有就去掉再加效果
                player.removePotionEffect(pe.getType());
            }
            player.addPotionEffect(pe);
        }
        return true;
    }

    @Contract(pure = true)
    @Override
    public @Nullable @Unmodifiable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            // 控制台不注册
            return null;
        }
        /* here
         */
        // 不提示
        return Collections.singletonList("");
    }
}