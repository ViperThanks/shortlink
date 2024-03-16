package github.viperthanks.shortlink.project.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import github.viperthanks.shortlink.project.common.constant.JsonConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * desc: 短链接访客记录返回响应体
 *
 * @author Viper Thanks
 * @since 16/3/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsAccessRecordRespDTO {

    /**
     * 访问时间
     */
    @JsonFormat(pattern = JsonConstants.DEFAULT_MINUTE_PATTERN, timezone = JsonConstants.DEFAULT_TIMEZONE)
    private Date createTime;
    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;
    /**
     * ip
     */
    private String ip;
    /**
     * 网络
     */
    private String network;
    /**
     * 地区字段
     */
    private String locale;
    /**
     * 设备
     */
    private String device;
    /**
     * 访客类型
     */
    private String uvType;


}
