package github.viperthanks.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * desc: 短链接创建dto
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkCreateRespDTO {

    /**
     * 分组信息
     */
    private String gid;
    /**
     * 原始链接
     */
    private String originUrl;
    /**
     * 短链接
     */
    private String fullShortUrl;

}
