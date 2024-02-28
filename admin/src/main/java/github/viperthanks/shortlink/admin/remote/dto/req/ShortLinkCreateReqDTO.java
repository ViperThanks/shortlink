package github.viperthanks.shortlink.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import github.viperthanks.shortlink.admin.common.constant.JsonConstants;
import lombok.Data;

import java.util.Date;

/**
 * desc: 短链接创建dto
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@Data
public class ShortLinkCreateReqDTO {

    /**
     * 域名
     */
    private String domain;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 创建类型 0：控制台 1：接口
     */
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;


    /**
     * 有效期
     */
    @JsonFormat(pattern = JsonConstants.DEFAULT_PATTERN, timezone = JsonConstants.DEFAULT_TIMEZONE)
    private Date validDate;

    /**
     * 描述
     */
    private String describe;

}
