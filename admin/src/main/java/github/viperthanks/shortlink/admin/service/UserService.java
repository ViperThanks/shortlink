package github.viperthanks.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.viperthanks.shortlink.admin.dao.entity.UserDO;
import github.viperthanks.shortlink.admin.dto.req.*;
import github.viperthanks.shortlink.admin.dto.resp.UserLoginRespDTO;
import github.viperthanks.shortlink.admin.dto.resp.UserRespDTO;

/**
 * desc:用户接口层
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户
     */
    UserRespDTO getUserByUsername(final String username);

    /**
     * 检查用户名是否存在，如果存在返回True
     */
    Boolean hasUsername(final String username);

    /**
     * 注册用户
     */
    void register(final UserRegisterReqDTO requestParam);

    /**
     * 更新用户通过用户名
     */
    void update(final UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     *
     * @param requestParam 用户登录请求dto
     */
    UserLoginRespDTO login(final UserLoginReqDTO requestParam);

    /**
     * 检查用户是否已经登录
     */
    Boolean checkLogin(final UserCheckLoginReqDTO requestParam);

    /**
     * 登出功能
     * @param requestParam
     */
    void logout(UserLogoutReqDTO requestParam);
}
