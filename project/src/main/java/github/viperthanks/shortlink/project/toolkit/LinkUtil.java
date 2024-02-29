package github.viperthanks.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static github.viperthanks.shortlink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_EXPIRE_TIME;

/**
 * desc: 短链接工具类
 *
 * @author Viper Thanks
 * @since 29/2/2024
 */
public class LinkUtil {
    /**
     * 获取短链接有效时间
     */
    public static long getLinkCacheValidDate(Date validate) {
        return Optional.ofNullable(validate)
                .map(each -> DateUtil.between(new Date(), validate, DateUnit.MS))
                .orElse(DEFAULT_CACHE_EXPIRE_TIME);
    }
}
