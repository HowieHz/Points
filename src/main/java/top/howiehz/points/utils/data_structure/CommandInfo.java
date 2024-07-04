package top.howiehz.points.utils.data_structure;

import top.howiehz.points.utils.data_structure.tuple.Tuple4;
import org.bukkit.command.TabExecutor;

import java.util.function.Supplier;

/**
 * 需要注册的指令执行器以及一些相关信息
 */
public class CommandInfo extends Tuple4<String, Supplier<TabExecutor>, String, Supplier<Boolean>> {
    public final String command;
    public final Supplier<TabExecutor> executor;
    public final String enabling;
    public final Supplier<Boolean> and;

    /**
     * @param command  指令
     * @param executor 要注册的执行器
     * @param enabling 判断是否开启的配置文件节点(为null就是直接开启)
     * @param and      其他的也需要满足的条件
     */
    public CommandInfo(String command, Supplier<TabExecutor> executor, String enabling, Supplier<Boolean> and) {
        super(command, executor, enabling, and);
        this.command = command;
        this.executor = executor;
        this.enabling = enabling;
        this.and = and;
    }
}
