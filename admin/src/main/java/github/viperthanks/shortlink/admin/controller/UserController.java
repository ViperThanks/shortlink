package github.viperthanks.shortlink.admin.controller;

import github.viperthanks.shortlink.admin.common.convention.result.Result;
import github.viperthanks.shortlink.admin.common.convention.result.Results;
import github.viperthanks.shortlink.admin.common.enums.UserErrorCodeEnum;
import github.viperthanks.shortlink.admin.dto.resp.UserRespDTO;
import github.viperthanks.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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
    public Result<?> getUserByUsername(@PathVariable(value = "username") String username) {
        UserRespDTO result = userService.getUserByUsername(username);
        if (Objects.isNull(result)) {
            return Results.failure(UserErrorCodeEnum.USER_NOT_EXIST);
        }
        return Results.success(result);
    }

}
