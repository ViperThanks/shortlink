package github.viperthanks.shortlink.project.service;

import github.viperthanks.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * desc: 短链接监控接口层
 *
 * @author Viper Thanks
 * @since 8/3/2024
 */
public interface ShortLinkStatsService {

    /**
     * 访问单个短链接指定时间内监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);
}
