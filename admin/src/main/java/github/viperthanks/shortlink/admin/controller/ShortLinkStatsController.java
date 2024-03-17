package github.viperthanks.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.remote.dto.ShortLinkRemoteService;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 16/3/2024
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ShortLinkStatsController {
    private final ShortLinkRemoteService shortLinkRemoteService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @RequestMapping(value = "/api/shortLink/admin/v1/stats", method = RequestMethod.GET)
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoteService.oneShortLinkStats(requestParam);
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @RequestMapping(value = "/api/shortLink/admin/v1/stats/group", method = RequestMethod.GET)
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return shortLinkRemoteService.groupShortLinkStats(requestParam);
    }


    /**
     * 单个短链接指定时间内访客记录
     */
    @RequestMapping(value = "/api/shortLink/admin/v1/stats/access-record", method = RequestMethod.GET)
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteService.shortLinkStatsAccessRecord(requestParam);
    }

    /**
     * 访问分组短链接指定时间内访客记录
     */
    @RequestMapping(value = "/api/shortLink/admin/v1/stats/access-record/group", method = RequestMethod.GET)
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteService.groupShortLinkStatsAccessRecord(requestParam);
    }

}
