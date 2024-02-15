package github.viperthanks.shortlink.admin.common.constant;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
public final class RedisCacheConstant {
    private static final String LOCK_COMMON_PREFIX = "shortlink:lock:";
    public static final String LOCK_USER_REGISTER_KEY = LOCK_COMMON_PREFIX + "user-register:";

}
