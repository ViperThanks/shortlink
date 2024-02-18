package github.viperthanks.shortlink.admin.dto.req;

import lombok.Data;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 18/2/2024
 */
@Data
public class UserLogoutReqDTO {
    private String token;
    private String username;
}
