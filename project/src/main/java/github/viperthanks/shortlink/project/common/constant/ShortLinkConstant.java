package github.viperthanks.shortlink.project.common.constant;

import java.util.concurrent.TimeUnit;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 29/2/2024
 */
public class ShortLinkConstant {
    /**
     * 永久短链接默认缓存时间
     */
    public static final long DEFAULT_CACHE_EXPIRE_TIME = TimeUnit.DAYS.toMillis(30);

    /**
     * 有效期短链接最大缓存时间
     */
    public static final long MAX_CACHE_EXPIRE_TIME = TimeUnit.DAYS.toMillis(30);
}
