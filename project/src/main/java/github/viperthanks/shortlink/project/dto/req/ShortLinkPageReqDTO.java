package github.viperthanks.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkDO;
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
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {
    private String gid;
    /**
     * 排序标签
     */
    private String orderTag;
}
