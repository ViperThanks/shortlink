package github.viperthanks.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * desc:短链接分页请求参数
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkPageReqDTO extends Page {
    private String gid;
}
