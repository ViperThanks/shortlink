package github.viperthanks.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.ShortLinkRemoteService;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkRemoteService shortLinkRemoteService;

    /**
     * 短链接创建
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/create", method = RequestMethod.POST)
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/page", method = RequestMethod.GET)
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 查询短链接分组内数量
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/count", method = RequestMethod.GET)
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("gidList") List<String> gidList) {
        return shortLinkRemoteService.listGroupShortLinkCount(gidList);
    }
}
