package github.viperthanks.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.project.common.constant.RedisKeyConstant;
import github.viperthanks.shortlink.project.common.convention.exception.ClientException;
import github.viperthanks.shortlink.project.common.convention.exception.ServiceException;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkDO;
import github.viperthanks.shortlink.project.dao.mapper.ShortLinkMapper;
import github.viperthanks.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import github.viperthanks.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {


    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 保存到垃圾桶
     *
     */
    @Override
    public void saveRecycleBin(RecycleBinSaveReqDTO requestParam) {
        if (StringUtils.isAnyBlank(requestParam.getGid(), requestParam.getFullShortUrl())) {
            throw new ClientException("参数错误，请重试");
        }
        //组装query wrapper
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
        if (null == shortLinkDO) {
            throw new ClientException("查找不到改短链接数据，请重试");
        }
        //组装update wrapper
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class).eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .set(ShortLinkDO::getEnableStatus, 1);
        boolean update = this.update(updateWrapper);
        if (!update) {
            throw new ServiceException("服务端异常，请重试");
        }
        stringRedisTemplate.delete(RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(requestParam.getFullShortUrl()));

    }
}
