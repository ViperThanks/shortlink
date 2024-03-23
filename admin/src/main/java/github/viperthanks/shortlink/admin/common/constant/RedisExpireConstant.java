package github.viperthanks.shortlink.admin.common.constant;

import java.time.Duration;

/**
 * desc: redis的过期时间常量类
 *
 * @author Viper Thanks
 * @since 23/3/2024
 */
public final class RedisExpireConstant {
    public static final Duration USER_LOGIN_EXPIRE_TIME = Duration.ofMinutes(30);
    public static final Duration USER_LOGIN_REFRESH_EXPIRE_TIME = Duration.ofMinutes(30);
}
