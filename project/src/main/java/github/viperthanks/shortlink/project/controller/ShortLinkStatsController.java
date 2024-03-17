package github.viperthanks.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.viperthanks.shortlink.project.common.convention.result.Result;
import github.viperthanks.shortlink.project.common.convention.result.Results;
import github.viperthanks.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import github.viperthanks.shortlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc:短链接监控控制
 *
 * @author Viper Thanks
 * @since 8/3/2024
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    private final ShortLinkStatsService shortLinkStatsService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @RequestMapping(value = "/api/shortLink/v1/stats", method = RequestMethod.GET)
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.oneShortLinkStats(requestParam));
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @RequestMapping(value = "/api/shortLink/v1/stats/group", method = RequestMethod.GET)
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return Results.success(shortLinkStatsService.groupShortLinkStats(requestParam));
    }

    /**
     * 单个短链接指定时间内访客记录
     */
    @RequestMapping(value = "/api/shortLink/v1/stats/access-record", method = RequestMethod.GET)
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return Results.success(shortLinkStatsService.shortLinkStatsAccessRecord(requestParam));
    }
}
