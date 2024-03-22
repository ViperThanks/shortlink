package github.viperthanks.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import github.viperthanks.shortlink.admin.common.biz.user.UserContext;
import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import github.viperthanks.shortlink.admin.dao.entity.GroupDO;
import github.viperthanks.shortlink.admin.dao.mapper.GroupMapper;
import github.viperthanks.shortlink.admin.remote.ShortLinkActualRemoteService;
import github.viperthanks.shortlink.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.RecycleBinRemoveReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * desc: 回收站♻️控制层
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final GroupMapper groupMapper;

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    /**
     * 保存到回收站
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/recycle-bin/save", method = RequestMethod.POST)
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        shortLinkActualRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 回收站恢复短链接
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/recycle-bin/recover", method = RequestMethod.POST)
    public Result<Void> recoverFormRecycleBin(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        shortLinkActualRemoteService.recoverFormRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 回收站移除短链接
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/recycle-bin/remove", method = RequestMethod.POST)
    public Result<Void> removeFormRecycleBin(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        shortLinkActualRemoteService.removeFormRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/recycle-bin/page", method = RequestMethod.GET)
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<String> gidList = groupMapper.selectList(queryWrapper).stream().map(GroupDO::getGid).toList();
        if (ObjectUtils.isEmpty(gidList)) {
            throw new ClientException("用户无分组信息");
        }
        requestParam.setGidList(gidList);
        return shortLinkActualRemoteService.pageRecycleBinShortLink(requestParam);
    }

}
