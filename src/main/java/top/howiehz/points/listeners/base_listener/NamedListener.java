package top.howiehz.points.listeners.base_listener;

import org.bukkit.event.Listener;

/**
 * 带名字的监听器
 */
public interface NamedListener extends Listener {
    /**
     * 获取监听器名字
     *
     * @return 监听器的名字
     */
    String getName();
}
