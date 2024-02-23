package github.viperthanks.shortlink.admin.dto.req;

import lombok.Data;

/**
 * desc: 短链接组排序请求参数
 *
 * @author Viper Thanks
 * @since 20/2/2024
 */
@Data
public class ShortLinkGroupSortReqDTO {
    /**
     * 短链接组标识
     */
    private String gid;
    /**
     * 排序
     */
    private Integer sortOrder;
}
