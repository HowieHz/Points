package com.hzzz.points.interfaces;

import org.bukkit.event.Listener;

public interface NamedListener extends Listener {
    /**
     * 获取监听器名字
     *
     * @return 监听器的名字
     */
    String getName();
}
