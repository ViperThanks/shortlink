package github.viperthanks.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.convention.exception.ServiceException;
import github.viperthanks.shortlink.admin.dao.entity.GroupDO;
import github.viperthanks.shortlink.admin.dao.mapper.GroupMapper;
import github.viperthanks.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import github.viperthanks.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import github.viperthanks.shortlink.admin.service.GroupService;
import github.viperthanks.shortlink.admin.toolkit.RandomStringGenerator;
import github.viperthanks.shortlink.admin.toolkit.SQLResultHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * desc: 短链接service实现类
 *
 * @author Viper Thanks
 * @since 19/2/2024
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    /**
     * 默认的gid长度
     */
    private static final int GID_DEFAULT_LEN = 6;
    @Override
    public void saveGroup(ShortLinkGroupSaveReqDTO requestParam) {
        String groupName = requestParam.getName();
        //这里要做拓展
        if (StringUtils.length(groupName) == 0) {
            throw new ClientException("短链接名不能为空白");
        }
        String gid = generateUniqueGid();
        //构造GroupDO
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .sortOrder(0)
                .name(groupName)
                .build();
        int effectRow = baseMapper.insert(groupDO);
        if (SQLResultHelper.isIllegalDMLResult(effectRow)) {
            throw new ServiceException("用户插入失败，请重试");
        }
    }

    /**
     * 生成数据库唯一gid
     */
    @Override
    public String generateUniqueGid(){
        String gid;
        do {
            gid = generateGid();
        } while (isGidExistFromDB(gid));
        return gid;
    }

    /**
     * 获取短链接分组
     */
    @Override
    public List<ShortLinkGroupRespDTO> getGroupList() {
        //todo 获取用户名
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .isNull(GroupDO::getUsername)
                .orderByDesc(Arrays.asList(GroupDO::getSortOrder,GroupDO::getUpdateTime));
        List<GroupDO> groupDOList = baseMapper.selectList(wrapper);
        return BeanUtil.copyToList(groupDOList,ShortLinkGroupRespDTO.class);
    }

    private boolean isGidExistFromDB(String gid) {
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                //todo 这里需要从网关获取用户名称
                .eq(GroupDO::getUsername, null);
        long count = baseMapper.selectCount(wrapper);
        return count > 0;
    }

    /**
     * 生成gid
     *
     * @return 长度为 size 的随机字符串
     */
    @Override
    public String generateGid() {
        return RandomStringGenerator.generate(GID_DEFAULT_LEN);
    }
}
