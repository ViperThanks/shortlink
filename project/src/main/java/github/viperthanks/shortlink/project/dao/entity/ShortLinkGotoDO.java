package github.viperthanks.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 28/2/2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_link_goto")
public class ShortLinkGotoDO {
    /**
     * id
     */
    private Long id;
    /**
     * gid
     */
    private String gid;
    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
