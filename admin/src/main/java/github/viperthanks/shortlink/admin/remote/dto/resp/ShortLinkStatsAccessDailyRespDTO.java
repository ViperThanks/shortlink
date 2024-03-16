package github.viperthanks.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * desc: 短链接基本访问属性响应题
 *
 * @author Viper Thanks
 * @since 8/3/2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkStatsAccessDailyRespDTO {

    /**
     * 日期
     */
    private String date;

    /**
     * 访问量
     */
    private Integer pv;

    /**
     * 独立访客量
     */
    private Integer uv;

    /**
     * 独立ip数
     */
    private Integer uip;
}
