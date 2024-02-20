package github.viperthanks.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.admin.common.constant.RedisCacheConstant;
import github.viperthanks.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.convention.exception.ServiceException;
import github.viperthanks.shortlink.admin.common.enums.UserErrorCodeEnum;
import github.viperthanks.shortlink.admin.dao.entity.UserDO;
import github.viperthanks.shortlink.admin.dao.mapper.UserMapper;
import github.viperthanks.shortlink.admin.dto.req.*;
import github.viperthanks.shortlink.admin.dto.resp.UserLoginRespDTO;
import github.viperthanks.shortlink.admin.dto.resp.UserRespDTO;
import github.viperthanks.shortlink.admin.service.UserService;
import github.viperthanks.shortlink.admin.toolkit.SQLResultHelper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * desc: 用户 业务层实现
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;
    @Override

    public UserRespDTO getUserByUsername(final String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(StringUtils.isNotBlank(username), UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(userDO)) {
            throw new ClientException(UserErrorCodeEnum.USER_NOT_EXIST);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    /**
     * 检查用户名是否存在，如果存在返回True
     */
    @Override
    public @Nonnull Boolean hasUsername(final String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    /**
     * 注册用户
     */
    @Override
    public void register(final UserRegisterReqDTO requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new ClientException(BaseErrorCode.CLIENT_ERROR);
        }
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.getCacheKey(RedisCacheConstant.LOCK_USER_REGISTER_KEY, requestParam.getUsername()));
        try {
            if (!lock.tryLock()) {
                throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
            }
            int effectRow = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
            if (effectRow < 1) {
                throw new ServiceException(UserErrorCodeEnum.USER_SAVE_ERROR);
            }
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 更新用户
     */
    @Override
    public void update(final UserUpdateReqDTO requestParam) {
        //todo 验证用户是否为登录用户
        LambdaUpdateWrapper<UserDO> lambdaUpdateWrapper = Wrappers.lambdaUpdate(UserDO.class).eq(UserDO::getUsername, requestParam.getUsername());
        int effectRow = baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), lambdaUpdateWrapper);
        if (SQLResultHelper.isIllegalDMLResult(effectRow)) {
            throw new ServiceException(UserErrorCodeEnum.USER_UPDATE_ERROR);
        }
    }

    /**
     * 用户登录
     * todo 基于jwt重构
     * @param requestParam 用户登录请求dto
     */
    @Override
    public UserLoginRespDTO login(final UserLoginReqDTO requestParam) {
        if (StringUtils.isBlank(requestParam.getUsername())) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_ERROR);
        }
        if (StringUtils.isBlank(requestParam.getPassword())) {
            throw new ClientException(BaseErrorCode.PASSWORD_SHORT_ERROR);
        }
        final LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getDelFlag, 0)
                .eq(UserDO::getPassword, requestParam.getPassword());
        final UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(userDO)) {
            throw new ClientException(UserErrorCodeEnum.USER_NOT_EXIST);
        }
        String key = RedisCacheConstant.LOGIN_USER_PREFIX + requestParam.getUsername();
        if (BooleanUtils.isTrue(stringRedisTemplate.hasKey(key))) {
            throw new ClientException("用户已经登录");
        }
        final String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(key, uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(key, 30, TimeUnit.DAYS);
        return new UserLoginRespDTO(uuid);
    }

    /**
     * 检查用户是否已经登录
     */
    @Override
    public Boolean checkLogin(final UserCheckLoginReqDTO requestParam) {
        if (StringUtils.isAnyBlank(requestParam.getUsername(), requestParam.getToken())) {
            return false;
        }
        String key = RedisCacheConstant.LOGIN_USER_PREFIX + requestParam.getUsername();
        Object userDOJson = stringRedisTemplate.opsForHash().get(key, requestParam.getToken());
        if (ObjectUtils.isEmpty(userDOJson)) {
            return false;
        }
        try {
            UserDO userDO = JSON.parseObject((String) userDOJson, UserDO.class);
            if (ObjectUtils.isNotEmpty(userDO)) {
                return true;
            }
        } catch (Exception e) {
            log.error("[用户] -> [检查登录] - 用户名 ：{} 存在异常的key : {} ", requestParam.getUsername(), key);
            return false;
        }
        return false;
    }

    @Override
    public void logout(UserLogoutReqDTO requestParam) {
        if (StringUtils.isAnyBlank(requestParam.getUsername(), requestParam.getToken())) {
            throw new ClientException("参数错误");
        }
        if (checkLogin(BeanUtil.toBean(requestParam, UserCheckLoginReqDTO.class))) {
            stringRedisTemplate.delete(RedisCacheConstant.LOGIN_USER_PREFIX + requestParam.getUsername());
        }else {
            throw new ClientException("用户Token不存在或用户未登录");
        }
    }
}
