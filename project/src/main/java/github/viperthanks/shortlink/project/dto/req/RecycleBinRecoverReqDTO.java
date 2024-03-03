package github.viperthanks.shortlink.project.dto.req;

import lombok.Data;

/**
 * desc: 回收站恢复请求对象
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@Data
public class RecycleBinRecoverReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接全称
     */
    private String fullShortUrl;
}
