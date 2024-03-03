package github.viperthanks.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import github.viperthanks.shortlink.admin.common.biz.user.UserContext;
import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.dao.entity.GroupDO;
import github.viperthanks.shortlink.admin.dao.mapper.GroupMapper;
import github.viperthanks.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import github.viperthanks.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import github.viperthanks.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 2/3/2024
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final GroupMapper groupMapper;
    private final RecycleBinService recycleBinService = new RecycleBinService() {};

    /**
     * 回收站短链接分页查询
     *
     * @param requestParam 短链接分页查询参数
     */
    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<String> gidList = groupMapper.selectList(queryWrapper).stream().map(GroupDO::getGid).toList();
        if (ObjectUtils.isEmpty(gidList)) {
            throw new ClientException("用户无分组信息");
        }
        requestParam.setGidList(gidList);
        return recycleBinService.pageRecycleBinShortLink(requestParam);
    }
}
