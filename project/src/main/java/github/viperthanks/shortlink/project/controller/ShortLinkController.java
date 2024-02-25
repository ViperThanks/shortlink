package github.viperthanks.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.viperthanks.shortlink.project.common.convention.result.Result;
import github.viperthanks.shortlink.project.common.convention.result.Results;
import github.viperthanks.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkPageReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import github.viperthanks.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * desc:短链接控制车
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    /**
     * 短链接创建
     */
    @RequestMapping(value = "/api/shortlink/v1/create", method = RequestMethod.POST)
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     */
    @RequestMapping(value = "/api/shortlink/v1/page", method = RequestMethod.GET)
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }
}
