package github.viperthanks.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.viperthanks.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
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

    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 单个短链接指定时间内访客记录
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内访客记录
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
