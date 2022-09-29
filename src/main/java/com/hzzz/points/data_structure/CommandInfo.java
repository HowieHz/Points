package com.hzzz.points.data_structure;

import org.bukkit.command.TabExecutor;

/**
 * 需要注册的指令执行器以及一些相关信息
 */
public class CommandInfo {
    public final String command;
    public final TabExecutor executor;
    public final String enabling;
    public final boolean and;

    /**
     * @param command  指令
     * @param executor 要注册的执行器
     * @param enabling 判断是否开启的配置文件节点(为null就是直接开启)
     * @param and      其他的也需要满足的条件
     */
    public CommandInfo(String command, TabExecutor executor, String enabling, boolean and) {
        this.command = command;
        this.executor = executor;
        this.enabling = enabling;
        this.and = and;
    }
}
