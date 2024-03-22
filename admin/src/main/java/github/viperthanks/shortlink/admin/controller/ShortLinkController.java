package github.viperthanks.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import github.viperthanks.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.viperthanks.shortlink.admin.remote.ShortLinkActualRemoteService;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkBaseInfoRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import github.viperthanks.shortlink.admin.toolkit.EasyExcelWebUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    /**
     * 短链接创建
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/create", method = RequestMethod.POST)
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkActualRemoteService.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/page", method = RequestMethod.GET)
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkActualRemoteService.pageShortLink(requestParam.getGid(), requestParam.getOrderTag(), requestParam.getCurrent(), requestParam.getSize());
    }

    /**
     * 查询短链接分组内数量
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/count", method = RequestMethod.GET)
    public Result<ArrayList<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("gidList") List<String> gidList) {
        return shortLinkActualRemoteService.listGroupShortLinkCount(gidList);
    }

    /**
     * 修改短链接
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/update", method = RequestMethod.POST)
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkActualRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 批量创建短链接
     */
    @SneakyThrows
    @PostMapping("/api/shortlink/admin/v1/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTOResult = shortLinkActualRemoteService.batchCreateShortLink(requestParam);
        if (shortLinkBatchCreateRespDTOResult.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTOResult.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "批量创建短链接-SaaS短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }
}
