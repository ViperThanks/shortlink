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
    public static final String GOTO_SHORTLINK_KEY = "shortlink:goto:%s";

    /**
     * 短链接跳转空置key
     */
    public static final String GOTO_SHORTLINK_IS_NULL_KEY = "shortlink:isnull_goto:%s";
    /**
     * 短链接跳转分布式锁key
     */
    public static final String LOCK_GOTO_SHORTLINK_KEY = "shortlink:lock:goto:%s";


    /**
     * 短链接基本属性UV key
     */
    public static final String SHORTLINK_STATS_UV_KEY = "shortlink:stats:uv:%s";

    /**
     * 短链接基本属性UV key
     */
    public static final String SHORTLINK_STATS_UIP_KEY = "shortlink:stats:uip:%s";

    /**
     * 短链接今日访问基本属性UV key
     */
    public static final String SHORTLINK_STATS_TODAY_UV_KEY = "shortlink:stats:today:uv:%s:%s";

    /**
     * 短链接今日访问基本属性UV key
     */
    public static final String SHORTLINK_STATS_TODAY_UIP_KEY = "shortlink:stats:today:uip:%s:%s";


    /**
     * 短链接修改分组 ID 锁前缀 Key
     */
    public static final String LOCK_GID_UPDATE_KEY = "shortlink:lock:update_gid:%s";

    /**
     * 短链接延迟队列消费统计 Key
     */
    public static final String DELAY_QUEUE_STATS_KEY = "shortlink:delay_queue:stats";

    /**
     * 短链接消息队列 topic key
     */
    public static final String SHORTLINK_STREAM_TOPIC_KEY = "short_link:stats-stream";

    /**
     * 短链接消息队列 group key
     */
    public static final String SHORTLINK_STREAM_GROUP_KEY = "short_link:stats-stream:only-group";
}
