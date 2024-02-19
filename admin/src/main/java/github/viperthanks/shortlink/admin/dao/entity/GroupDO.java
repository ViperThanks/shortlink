package github.viperthanks.shortlink.admin.dao.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import github.viperthanks.shortlink.admin.common.convention.database.BaseDO;
import lombok.*;

/**
 * desc:短链接分组实体
 *
 * @author Viper Thanks
 * @since 19/2/2024
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_group")
public class GroupDO extends BaseDO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建组织的用户名
     */
    private String username;
}