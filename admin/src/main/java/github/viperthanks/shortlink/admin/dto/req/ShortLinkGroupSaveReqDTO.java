package github.viperthanks.shortlink.admin.dto.req;

import lombok.Data;

/**
 * desc: 短链接组保存请求参数
 *
 * @author Viper Thanks
 * @since 19/2/2024
 */
@Data
public class ShortLinkGroupSaveReqDTO {

    /**
     * 分组名称
     */
    private String name;
}
