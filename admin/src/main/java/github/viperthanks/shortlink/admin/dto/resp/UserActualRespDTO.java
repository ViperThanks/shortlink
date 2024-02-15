package github.viperthanks.shortlink.admin.dto.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * desc: 用户返回参数响应 （不脱敏）
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserActualRespDTO extends UserRespDTO {
    private String phone;
}
