package github.viperthanks.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import github.viperthanks.shortlink.admin.remote.dto.ShortLinkRemoteService;
import github.viperthanks.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import github.viperthanks.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * desc: 回收站♻️控制层
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final ShortLinkRemoteService shortLinkRemoteService;

    private final RecycleBinService recycleBinService;


    /**
     * 保存到回收站
     */
    @RequestMapping(value = "/api/shortlink/v1/admin/recycle-bin/save", method = RequestMethod.POST)
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @RequestMapping(value = "/api/shortlink/v1/admin/recycle-bin/page", method = RequestMethod.GET)
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        return recycleBinService.pageRecycleBinShortLink(requestParam);
    }

}
