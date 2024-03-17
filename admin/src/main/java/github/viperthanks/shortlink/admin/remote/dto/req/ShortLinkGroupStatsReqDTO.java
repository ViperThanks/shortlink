package github.viperthanks.shortlink.admin.remote.dto.req;

import lombok.Data;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 8/3/2024
 */
@Data
public class ShortLinkGroupStatsReqDTO {
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
