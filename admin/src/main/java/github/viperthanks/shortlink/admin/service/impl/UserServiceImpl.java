package github.viperthanks.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.admin.common.convention.exception.ClientException;
import github.viperthanks.shortlink.admin.common.enums.UserErrorCodeEnum;
import github.viperthanks.shortlink.admin.dao.entity.UserDO;
import github.viperthanks.shortlink.admin.dao.mapper.UserMapper;
import github.viperthanks.shortlink.admin.dto.resp.UserRespDTO;
import github.viperthanks.shortlink.admin.service.UserService;
import org.apache.commons.lang3.StringUtils;
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
public class UserServiceImpl extends ServiceImpl<UserMapper,UserDO> implements UserService {
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
}
