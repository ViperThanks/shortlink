package github.viperthanks.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * desc: 用户返回参数响应
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@Data
public class UserRespDTO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

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


