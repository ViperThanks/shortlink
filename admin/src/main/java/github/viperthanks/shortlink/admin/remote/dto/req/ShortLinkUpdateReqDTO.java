package github.viperthanks.shortlink.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import github.viperthanks.shortlink.admin.common.constant.JsonConstants;
import github.viperthanks.shortlink.admin.common.enums.ValidDateTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * desc: 短链接更新请求dto
 *
 * @author Viper Thanks
 * @since 28/2/2024
 */
@Data
public class ShortLinkUpdateReqDTO {

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始分组标识
     */
    private String originGid;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private ValidDateTypeEnum validDateType;

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
