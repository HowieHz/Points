package com.hzzz.points.data_structure;

import org.bukkit.command.CommandExecutor;

public class CommandInfo {
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
