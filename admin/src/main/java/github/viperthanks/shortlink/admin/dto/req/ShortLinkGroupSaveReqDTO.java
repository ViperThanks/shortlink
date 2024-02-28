package github.viperthanks.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * desc: 短链接组保存请求参数
 *
 * @author Viper Thanks
 * @since 19/2/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkGroupSaveReqDTO {

    /**
     * 分组名称
     */
    private String name;

    /**
     * 用户名
     */
    private String username;
}
