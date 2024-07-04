package top.howiehz.points.utils.data_structure;

import top.howiehz.points.utils.message.MsgKey;

/**
 * <p>用于初始化文字</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @version 0.2.5
 * @since 2022-10-23 16:56
 */
public class KeyAndMsgInfo {
    public final MsgKey key;
    public final String value;

    /**
     * @param key   键
     * @param value 值
     */
    public KeyAndMsgInfo(MsgKey key, String value) {
        this.key = key;
        this.value = value;
    }
}
