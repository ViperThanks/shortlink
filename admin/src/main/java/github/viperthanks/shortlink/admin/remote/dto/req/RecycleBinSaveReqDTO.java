package github.viperthanks.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * desc: 保存到垃圾桶请求对象
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@Data
public class RecycleBinSaveReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接全称
     */
    private String fullShortUrl;
}
