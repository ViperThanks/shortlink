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
import github.viperthanks.shortlink.project.toolkit.LinkUtil;
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
import java.util.concurrent.TimeUnit;

import static github.viperthanks.shortlink.project.common.constant.RedisConstant.DEFAULT_IS_NULL_DURATION;

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
        long linkCacheValidDate = LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate());
        if (linkCacheValidDate > 0) {
            stringRedisTemplate.opsForValue().set(
                    RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl),
                    requestParam.getOriginUrl(),
                    linkCacheValidDate,
                    TimeUnit.MILLISECONDS);
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
     * advanced：缓存穿透实现 ， 先判断redis是否存在，不存在则访问布隆过滤器，如果布隆过滤器不存在（布隆过滤器不存在不会有误判）。
     * 如果布隆过滤器存在，则看看缓存空值的key是否存在，如果存在，说明之前穿透过，直接return
     * 如果缓存空值的key不存在，则访问数据库
     * advanced：缓存击穿的实现，如果判断没有命中缓存，在缓存重建的过程中做分布式锁，并且基于双锁实现优化
     */
    @Override
    public void restoreUrl(String uri, HttpServletRequest request, HttpServletResponse response) {
        String serverName = request.getServerName();
        final String fullShortUrl = serverName + '/' + uri;
        String originalLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl));
        if (StringUtils.isNotBlank(originalLink)) {
            sendRedirect(response, originalLink);
        }
        //看看布隆过滤器有没有
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            sendRedirect(response, "/page/notfound");
            return;
        }
        String isNull = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORTLINK_IS_NULL_KEY.formatted(fullShortUrl));
        if (StringUtils.isNotBlank(isNull)) {
            sendRedirect(response, "/page/notfound");
            return;
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
                //缓存穿透的实现
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.GOTO_SHORTLINK_IS_NULL_KEY.formatted(fullShortUrl), "-", DEFAULT_IS_NULL_DURATION);
                log.error("根据fullShortUrl ： {} 无法获取 hasShortLinkGotoDO , uri : {} , ip : {}", fullShortUrl, uri, request.getRemoteAddr());
                sendRedirect(response, "/page/notfound");
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> shortLinkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getGid, hasShortLinkGotoDO.getGid())
                    .eq(BaseDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(shortLinkQueryWrapper);
            if (null == shortLinkDO) {
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.GOTO_SHORTLINK_IS_NULL_KEY.formatted(fullShortUrl), "-", DEFAULT_IS_NULL_DURATION);
                sendRedirect(response, "/page/notfound");
                return;
            }
            long linkCacheValidDate = LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate());
            if (linkCacheValidDate == -1L) {
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.GOTO_SHORTLINK_IS_NULL_KEY.formatted(fullShortUrl), "-", DEFAULT_IS_NULL_DURATION);
                sendRedirect(response, "/page/notfound");
                return;
            }
            stringRedisTemplate.opsForValue().set(
                    RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl),
                    shortLinkDO.getOriginUrl(),
                    linkCacheValidDate,
                    TimeUnit.MILLISECONDS);
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
