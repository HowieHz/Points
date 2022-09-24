package com.hzzz.points.data_structure;

import org.bukkit.command.CommandExecutor;

public class CommandInfo {  // 指令 要注册的执行器 判断是否开启的配置文件节点(为null就是直接开启) 其他的也需要满足的判断
    public String command;
    public CommandExecutor executor;
    public String enabling;
    public boolean and;

    public CommandInfo(String command, CommandExecutor executor, String enabling, boolean and){
        this.command = command;
        this.executor = executor;
        this.enabling = enabling;
        this.and = and;
    }
}
