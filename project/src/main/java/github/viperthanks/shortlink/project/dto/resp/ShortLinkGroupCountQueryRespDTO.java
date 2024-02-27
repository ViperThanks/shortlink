package github.viperthanks.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * desc: 短链接分组查询数量返回值对象
 *
 * @author Viper Thanks
 * @since 27/2/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkGroupCountQueryRespDTO {
    /**
     * 分组标识
     */
    private String gid;
    /**
     * 短链接数量
     */
    private Integer shortLinkCount;
}
