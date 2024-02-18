package github.viperthanks.shortlink.admin.dto.req;

import lombok.Data;

/**
 * desc:用户登录请求DTO
 *
 * @author Viper Thanks
 * @since 18/2/2024
 */
@Data
public class UserLoginReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
