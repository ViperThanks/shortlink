package github.viperthanks.shortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * desc: 用户登录响应DTO
 *
 * @author Viper Thanks
 * @since 18/2/2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRespDTO {
    private String token;
}
