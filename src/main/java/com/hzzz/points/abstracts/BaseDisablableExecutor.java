package com.hzzz.points.abstracts;

import com.hzzz.points.interfaces.IDisablable;
import org.bukkit.command.CommandExecutor;

public abstract class BaseDisablableExecutor implements CommandExecutor, IDisablable {
    protected boolean disabled;
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean bool) {
        disabled = bool;
    }
}
