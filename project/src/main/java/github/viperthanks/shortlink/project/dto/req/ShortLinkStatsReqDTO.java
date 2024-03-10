package github.viperthanks.shortlink.project.dto.req;

import lombok.Data;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 8/3/2024
 */
@Data
public class ShortLinkStatsReqDTO {
    /**
     * 完整短链接
     */
    private String fullShortUrl;
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
