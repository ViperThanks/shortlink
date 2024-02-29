package github.viperthanks.shortlink.project.common.constant;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 28/2/2024
 */
public final class RedisKeyConstant {
    /**
     * 短链接跳转key'
     */
    public static final String GOTO_SHORTLINK_KEY = "shortlink_goto_%s";

    /**
     * 短链接跳转空置key
     */
    public static final String GOTO_SHORTLINK_IS_NULL_KEY = "shortlink_isnull_goto_%s";
    /**
     * 短链接跳转分布式锁key
     */
    public static final String LOCK_GOTO_SHORTLINK_KEY = "shortlink_lock_goto_%s";
}
