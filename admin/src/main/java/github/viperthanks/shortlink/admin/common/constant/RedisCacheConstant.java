package github.viperthanks.shortlink.admin.common.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
public final class RedisCacheConstant {
    /**
     * 锁公共前缀
     */
    private static final String LOCK_COMMON_PREFIX = "shortlink:admin:lock:";
    /**
     * 用户注册分布式锁key
     */
    public static final String LOCK_USER_REGISTER_KEY = LOCK_COMMON_PREFIX + "user_register:";
    /**
     * 用户最大分组分布式锁key
     */
    public static final String LOCK_GROUP_CREAT_KEY = LOCK_COMMON_PREFIX + "group_create:%s";

    public static final String LOGIN_USER_PREFIX = "shortlink:admin:login_user:";


    /**
     * 默认分隔符
     */
    private static final String DEFAULT_SEPARATOR = ":";
    /**
     * 获取redis的缓存key
     * @param prefix 前缀，可以省略 {@value DEFAULT_SEPARATOR} 结尾
     * @param bizDefineMark 业务标识
     */
    public static String getCacheKey(String prefix, String bizDefineMark){
        if (!StringUtils.endsWith(prefix, DEFAULT_SEPARATOR)) {
            return prefix + DEFAULT_SEPARATOR + bizDefineMark;
        }
        return prefix + bizDefineMark;
    }

}
