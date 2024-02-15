package github.viperthanks.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.admin.common.constant.RedisCacheConstant;
import github.viperthanks.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.convention.exception.ServiceException;
import github.viperthanks.shortlink.admin.common.enums.UserErrorCodeEnum;
import github.viperthanks.shortlink.admin.dao.entity.UserDO;
import github.viperthanks.shortlink.admin.dao.mapper.UserMapper;
import github.viperthanks.shortlink.admin.dto.req.UserRegisterReqDTO;
import github.viperthanks.shortlink.admin.dto.resp.UserRespDTO;
import github.viperthanks.shortlink.admin.service.UserService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * desc: 用户 业务层实现
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper,UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;
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
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername());
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
}
