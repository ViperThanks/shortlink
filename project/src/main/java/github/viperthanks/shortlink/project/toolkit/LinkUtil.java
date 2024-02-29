package github.viperthanks.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

import static github.viperthanks.shortlink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_EXPIRE_TIME;
import static github.viperthanks.shortlink.project.common.constant.ShortLinkConstant.MAX_CACHE_EXPIRE_TIME;

/**
 * desc: 短链接工具类
 *
 * @author Viper Thanks
 * @since 29/2/2024
 */
public class LinkUtil {
    /**
     * 获取短链接有效时间，返回时间戳
     * 如果超过当前时间返回-1
     */
    public static long getLinkCacheValidDate(Date validate) {
        if (null == validate) {
            return DEFAULT_CACHE_EXPIRE_TIME;
        }
        long res;
        return (res = DateUtil.between(new Date(), validate, DateUnit.MS, false)) > 0 ? Math.min(res, MAX_CACHE_EXPIRE_TIME) : -1L;
    }

}
