package github.viperthanks.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.admin.common.biz.user.UserContext;
import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.convention.exception.ServiceException;
import github.viperthanks.shortlink.admin.dao.entity.GroupDO;
import github.viperthanks.shortlink.admin.dao.mapper.GroupMapper;
import github.viperthanks.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import github.viperthanks.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import github.viperthanks.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
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
                .username(UserContext.getUsername())
                .name(groupName)
                .build();
        int effectRow = baseMapper.insert(groupDO);
        if (SQLResultHelper.isIllegalDMLResult(effectRow)) {
            throw new ServiceException("插入失败，请重试");
        }
    }

    private static final int GID_MAX_TRY_LIMIT = 10;
    /**
     * 生成数据库唯一gid
     */
    @Override
    public String generateUniqueGid(){
        String gid;

        int count = 1;
        do {
            gid = generateGid();
            count++;
        } while (isGidExistFromDB(gid));
        if (count > GID_MAX_TRY_LIMIT) {
            log.warn("生成数据库唯一gid循环次数大于10次");
        }
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
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(Arrays.asList(GroupDO::getSortOrder, GroupDO::getUpdateTime));
        List<GroupDO> groupDOList = baseMapper.selectList(wrapper);
        return BeanUtil.copyToList(groupDOList,ShortLinkGroupRespDTO.class);
    }

    /**
     * 修改短链接组
     */
    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        if (StringUtils.isAnyBlank(requestParam.getGid(), requestParam.getName())) {
            throw new ClientException("参数错误");
        }
        LambdaQueryWrapper<GroupDO> countWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getName, requestParam.getName())
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        Long count = baseMapper.selectCount(countWrapper);
        if (SQLResultHelper.isLegalCountResult(count)) {
            throw new ClientException("短链接组有重复的名字");
        }
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .set(GroupDO::getName, requestParam.getName());
        int update = baseMapper.update(BeanUtil.toBean(requestParam, GroupDO.class), updateWrapper);
        if (SQLResultHelper.isIllegalDMLResult(update)) {
            throw new ServiceException("修改失败，请重试");
        }
    }

    @Override
    public void deleteGroup(String gid) {
        if (StringUtils.isAnyBlank(gid)) {
            throw new ClientException("参数错误");
        }
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        int update = baseMapper.update(groupDO, updateWrapper);
        if (SQLResultHelper.isIllegalDMLResult(update)) {
            throw new ServiceException("删除失败，请重试");
        }
    }

    /**
     * 短链接分组排序
     */
    @Override
    public void groupSort(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each ->
                {
                    LambdaUpdateWrapper<GroupDO> wrapper = Wrappers.lambdaUpdate(GroupDO.class)
                            .eq(GroupDO::getUsername, UserContext.getUsername())
                            .eq(GroupDO::getDelFlag, 0)
                            .eq(GroupDO::getGid, each.getGid())
                            .set(GroupDO::getSortOrder, each.getSortOrder());
                    this.update(wrapper);
                }
        );
    }

    /**
     * 该gid是否存在数据库
     */
    private boolean isGidExistFromDB(String gid) {
        return isGidExistFromDB(gid, UserContext.getUsername());
    }

    /**
     * 该gid是否存在数据库
     * note: 因为db存在唯一索引idx_unique_gid_username
     */
    private boolean isGidExistFromDB(String gid, String username) {
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username);
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
