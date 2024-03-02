package github.viperthanks.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * desc:短链接回收站分页请求参数
 *
 * @author Viper Thanks
 * @since 25/2/2024
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShortLinkRecycleBinPageReqDTO extends Page {
    private List<String> gidList;
}
