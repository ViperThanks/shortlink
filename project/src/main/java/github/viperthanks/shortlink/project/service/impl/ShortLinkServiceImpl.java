package github.viperthanks.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.project.common.constant.RedisKeyConstant;
import github.viperthanks.shortlink.project.common.convention.exception.ClientException;
import github.viperthanks.shortlink.project.common.convention.exception.ServiceException;
import github.viperthanks.shortlink.project.common.database.BaseDO;
import github.viperthanks.shortlink.project.common.enums.ValidDateTypeEnum;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkDO;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkGotoDO;
import github.viperthanks.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import github.viperthanks.shortlink.project.dao.mapper.ShortLinkMapper;
import github.viperthanks.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkPageReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import github.viperthanks.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import github.viperthanks.shortlink.project.service.ShortLinkService;
import github.viperthanks.shortlink.project.toolkit.HashUtil;
import github.viperthanks.shortlink.project.toolkit.SQLResultHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * desc: 链接业务实现层
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkSuffix;
        String fullShortUrlWithProtocol = "http://" + fullShortUrl;
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDate(requestParam.getValidDate())
                .validDateType(requestParam.getValidDateType())
                .describe(requestParam.getDescribe())
                .shortUri(shortLinkSuffix)
                .enableStatus(0)
                .fullShortUrl(fullShortUrl)
                .build();
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        int effectRow = 1;
        try {
            effectRow &= baseMapper.insert(shortLinkDO);
            effectRow &= shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException e) {
            LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(entityClass)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(wrapper);
            if (ObjectUtils.isNotEmpty(hasShortLinkDO)) {
                String warnMsg = String.format("生成短链接重复，检查domain %s 和url %s 是否重复！", requestParam.getDomain(), requestParam.getOriginUrl());
                log.warn(warnMsg);
                throw new ClientException(warnMsg);
            }
        }
        if (SQLResultHelper.isIllegalDMLResult(effectRow)) {
            throw new ServiceException("新增失败，请重试");
        }
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
        return ShortLinkCreateRespDTO.builder()
                .gid(shortLinkDO.getGid())
                .originUrl(shortLinkDO.getOriginUrl())
                .fullShortUrl(fullShortUrlWithProtocol)
                .build();
    }

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页短链接请求参数
     */
    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(entityClass)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getEnableStatus, 0)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resultPage = page(requestParam, wrapper);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO bean = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            if (!StringUtils.startsWithAny(bean.getDomain(), "http://", "https://"))
                bean.setDomain("http://" + bean.getDomain());
            return bean;
        });
    }

    /**
     * 通过gid list获取他的分组内数量
     *
     * @param gidList
     */
    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gidList) {
        QueryWrapper<ShortLinkDO> wrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in(ObjectUtils.isNotEmpty(gidList), "gid", gidList)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> resultDoList = baseMapper.selectMaps(wrapper);
        return BeanUtil.copyToList(resultDoList, ShortLinkGroupCountQueryRespDTO.class);
    }

    /**
     * 更新短链接
     *
     */
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(entityClass)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .set(ShortLinkDO::getValidDateType, requestParam.getValidDateType())
                .set(requestParam.getValidDateType() == ValidDateTypeEnum.PERMANENT, ShortLinkDO::getValidDate, null)
                .set(ShortLinkDO::getDescribe, requestParam.getDescribe())
                .set(ShortLinkDO::getOriginUrl, requestParam.getOriginUrl());
        this.update(updateWrapper);
    }

    /**
     * 跳转短链接
     */
    @Override
    public void restoreUrl(String uri, HttpServletRequest request, HttpServletResponse response) {
        String serverName = request.getServerName();
        final String fullShortUrl = serverName + '/' + uri;
        String originalLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl));
        if (StringUtils.isNotBlank(originalLink)) {
            sendRedirect(response, originalLink);
        }
        //不命中缓存，重启缓存时开启分布式锁，这里做悲观逻辑
        RLock lock = redissonClient.getLock(RedisKeyConstant.LOCK_GOTO_SHORTLINK_KEY.formatted(fullShortUrl));
        try {
            lock.lock();
            originalLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl));
            if (StringUtils.isNotBlank(originalLink)) {
                sendRedirect(response, originalLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO hasShortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
            if (null == hasShortLinkGotoDO) {
                //此处需要做风控
                log.error("根据fullShortUrl ： {} 无法获取 hasShortLinkGotoDO , uri : {} , ip : {}", fullShortUrl, uri, request.getRemoteAddr());
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> shortLinkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getGid, hasShortLinkGotoDO.getGid())
                    .eq(BaseDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(shortLinkQueryWrapper);
            if (null == shortLinkDO) {
                return;
            }
            stringRedisTemplate.opsForValue().set(RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl), shortLinkDO.getOriginUrl());
            sendRedirect(response, shortLinkDO.getOriginUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 发送重定向链接
     */
    private void sendRedirect(HttpServletResponse response, final String url) {
        try {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.sendRedirect(url);
        } catch (IOException e) {
            log.error("重定向时发生IO异常", e);
            throw new ServiceException("服务器异常，请重新刷新");
        }
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix;
        int customGenerateCount = 0;
        do {
            if (customGenerateCount++ > 10) {
                throw new ServiceException("短链接重复次数过多，请稍后重试");
            }
            String originUrl = requestParam.getOriginUrl() + System.currentTimeMillis();
            shortLinkSuffix = HashUtil.hashToBase62(originUrl);
        } while (shortUriCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortLinkSuffix));
        return shortLinkSuffix;
    }
}
