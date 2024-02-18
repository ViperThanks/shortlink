package github.viperthanks.shortlink.admin.dto.req;

import lombok.Data;

/**
 * desc: 用户注册接受对象
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@Data
public class UserUpdateReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
