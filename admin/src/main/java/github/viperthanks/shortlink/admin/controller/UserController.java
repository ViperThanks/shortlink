package github.viperthanks.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import github.viperthanks.shortlink.admin.dto.req.UserRegisterReqDTO;
import github.viperthanks.shortlink.admin.dto.resp.UserActualRespDTO;
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
    @RequestMapping(value = "/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable(value = "username") String username) {
        UserRespDTO result = userService.getUserByUsername(username);

        return Results.success(result);
    }

    /**
     * 根据用户名获取用户 （不脱敏）
     */
    @RequestMapping(value = "/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable(value = "username") String username) {
        UserRespDTO userRespDTO = userService.getUserByUsername(username);
        return Results.success(BeanUtil.copyProperties(userRespDTO, UserActualRespDTO.class));
    }

    /**
     * 是否存在该用户名
     */
    @RequestMapping(value = "/api/shortlink/v1/hasUsername/{username}")
    public Result<Boolean> hasUsername(@PathVariable(value = "username") String username) {
        Boolean result = userService.hasUsername(username);
        return Results.success(result);
    }

    /**
     * 注册用户
     */
    @RequestMapping(value = "/api/shortlink/v1/register", method = RequestMethod.POST)
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

}
