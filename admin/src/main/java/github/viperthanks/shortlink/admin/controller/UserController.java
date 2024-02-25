package github.viperthanks.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import github.viperthanks.shortlink.admin.dto.req.*;
import github.viperthanks.shortlink.admin.dto.resp.UserActualRespDTO;
import github.viperthanks.shortlink.admin.dto.resp.UserLoginRespDTO;
import github.viperthanks.shortlink.admin.dto.resp.UserRespDTO;
import github.viperthanks.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * desc: 用户管理控制层
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    /**
     * 根据用户名获取用户
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable(value = "username") String username) {
        UserRespDTO result = userService.getUserByUsername(username);

        return Results.success(result);
    }

    /**
     * 根据用户名获取用户 （不脱敏）
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable(value = "username") String username) {
        UserRespDTO userRespDTO = userService.getUserByUsername(username);
        return Results.success(BeanUtil.toBean(userRespDTO, UserActualRespDTO.class));
    }

    /**
     * 是否存在该用户名
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/has-username/{username}")
    public Result<Boolean> hasUsername(@PathVariable(value = "username") String username) {
        Boolean result = userService.hasUsername(username);
        return Results.success(result);
    }

    /**
     * 注册用户
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/user", method = RequestMethod.POST)
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 更新用户
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/user", method = RequestMethod.PUT)
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/user/login", method = RequestMethod.POST)
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 用户登出
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/user/logout", method = RequestMethod.POST)
    public Result<Void> logout(@RequestBody UserLogoutReqDTO requestParam) {
        userService.logout(requestParam);
        return Results.success();
    }

    /**
     * 检查用户是否登录
     */
    @RequestMapping(value = "/api/shortlink/admin/v1/user/check-login", method = RequestMethod.POST)
    public Result<Boolean> checkLogin(@RequestBody UserCheckLoginReqDTO requestParam) {
        return Results.success(userService.checkLogin(requestParam));
    }





}
