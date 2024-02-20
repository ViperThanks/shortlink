package github.viperthanks.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * desc: 短链接分组返回值
 *
 * @author Viper Thanks
 * @since 19/2/2024
 */
@Data
public class ShortLinkGroupRespDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建组织的用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}
