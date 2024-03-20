package github.viperthanks.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import github.viperthanks.shortlink.project.common.cofig.GotoDomainWhiteListProperties;
import github.viperthanks.shortlink.project.common.constant.RedisKeyConstant;
import github.viperthanks.shortlink.project.common.convention.exception.ClientException;
import github.viperthanks.shortlink.project.common.convention.exception.ServiceException;
import github.viperthanks.shortlink.project.common.database.BaseDO;
import github.viperthanks.shortlink.project.common.enums.ValidDateTypeEnum;
import github.viperthanks.shortlink.project.dao.entity.*;
import github.viperthanks.shortlink.project.dao.mapper.*;
import github.viperthanks.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkPageReqDTO;
import github.viperthanks.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import github.viperthanks.shortlink.project.dto.resp.*;
import github.viperthanks.shortlink.project.mq.producter.DelayShortLinkStatsProducer;
import github.viperthanks.shortlink.project.mq.producter.ShortLinkStatsSaveProducer;
import github.viperthanks.shortlink.project.service.LinkStatsTodayService;
import github.viperthanks.shortlink.project.service.ShortLinkService;
import github.viperthanks.shortlink.project.toolkit.GaoDeUtil;
import github.viperthanks.shortlink.project.toolkit.HashUtil;
import github.viperthanks.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static github.viperthanks.shortlink.project.common.constant.RedisConstant.DEFAULT_IS_NULL_DURATION;
import static github.viperthanks.shortlink.project.common.constant.RedisKeyConstant.*;

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
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final GaoDeUtil gaoDeUtil;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final TransactionTemplate transactionTemplate;
    private final LinkStatsTodayService linkStatsTodayService;
    private final DelayShortLinkStatsProducer delayShortLinkStatsProducer;
    private final GotoDomainWhiteListProperties gotoDomainWhiteListProperties;
    private final ShortLinkStatsSaveProducer shortLinkStatsSaveProducer;

    /**
     * 默认域名
     */
    @Value("${shortlink.domain.default}")
    private String defaultDomain;

    //transactional template嵌套太多层了

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        verificationWhitelist(requestParam.getOriginUrl());
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = defaultDomain + "/" + shortLinkSuffix;
        String fullShortUrlWithProtocol = "http://" + fullShortUrl;
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(defaultDomain)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDate(requestParam.getValidDate())
                .validDateType(requestParam.getValidDateType())
                .describe(requestParam.getDescribe())
                .shortUri(shortLinkSuffix)
                .enableStatus(0)
                .delTime(0L)
                .fullShortUrl(fullShortUrl)
                .favicon(getFavicon(requestParam.getOriginUrl()))
                .build();
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        try {
            Boolean res = transactionTemplate.execute(action -> {
                Object savepoint = action.createSavepoint();
                try {
                    int insert = baseMapper.insert(shortLinkDO);
                    int insert1 = shortLinkGotoMapper.insert(shortLinkGotoDO);
                    if (!(insert > 0 && insert1 > 0)) {
                        action.rollbackToSavepoint(savepoint);
                        log.error("创建短链接出现异常 ： 插入link 表返回值为 ： {}  插入goto表的返回值为{} ", insert, insert1);
                        return false;
                    }
                    return true;
                } catch (TransactionException e) {
                    log.error("创建短链接出现异常 ：", e);
                    action.rollbackToSavepoint(savepoint);
                    return false;
                }
            });
            if (BooleanUtils.isNotTrue(res)) {
                throw new ServiceException("新增失败，请稍后重试");
            }
        } catch (DuplicateKeyException e) {
            String warnMsg = String.format("生成短链接重复，检查domain %s 和url %s 是否重复！", defaultDomain, requestParam.getOriginUrl());
            log.warn(warnMsg);
            throw new ClientException(warnMsg);
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
        IPage<ShortLinkDO> resultPage = baseMapper.pageLink(requestParam);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO bean = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            if (!StringUtils.startsWithAny(bean.getDomain(), "http://", "https://")) {
                bean.setDomain("http://" + bean.getDomain());
            }
            return bean;
        });
    }

    /**
     * 通过gid list获取他的分组内数量

     */
    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                .eq("del_time", 0L)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    /**
     * 更新短链接
     *
     */
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        verificationWhitelist(requestParam.getOriginUrl());
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ClientException("短链接记录不存在");
        }
        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), ValidDateTypeEnum.PERMANENT), ShortLinkDO::getValidDate, null);
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .domain(hasShortLinkDO.getDomain())
                    .shortUri(hasShortLinkDO.getShortUri())
                    .favicon(hasShortLinkDO.getFavicon())
                    .createdType(hasShortLinkDO.getCreatedType())
                    .gid(requestParam.getGid())
                    .originUrl(requestParam.getOriginUrl())
                    .describe(requestParam.getDescribe())
                    .validDateType(requestParam.getValidDateType().getIndex())
                    .validDate(requestParam.getValidDate())
                    .build();
            baseMapper.update(shortLinkDO, updateWrapper);
        } else {
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, requestParam.getFullShortUrl()));
            RLock rLock = readWriteLock.writeLock();
            if (!rLock.tryLock()) {
                throw new ServiceException("短链接正在被访问，请稍后再试...");
            }
            try {
                LambdaUpdateWrapper<ShortLinkDO> linkUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getDelTime, 0L)
                        .eq(ShortLinkDO::getEnableStatus, 0);
                ShortLinkDO delShortLinkDO = ShortLinkDO.builder()
                        .delTime(System.currentTimeMillis())
                        .build();
                delShortLinkDO.setDelFlag(1);
                baseMapper.update(delShortLinkDO, linkUpdateWrapper);
                ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .domain(defaultDomain)
                        .originUrl(requestParam.getOriginUrl())
                        .gid(requestParam.getGid())
                        .createdType(hasShortLinkDO.getCreatedType())
                        .validDateType(requestParam.getValidDateType().getIndex())
                        .validDate(requestParam.getValidDate())
                        .describe(requestParam.getDescribe())
                        .shortUri(hasShortLinkDO.getShortUri())
                        .enableStatus(hasShortLinkDO.getEnableStatus())
                        .totalPv(hasShortLinkDO.getTotalPv())
                        .totalUv(hasShortLinkDO.getTotalUv())
                        .totalUip(hasShortLinkDO.getTotalUip())
                        .fullShortUrl(hasShortLinkDO.getFullShortUrl())
                        .favicon(getFavicon(requestParam.getOriginUrl()))
                        .delTime(0L)
                        .build();
                baseMapper.insert(shortLinkDO);
                LambdaQueryWrapper<LinkStatsTodayDO> statsTodayQueryWrapper = Wrappers.lambdaQuery(LinkStatsTodayDO.class)
                        .eq(LinkStatsTodayDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkStatsTodayDO::getGid, hasShortLinkDO.getGid())
                        .eq(LinkStatsTodayDO::getDelFlag, 0);
                List<LinkStatsTodayDO> linkStatsTodayDOList = linkStatsTodayMapper.selectList(statsTodayQueryWrapper);
                if (CollUtil.isNotEmpty(linkStatsTodayDOList)) {
                    linkStatsTodayMapper.deleteBatchIds(linkStatsTodayDOList.stream()
                            .map(LinkStatsTodayDO::getId)
                            .toList()
                    );
                    linkStatsTodayDOList.forEach(each -> each.setGid(requestParam.getGid()));
                    linkStatsTodayService.saveBatch(linkStatsTodayDOList);
                }
                LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkGotoDO::getGid, hasShortLinkDO.getGid());
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
                shortLinkGotoMapper.deleteById(shortLinkGotoDO.getId());
                shortLinkGotoDO.setGid(requestParam.getGid());
                shortLinkGotoMapper.insert(shortLinkGotoDO);
                LambdaUpdateWrapper<LinkAccessStatsDO> linkAccessStatsUpdateWrapper = Wrappers.lambdaUpdate(LinkAccessStatsDO.class)
                        .eq(LinkAccessStatsDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkAccessStatsDO::getGid, hasShortLinkDO.getGid())
                        .eq(LinkAccessStatsDO::getDelFlag, 0);
                LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                        .gid(requestParam.getGid())
                        .build();
                linkAccessStatsMapper.update(linkAccessStatsDO, linkAccessStatsUpdateWrapper);
                LambdaUpdateWrapper<LinkLocaleStatsDO> linkLocaleStatsUpdateWrapper = Wrappers.lambdaUpdate(LinkLocaleStatsDO.class)
                        .eq(LinkLocaleStatsDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkLocaleStatsDO::getGid, hasShortLinkDO.getGid())
                        .eq(LinkLocaleStatsDO::getDelFlag, 0);
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .gid(requestParam.getGid())
                        .build();
                linkLocaleStatsMapper.update(linkLocaleStatsDO, linkLocaleStatsUpdateWrapper);
                LambdaUpdateWrapper<LinkOsStatsDO> linkOsStatsUpdateWrapper = Wrappers.lambdaUpdate(LinkOsStatsDO.class)
                        .eq(LinkOsStatsDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkOsStatsDO::getGid, hasShortLinkDO.getGid())
                        .eq(LinkOsStatsDO::getDelFlag, 0);
                LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                        .gid(requestParam.getGid())
                        .build();
                linkOsStatsMapper.update(linkOsStatsDO, linkOsStatsUpdateWrapper);
                LambdaUpdateWrapper<LinkBrowserStatsDO> linkBrowserStatsUpdateWrapper = Wrappers.lambdaUpdate(LinkBrowserStatsDO.class)
                        .eq(LinkBrowserStatsDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkBrowserStatsDO::getGid, hasShortLinkDO.getGid())
                        .eq(LinkBrowserStatsDO::getDelFlag, 0);
                LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                        .gid(requestParam.getGid())
                        .build();
                linkBrowserStatsMapper.update(linkBrowserStatsDO, linkBrowserStatsUpdateWrapper);
                LambdaUpdateWrapper<LinkDeviceStatsDO> linkDeviceStatsUpdateWrapper = Wrappers.lambdaUpdate(LinkDeviceStatsDO.class)
                        .eq(LinkDeviceStatsDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkDeviceStatsDO::getGid, hasShortLinkDO.getGid())
                        .eq(LinkDeviceStatsDO::getDelFlag, 0);
                LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                        .gid(requestParam.getGid())
                        .build();
                linkDeviceStatsMapper.update(linkDeviceStatsDO, linkDeviceStatsUpdateWrapper);
                LambdaUpdateWrapper<LinkNetworkStatsDO> linkNetworkStatsUpdateWrapper = Wrappers.lambdaUpdate(LinkNetworkStatsDO.class)
                        .eq(LinkNetworkStatsDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkNetworkStatsDO::getGid, hasShortLinkDO.getGid())
                        .eq(LinkNetworkStatsDO::getDelFlag, 0);
                LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                        .gid(requestParam.getGid())
                        .build();
                linkNetworkStatsMapper.update(linkNetworkStatsDO, linkNetworkStatsUpdateWrapper);
                LambdaUpdateWrapper<LinkAccessLogsDO> linkAccessLogsUpdateWrapper = Wrappers.lambdaUpdate(LinkAccessLogsDO.class)
                        .eq(LinkAccessLogsDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkAccessLogsDO::getGid, hasShortLinkDO.getGid())
                        .eq(LinkAccessLogsDO::getDelFlag, 0);
                LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                        .gid(requestParam.getGid())
                        .build();
                linkAccessLogsMapper.update(linkAccessLogsDO, linkAccessLogsUpdateWrapper);
            } finally {
                rLock.unlock();
            }
        }
        if (!Objects.equals(hasShortLinkDO.getValidDateType(), requestParam.getValidDateType())
                || !Objects.equals(hasShortLinkDO.getValidDate(), requestParam.getValidDate())) {
            stringRedisTemplate.delete(String.format(GOTO_SHORTLINK_KEY, requestParam.getFullShortUrl()));
            if (hasShortLinkDO.getValidDate() != null && hasShortLinkDO.getValidDate().before(new Date())) {
                if (Objects.equals(requestParam.getValidDateType(), ValidDateTypeEnum.PERMANENT) || requestParam.getValidDate().after(new Date())) {
                    stringRedisTemplate.delete(String.format(GOTO_SHORTLINK_IS_NULL_KEY, requestParam.getFullShortUrl()));
                }
            }
        }
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
        int serverPort = request.getServerPort();
        final String fullShortUrl = serverName + ":" + serverPort + '/' + uri;
        String originalLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl));
        if (StringUtils.isNotBlank(originalLink)) {
            ShortLinkStatsRecordDTO statsRecord = buildLinkStatsRecordAndSetUser(fullShortUrl, request, response);
            shortLinkStats(fullShortUrl, null, statsRecord);
            sendRedirect(response, originalLink);
            return;
        }
        //看看布隆过滤器有没有
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            send2Notfound(response);
            return;
        }
        String isNull = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORTLINK_IS_NULL_KEY.formatted(fullShortUrl));
        if (StringUtils.isNotBlank(isNull)) {
            send2Notfound(response);
            return;
        }
        //不命中缓存，重启缓存时开启分布式锁，这里做悲观逻辑
        RLock lock = redissonClient.getLock(RedisKeyConstant.LOCK_GOTO_SHORTLINK_KEY.formatted(fullShortUrl));
        try {
            lock.lock();
            originalLink = stringRedisTemplate.opsForValue().get(RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl));
            if (StringUtils.isNotBlank(originalLink)) {
                ShortLinkStatsRecordDTO statsRecord = buildLinkStatsRecordAndSetUser(fullShortUrl, request, response);
                shortLinkStats(fullShortUrl, null, statsRecord);
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
                send2Notfound(response);
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
                send2Notfound(response);
                return;
            }
            long linkCacheValidDate = LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate());
            if (linkCacheValidDate == -1L) {
                stringRedisTemplate.opsForValue().set(RedisKeyConstant.GOTO_SHORTLINK_IS_NULL_KEY.formatted(fullShortUrl), "-", DEFAULT_IS_NULL_DURATION);
                send2Notfound(response);
                return;
            }
            stringRedisTemplate.opsForValue().set(
                    RedisKeyConstant.GOTO_SHORTLINK_KEY.formatted(fullShortUrl),
                    shortLinkDO.getOriginUrl(),
                    linkCacheValidDate,
                    TimeUnit.MILLISECONDS);
            ShortLinkStatsRecordDTO statsRecord = buildLinkStatsRecordAndSetUser(fullShortUrl, request, response);
            shortLinkStats(fullShortUrl, null, statsRecord);
            sendRedirect(response, shortLinkDO.getOriginUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }


    private String getUVName(){
        return "uv";
    }

    private ShortLinkStatsRecordDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, ServletRequest request, ServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        AtomicReference<String> uv = new AtomicReference<>();
        Runnable addResponseCookieTask = () -> {
            uv.set(cn.hutool.core.lang.UUID.fastUUID().toString());
            Cookie uvCookie = new Cookie("uv", uv.get());
            uvCookie.setMaxAge(60 * 60 * 24 * 30);
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            ((HttpServletResponse) response).addCookie(uvCookie);
            uvFirstFlag.set(Boolean.TRUE);
            stringRedisTemplate.opsForSet().add(SHORTLINK_STATS_UV_KEY.formatted(fullShortUrl), uv.get());
        };
        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies)
                    .filter(each -> Objects.equals(each.getName(), "uv"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(each -> {
                        uv.set(each);
                        Long uvAdded = stringRedisTemplate.opsForSet().add(SHORTLINK_STATS_UV_KEY.formatted(fullShortUrl), each);
                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                    }, addResponseCookieTask);
        } else {
            addResponseCookieTask.run();
        }
        String remoteAddr = LinkUtil.getIp(((HttpServletRequest) request));
        String os = LinkUtil.getOs(((HttpServletRequest) request));
        String browser = LinkUtil.getBrowser(((HttpServletRequest) request));
        String device = LinkUtil.getDevice(((HttpServletRequest) request));
        String network = LinkUtil.getNetwork(((HttpServletRequest) request));
        Long uipAdded = stringRedisTemplate.opsForSet().add(SHORTLINK_STATS_UIP_KEY.formatted(fullShortUrl), remoteAddr);
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
        return ShortLinkStatsRecordDTO.builder()
                .fullShortUrl(fullShortUrl)
                .uv(uv.get())
                .uvFirstFlag(uvFirstFlag.get())
                .uipFirstFlag(uipFirstFlag)
                .remoteAddr(remoteAddr)
                .os(os)
                .browser(browser)
                .device(device)
                .network(network)
                .build();
    }

    @Override
    public void shortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO statsRecord) {
        Map<String, String> producerMap = Maps.newHashMapWithExpectedSize(3);
        producerMap.put("fullShortUrl", fullShortUrl);
        producerMap.put("gid", gid);
        producerMap.put("statsRecord", JSON.toJSONString(statsRecord));
        shortLinkStatsSaveProducer.send(producerMap);
    }


    private void verificationWhitelist(final String originUrl) {
        if (StringUtils.isBlank(originUrl)) {
            throw new ClientException("请输入正确的原始url");
        }
        Boolean enable = gotoDomainWhiteListProperties.getEnable();
        if (BooleanUtils.isNotTrue(enable)) {
            return;
        }
        gotoDomainWhiteListProperties.getDetails()
                .stream()
                .filter(item -> item.equals(LinkUtil.extractDomain(originUrl)))
                .findFirst()
                .orElseThrow(() -> new ClientException("该短链接不适合 ， 合适的在这里 %s ".formatted(gotoDomainWhiteListProperties.getNames())));
    }


    /**
     * 短链接基本数据统计
     */
    private void doShortLinkStates(String fullShortUrl, String gid, HttpServletRequest request, HttpServletResponse response) {
        try {
            //uv的实现
            AtomicReference<String> uvValue = new AtomicReference<>();
            Runnable createAndAddCookieTask = () -> {
                uvValue.set(UUID.randomUUID().toString());
                Cookie uvCookie = new Cookie(getUVName(), uvValue.get());
                uvCookie.setMaxAge(60 * 60 * 24 * 30);
                uvCookie.setPath(StringUtils.substring(fullShortUrl, fullShortUrl.lastIndexOf("/"), fullShortUrl.length()));
                response.addCookie(uvCookie);
                stringRedisTemplate.opsForSet().add(SHORTLINK_STATS_UV_KEY.formatted(fullShortUrl), uvValue.get());
            };
            AtomicBoolean uvFirstFlag = new AtomicBoolean(true);
            final Cookie[] cookies = request.getCookies();
            if (ObjectUtils.isNotEmpty(cookies)) {
                Arrays.stream(cookies).filter(each -> Objects.equals(each.getName(), getUVName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(each -> {
                            uvValue.set(each);
                            Long result = stringRedisTemplate.opsForSet().add(SHORTLINK_STATS_UV_KEY.formatted(fullShortUrl), each);
                            uvFirstFlag.set(ObjectUtils.compare(result, 0L) > 0);
                        }, createAndAddCookieTask);
            }else {
                createAndAddCookieTask.run();
            }
            //uip的实现
            String ip = LinkUtil.getIp(request);
            Long result = stringRedisTemplate.opsForSet().add(SHORTLINK_STATS_UIP_KEY.formatted(fullShortUrl), ip);
            boolean ipFirstFlag = ObjectUtils.compare(result, 0L) > 0;
            if (StringUtils.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO hasShortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = hasShortLinkGotoDO.getGid();
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            int hour = DateUtil.hour(now, true);
            int dayOfWeek = DateUtil.dayOfWeek(now);
            //构建基本数据对象
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uip(ipFirstFlag ? 1 : 0)
                    .uv(uvFirstFlag.get() ? 1 : 0)
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .weekday(dayOfWeek)
                    .hour(hour)
                    .date(now)
                    .build();
            linkAccessStatsMapper.shortLinkStates(linkAccessStatsDO);

            //locale的 地区的实现
            //调用高德的key
            String province = "unknown";
            String city = "unknown";
            String json = gaoDeUtil.sendHttpRequest2GaodeMap(ip);
            JSONObject jsonObject = JSON.parseObject(json);
            String infocode = jsonObject.getString("infocode");
            if (gaoDeUtil.isSuccess(infocode)) {
                LinkLocaleStatsDO localeStatsDO = LinkLocaleStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(now)
                        .province(province = gaoDeUtil.handleGaoDeApiRespString(jsonObject.getString("province")))
                        .city(city = gaoDeUtil.handleGaoDeApiRespString(jsonObject.getString("city")))
                        .adcode(gaoDeUtil.handleGaoDeApiRespString(jsonObject.getString("adcode")))
                        .cnt(1)
                        .country("中国")
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleStates(localeStatsDO);
            }
            //os的 操作系统的实现
            String os = LinkUtil.getOs(request);
            LinkOsStatsDO osStatsDO = LinkOsStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(now)
                    .os(os)
                    .cnt(1)
                    .build();
            linkOsStatsMapper.shortLinkOsStates(osStatsDO);

            String browser = LinkUtil.getBrowser(request);
            //浏览器的实现
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .browser(browser)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);
            String device = LinkUtil.getDevice(request);
            //设备的实现
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .device(device)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);
            String network = LinkUtil.getNetwork(request);
            //network 网络的实现
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .network(network)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .build();
            linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);

            //logs的实现
            LinkAccessLogsDO linkLogsDO = LinkAccessLogsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .user(uvValue.get())
                    .gid(gid)
                    .ip(ip)
                    .os(os)
                    .browser(browser)
                    .network(network)
                    .device(device)
                    .locale(String.join("-", "中国", province, city))
                    .build();
            linkAccessLogsMapper.insert(linkLogsDO);

            Long todayUvAdded = stringRedisTemplate.opsForSet().add(SHORTLINK_STATS_TODAY_UV_KEY.formatted(dateFormat.format(now), fullShortUrl),uvValue.get());
            Long todayUipAdded = stringRedisTemplate.opsForSet().add(SHORTLINK_STATS_TODAY_UIP_KEY.formatted(dateFormat.format(now), fullShortUrl), ip);
            boolean todayFirstUvFlag = ObjectUtils.compare(todayUvAdded, 0L) > 0;
            boolean todayFirstUipFlag = ObjectUtils.compare(todayUipAdded, 0L) > 0;
            //today的实现
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .date(now)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .todayPv(1)
                    .todayUv(todayFirstUvFlag ? 1 : 0)
                    .todayUip(todayFirstUipFlag ? 1 : 0)
                    .build();

            linkStatsTodayMapper.shortLinkTodayStates(linkStatsTodayDO);

            //历史记录+1
            baseMapper.incrementStats(gid, fullShortUrl, 1, uvFirstFlag.get() ? 1 : 0, ipFirstFlag ? 1 : 0);

        } catch (Exception ex) {
            log.error("执行短链接基本数据统计时报错 ：fullShortUrl ：{}， gid : {}", fullShortUrl, gid, ex);
        }
    }

    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        List<ShortLinkBaseInfoRespDTO> result = new ArrayList<>();
        for (int i = 0; i < originUrls.size(); i++) {
            ShortLinkCreateReqDTO shortLinkCreateReqDTO = BeanUtil.toBean(requestParam, ShortLinkCreateReqDTO.class);
            shortLinkCreateReqDTO.setOriginUrl(originUrls.get(i));
            shortLinkCreateReqDTO.setDescribe(describes.get(i));
            try {
                ShortLinkCreateRespDTO shortLink = createShortLink(shortLinkCreateReqDTO);
                ShortLinkBaseInfoRespDTO linkBaseInfoRespDTO = ShortLinkBaseInfoRespDTO.builder()
                        .fullShortUrl(shortLink.getFullShortUrl())
                        .originUrl(shortLink.getOriginUrl())
                        .describe(describes.get(i))
                        .build();
                result.add(linkBaseInfoRespDTO);
            } catch (Throwable ex) {
                log.error("批量创建短链接失败，原始参数：{}", originUrls.get(i));
            }
        }
        return ShortLinkBatchCreateRespDTO.builder()
                .total(result.size())
                .baseLinkInfos(result)
                .build();
    }

    /**
     * 重定向到找不到页面
     */
    private void send2Notfound(HttpServletResponse response) {
        sendRedirect(response, "/page/notfound");
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
            String factor = UUID.randomUUID().toString();
            String originUrl = requestParam.getOriginUrl() + factor;
            shortLinkSuffix = HashUtil.hashToBase62(originUrl);
        } while (shortUriCreateCachePenetrationBloomFilter.contains(defaultDomain + "/" + shortLinkSuffix));
        return shortLinkSuffix;
    }


    /**
     * 获取网站的favicon图标
     * @param url 网站url
     */
    @SneakyThrows
    private String getFavicon(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) (targetUrl.openConnection());
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(HttpMethod.GET.name());
        connection.connect();

        //301 or 302
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_MOVED_PERM == responseCode || HttpURLConnection.HTTP_MOVED_TEMP == responseCode) {
            //获取重定向的url对象
            String redirectUrl = connection.getHeaderField("Location");
            if (StringUtils.isNotBlank(redirectUrl)){
                URL newUrl = new URL(redirectUrl);
                connection = (HttpURLConnection) newUrl.openConnection();
                connection.setRequestMethod(HttpMethod.GET.name());
                connection.connect();
                responseCode = connection.getResponseCode();
            }
        }
        // 200
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (null != faviconLink) {
                return faviconLink.attr("abs:href");
            }

        }
        return null;
    }
}
